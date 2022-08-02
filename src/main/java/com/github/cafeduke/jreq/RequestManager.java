package com.github.cafeduke.jreq;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.github.cafeduke.common.Util;
import com.github.cafeduke.jreq.ArgProcessor.HttpMethod;
import com.github.cafeduke.jreq.ArgProcessor.MultiThreadMode;

/**
 * RequestManager manages one or more sequential/simultaneous requests to be spawned.
 * Each request is encapsulated as a SingleClient. Hence, RequestMananger handles creation
 * and synchronization of multiple SingleClients.
 * 
 * @author Raghunandan.Seshadri
 */
public class RequestManager implements Runnable
{
    /**
     * Maximum number of attempts after which all threads will be expected to be
     * in the waiting state.
     */
    private static final int MAX_ATTEMPT_TO_SYNC_THREAD = 6000;

    /**
     * Time (in milliseconds) to be slept after every attempt to check if number
     * of threads sleeping is equal to actual number of threads spawned.
     */
    private static final int SLEEP_AFTER_EACH_ATTEMPT = 10;

    /**
     * Raw arguments for sending request
     */
    private String arg[] = null;

    /**
     * Processed argument object.
     */
    private ArgProcessor cmdArg = null;

    /**
     * Unique ID for each JReq client.
     */
    private String clientId = null;

    /**
     * An array of SingleClients. Each SingleClient abstracts a request.
     * The RequestManager should create/manage the SingleClients.
     */
    private SingleClient webClient[] = null;

    /**
     * Executor service
     */
    private ExecutorService executor = null;

    /**
     * Java Logger
     */
    private Logger logger = null;

    /**
     * Reference to context
     */
    private JReq.Context context = null;

    /**
     * Create a request manager instance.
     * 
     * @param context JReq context
     * @param arg JReq request arguments.
     */
    public RequestManager(JReq.Context context, String arg[])
    {
        this.context = context;
        this.arg = arg;
        this.clientId = context.getClientId();
        this.logger = context.getLogger();
    }

    /**
     * Array of response codes corresponding to the requests.
     * 
     * @return Response code for the sent request. If non blocking APIs were used
     *         the array can be null.
     */
    public int[] getResponseCode()
    {
        if (webClient == null)
            return null;

        int respCode[] = new int[webClient.length];

        for (int index = 0; index < respCode.length; ++index)
            respCode[index] = (webClient[index] == null) ? -1 : webClient[index].getResponseCode();

        return respCode;
    }

    /**
     * Send request and return an array of response codes corresponding to the requests.
     * 
     * <br>
     * If the request is blocking the main thread itself will take care of
     * spawning multiple SingleClients and capturing the response.
     * 
     * <br>
     * If the request has to be non-blocking a separate thread is created by main thread
     * which will take care of spawning multiple SingleClients and capturing the response.
     * The main thread will return.
     * 
     * @return An array of response codes corresponding to the requests. If non blocking
     *         APIs were used the array can be null.
     */
    public int[] sendRequest()
    {
        try
        {
            this.cmdArg = new ArgProcessor(arg);
            if (!cmdArg.processArg())
                return getResponseCode();

            if (this.cmdArg.blockRequest)
            {
                run();
            }
            else
            {
                Thread t = new Thread(this);
                t.start();
            }
        }
        catch (Exception e)
        {
            if (cmdArg.enableErrorLog)
            {
                System.out.println("Exception: " + e.toString());
                System.out.println("Refer log for details.");
                logger.log(Level.SEVERE, "Exception", e);
            }
        }
        return getResponseCode();
    }

    @Override
    public void run()
    {
        try
        {
            /* Accept Header */
            cmdArg.mapHeaderValue.put("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");

            /* Authorization Header */
            if (cmdArg.login != null && cmdArg.password != null)
            {
                String bareString = cmdArg.login + ":" + cmdArg.password;
                String encodedString = new String(Base64.encodeBase64(bareString.getBytes()));
                String basicAuthHeaderValue = "Basic " + encodedString;
                cmdArg.mapHeaderValue.put("Authorization", basicAuthHeaderValue);
            }

            /* Unique request header */
            if (cmdArg.sendClientId)
            {
                cmdArg.mapHeaderValue.put(JReq.CLIENT_ID_HEADER, clientId);
                logger.fine("InstanceId Header = " + clientId);
            }

            if (cmdArg.multiThreadMode == MultiThreadMode.SC)
            {
                if (cmdArg.fileURI == null)
                {
                    webClient = new SingleClient[] { new SingleClient(context, cmdArg) };
                    webClient[0].run();
                    logReponseCode();
                }
                else
                {
                    List<String> listURI = FileUtils.readLines(cmdArg.fileURI, Charset.defaultCharset());
                    createSingleClients(listURI.size());
                    for (int index = 0; index < webClient.length; ++index)
                    {
                        webClient[index].setURI(listURI.get(index));
                        webClient[index].run();
                    }
                    logResponseCodeURI(listURI);
                }
            }
            else if (cmdArg.multiThreadMode == MultiThreadMode.MSC)
            {
                createSingleClients(cmdArg.threadCount);
                syncParallelSingleClients();
                logReponseCode();
            }
            else if (cmdArg.multiThreadMode == MultiThreadMode.MUC)
            {
                List<String> listURI = FileUtils.readLines(cmdArg.fileURI, Charset.defaultCharset());
                createSingleClients(listURI.size());
                for (int index = 0; index < webClient.length; ++index)
                    webClient[index].setURI(listURI.get(index));
                syncParallelSingleClients();
                logResponseCodeURI(listURI);
            }

            writeAggregateMetaData();
        }
        catch (Exception e)
        {
            if (cmdArg.enableErrorLog)
            {
                System.out.println("Exception: " + e.toString());
                System.out.println("Refer log for details.");
                logger.log(Level.SEVERE, "Exception", e);
            }
        }
    }

    /**
     * Write the aggregation of the meta data obtained from SingleClients.
     */
    private void writeAggregateMetaData() throws IOException
    {
        if (!cmdArg.recordMetaData)
            return;

        Properties propAvgMeta = new Properties();

        String key[] = new String[] { "RequestDataSendDuration", "ResponseDataReceiveDuration" };
        long avg[] = new long[key.length];

        for (int i = 0; i < webClient.length; ++i)
        {
            for (int j = 0; j < key.length; ++j)
                avg[j] += webClient[i].getIntegerMetaData(key[j]);
        }

        for (int i = 0; i < key.length; ++i)
        {
            avg[i] = avg[i] / webClient.length;
            propAvgMeta.setProperty(key[i], "" + avg[i]);
        }

        propAvgMeta.store(new FileWriter("jreq.avg.properties"), "Connection Average Meta Data");
    }

    /**
     * Log the obtained response codes. If the response code arry size is greater
     * than one then use below given log format.
     * <br>
     * {@code <request range begin>-<request range end>=<response code>}
     */
    private void logReponseCode() throws IOException
    {
        int respCode[] = getResponseCode();
        if (respCode == null || respCode.length == 0)
            return;

        String message = null;

        if (respCode.length == 1)
            message = "ResponseCode=" + respCode[0];
        else
        {
            List<String> listRange = new ArrayList<String>();

            int rangeBegin = -1, rangeRespCode = -1;
            for (int i = 0; i < respCode.length; ++i)
            {
                if (respCode[i] != rangeRespCode)
                {
                    if (rangeRespCode != -1)
                        listRange.add(rangeBegin + "-" + i + "=" + rangeRespCode);
                    rangeRespCode = respCode[i];
                    rangeBegin = (i + 1);
                }
            }
            listRange.add(rangeBegin + "-" + respCode.length + "=" + rangeRespCode);
            message = "RequestRange-ResponseCode" + Util.LineSep;
            message = message + listRange.toString().replaceAll("[\\[\\]]", "").replaceAll(", ", Util.LineSep);
        }
        logger.info(message);
        writeRespCode(message);
    }

    /**
     * Log response codes from multiple unique client (MUC) requests.
     * 
     * @param respCode Array of response codes corresponding to the requests.
     * @param listURI List of URIs to be requested.
     */
    private void logResponseCodeURI(List<String> listURI) throws IOException
    {
        int respCode[] = getResponseCode();
        if (respCode == null || respCode.length == 0)
            return;

        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < respCode.length; ++i)
            buffer.append(listURI.get(i) + ", ResponseCode=" + respCode[i] + Util.LineSep);
        logger.info(buffer.toString());
        writeRespCode(buffer.toString());
    }

    /**
     * If response code file is specified, write {@code message} having
     * response codes to file.
     * 
     * @param message
     * @throws IOException
     */
    private void writeRespCode(String message) throws IOException
    {
        if (cmdArg.fileRespCode == null)
            return;

        PrintWriter out = new PrintWriter(new FileWriter(cmdArg.fileRespCode));
        out.println(message);
        out.close();
    }

    /**
     * Creates multiple SingleClients and assigns it with appropriate resources.
     * <ul>
     * <li>Each SingleClient can have its own post body file and request header file.
     * <li>Each SingleClient will have its own output file and output header file.
     * </ul>
     * 
     * <br>
     * Handling of output file
     * <ul>
     * <li>If output file is specified, it is appended with {@code <SingleClientIndex>.html}
     * <li>If header output file is specified, it is appended with {@code <SingleClientIndex>.html}
     * </ul>
     * 
     * <br>
     * Handling of post-body/request-header files
     * <ul>
     * <li>If post-body-content is specified, all SingleClients will use the same
     * <li>If one post-body/request-header file is specified, all SingleClients will use the same.
     * <li>If count > number of post-body/request-header files, first count number of
     * SingleClients will use a unique post-body/request-header file, others use none.
     * </ul>
     * 
     * @param cmdArg An instance of command line arguments
     * @param count Number of SingleClients to create
     * @throws MalformedURLException
     */
    private void createSingleClients(int count) throws MalformedURLException
    {
        webClient = new SingleClient[count];

        for (int i = 0; i < count; i++)
        {
            webClient[i] = new SingleClient(context, cmdArg);

            String currClientId = String.format("%04d", (i + 1));

            if (cmdArg.outputFile != null)
                webClient[i].setFileResponse(new File(cmdArg.outputFile + currClientId + ".out"));

            if (cmdArg.outputFileHeader != null)
                webClient[i].setFileResponseHeader(new File(cmdArg.outputFileHeader + currClientId + ".out"));

            if (cmdArg.httpMethod == HttpMethod.POST)
            {
                if (cmdArg.postBody != null)
                    webClient[i].setPostBody(cmdArg.postBody);
                else if (cmdArg.listPostBodyFile.size() == 1)
                    webClient[i].setFilePostBody(cmdArg.listPostBodyFile.get(0));
                else if (i < cmdArg.listPostBodyFile.size())
                    webClient[i].setFilePostBody(cmdArg.listPostBodyFile.get(i));
                else
                    webClient[i].setPostBody("");
            }

            if (cmdArg.listHeader.size() > 0)
                webClient[i].setRequestHeader(cmdArg.listHeader);
            else if (cmdArg.listRequestHeaderFile.size() > 0)
            {
                if (cmdArg.listRequestHeaderFile.size() == 1)
                    webClient[i].setFileRequestHeader(cmdArg.listRequestHeaderFile.get(0));
                else if (i < cmdArg.listRequestHeaderFile.size())
                    webClient[i].setFileRequestHeader(cmdArg.listRequestHeaderFile.get(i));
            }
        }
    }

    /**
     * Synchronizes all the SingleClients - Ensures all the SingleClients are in a wait
     * state and then releases (notifies) them all. This ensures parallelism.
     * 
     * The sequence of events are as follows:
     * <ul>
     * <li>Create a thread for every {@code webClient} instance.
     * <li>Create a SyncObject and assign the same for every runnable SingleClient instance.
     * <li>Start all threads
     * <ul>
     * <li>Wait until all threads come to a wait state.
     * <li>If all threads do not reach wait state even after max timeout throw IllegalStateException
     * </ul>
     * <li>Create a SingleClient and assign the same SyncObject
     * <li>Create a notifier thread for the above runnable SingleClient.
     * <li>Start notifier thread. This will notify all the waiting threads.
     * <li>Wait for all threads to complete
     * <ul>
     * <li>Wait for {@code timeoutConn} milliseconds for all threads to complete execution.
     * <li>If any thread is still alive, interrupt it.
     * </ul>
     * <li>Record response code from each thread.
     * </ul>
     * 
     * @param cmdArg An instance of command line arguments
     * @param webClient An array of SingleClient, each having its own request properties.
     * @throws InterruptedException
     * @throws MalformedURLException
     */
    private void syncParallelSingleClients() throws InterruptedException, MalformedURLException
    {
        CyclicBarrier syncBarrier = new CyclicBarrier(webClient.length);

        for (SingleClient currSingleClient : webClient)
            currSingleClient.setCyclicBarrier(syncBarrier);

        executor = Executors.newFixedThreadPool(webClient.length);
        for (int i = 0; i < webClient.length; ++i)
            executor.submit(webClient[i]);

        /**
         * Once the last thread invokes barrier.await(), all thread come out of wait.
         * When all threads come out of wait, the number of threads waiting on barrier shall be zero.
         * 
         * Check if Number of threads waiting on the barrier is 0.
         * 
         * Give necessary time for this to happen by checking the above condition
         * max_attempts_to_sync_threads times and sleeping for sleep_after_attempt
         * milliseconds after each attempt, failing which throw an exception.
         */
        for (int i = 0; i < MAX_ATTEMPT_TO_SYNC_THREAD; ++i)
        {
            if (syncBarrier.getNumberWaiting() == 0)
                break;
            Util.sleepInMilli(SLEEP_AFTER_EACH_ATTEMPT);
        }

        if (syncBarrier.getNumberWaiting() != 0)
        {
            /**
             * If we are here, it means even after sufficient waiting not all
             * threads have come to the waiting state. Log error and exit
             */
            logger.warning("All threads did not wake up from wait after " + (MAX_ATTEMPT_TO_SYNC_THREAD * SLEEP_AFTER_EACH_ATTEMPT) + " millisecond");
            throw new IllegalStateException("All threads did not wake up from wait");
        }

        executor.shutdown();
        if (!executor.awaitTermination(cmdArg.timeoutRespBody, TimeUnit.MILLISECONDS))
        {
            logger.warning("Timeout: ForceShutdown: All threads did not terminate after waiting for " + cmdArg.timeoutRespBody + " millisecond");
            executor.shutdownNow();
        }

        abortResponseBodyProcessing();
    }

    /**
     * Abort response body processing if SingleClient threads are still alive.
     */
    public void abortResponseBodyProcessing()
    {
        if (executor.isTerminated())
            return;

        logger.warning("Aborting response body processing.");
        for (int i = 0; i < webClient.length; ++i)
            webClient[i].abortResponseBodyProcessing();

        executor.shutdownNow();
    }
}
