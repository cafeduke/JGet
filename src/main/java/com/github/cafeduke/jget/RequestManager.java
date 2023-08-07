package com.github.cafeduke.jget;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.github.cafeduke.jget.ArgProcessor.HttpMethod;
import com.github.cafeduke.jget.ArgProcessor.MultiThreadMode;
import com.github.cafeduke.jget.common.Util;

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
    private SingleClient client[] = null;

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
    private JGet.Context context = null;

    /**
     * The prepared HttpClient object to be used by all SingleClients
     */
    private HttpClient httpClient = null;

    public static final HttpClient.Version DEFAULT_HTTP_VERSION = HttpClient.Version.HTTP_1_1;

    public static final String[] SSL_PARAMS_DEFAULT_PROTOCOLS = new String[] { "TLSv1.2", "TLSv1.1" };

    /**
     * Create a request manager instance.
     *
     * @param context JReq context
     * @param arg JReq request arguments.
     */
    public RequestManager(JGet.Context context, String arg[])
    {
        this.context = context;
        this.cmdArg = new ArgProcessor(arg);
        this.clientId = context.getClientId();
        this.logger = context.getLogger();
        this.httpClient = null;
    }

    /**
     * Array of response codes corresponding to the requests.
     *
     * @return Response code for the sent request. If non blocking APIs were used
     *         the array can be null.
     */
    public int[] getResponseCode()
    {
        if (client == null)
            return null;

        int respCode[] = new int[client.length];

        for (int index = 0; index < respCode.length; ++index)
            respCode[index] = (client[index] == null) ? -1 : client[index].getResponseCode();

        return respCode;
    }

    /**
     * Send request and return an array of response codes corresponding to the requests.
     * <br>
     * If the request is blocking the main thread itself will take care of
     * spawning multiple SingleClients and capturing the response.
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
            if (!cmdArg.processArg())
                return getResponseCode();

            this.httpClient = buildHttpClient();

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
                cmdArg.mapHeaderValue.put(JGet.CLIENT_ID_HEADER, clientId);
                logger.fine("InstanceId Header = " + clientId);
            }

            if (cmdArg.multiThreadMode == MultiThreadMode.SC)
            {
                if (cmdArg.fileURI == null)
                {
                    client = new SingleClient[] { new SingleClient(context, httpClient, cmdArg) };
                    client[0].run();
                    logReponseCode();
                }
                else
                {
                    List<String> listURI = FileUtils.readLines(cmdArg.fileURI, Charset.defaultCharset());
                    createSingleClients(listURI.size());
                    for (int index = 0; index < client.length; ++index)
                    {
                        client[index].setURI(listURI.get(index));
                        client[index].run();
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
                for (int index = 0; index < client.length; ++index)
                    client[index].setURI(listURI.get(index));
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
     * Prepare HttpClient.Builder with properties that shall apply to all clients.
     */
    private HttpClient buildHttpClient() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, KeyManagementException
    {
        ProxySelector proxySelector = HttpClient.Builder.NO_PROXY;
        if (cmdArg.proxyHost != null)
        {
            String tokenProxy[] = Util.cut(cmdArg.proxyHost, ':');
            proxySelector = ProxySelector.of(new InetSocketAddress(tokenProxy[0], Integer.valueOf(tokenProxy[1])));
        }

        /* Add non-ssl parameters */
        HttpClient.Builder builder = HttpClient.newBuilder()
            .version(cmdArg.httpVersion)
            .followRedirects(cmdArg.followRedirect ? Redirect.ALWAYS : Redirect.NEVER)
            .proxy(proxySelector);

        /* Add ssl parameters */
        if (cmdArg.isSSL)
        {
            SSLContext context = null;
            if (cmdArg.fileKeystore == null && cmdArg.passwordKeyStore == null)
            {
                context = SSLContext.getInstance("TLS");
                context.init(null, new TrustManager[] { new X509ExtendedTrustManager()
                    {
                        @Override
                        public X509Certificate[] getAcceptedIssuers()
                        {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(final X509Certificate[] a_certificates, final String a_auth_type)
                        {
                        }

                        @Override
                        public void checkServerTrusted(final X509Certificate[] a_certificates, final String a_auth_type)
                        {
                        }

                        @Override
                        public void checkClientTrusted(final X509Certificate[] a_certificates, final String a_auth_type, final Socket a_socket)
                        {
                        }

                        @Override
                        public void checkServerTrusted(final X509Certificate[] a_certificates, final String a_auth_type, final Socket a_socket)
                        {
                        }

                        @Override
                        public void checkClientTrusted(final X509Certificate[] a_certificates, final String a_auth_type, final SSLEngine a_engine)
                        {
                        }

                        @Override
                        public void checkServerTrusted(final X509Certificate[] a_certificates, final String a_auth_type, final SSLEngine a_engine)
                        {
                        }
                    } }, null);
            }
            else
            {
                JGet.setKeyStore(cmdArg.fileKeystore.getAbsolutePath(), cmdArg.passwordKeyStore);

                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                InputStream in = new java.io.FileInputStream(cmdArg.fileKeystore);
                ks.load(in, cmdArg.passwordKeyStore.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ks);

                context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
            }

            SSLParameters params = new SSLParameters();
            if (cmdArg.ciphers != null)
                params.setCipherSuites(cmdArg.ciphers.split(","));
            params.setProtocols((cmdArg.tlsVersion == null) ? SSL_PARAMS_DEFAULT_PROTOCOLS : new String[] { "TLSv" + cmdArg.tlsVersion });

            builder.sslContext(context)
                .sslParameters(params);
        }
        return builder.build();
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

        for (int i = 0; i < client.length; ++i)
        {
            for (int j = 0; j < key.length; ++j)
                avg[j] += client[i].getIntegerMetaData(key[j]);
        }

        for (int i = 0; i < key.length; ++i)
        {
            avg[i] = avg[i] / client.length;
            propAvgMeta.setProperty(key[i], "" + avg[i]);
        }

        propAvgMeta.store(new FileWriter("jget.avg.properties"), "Connection Average Meta Data");
    }

    /**
     * Log the obtained response codes. If the response code array size is greater
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
            message = "ResponseCode=" + respCode[0] + Util.LineSep;
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
            message = message + listRange.toString()
                .replaceAll("[\\[\\]]", "")
                .replaceAll(", ", Util.LineSep);
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
        out.print(message);
        out.close();
    }

    /**
     * Creates multiple SingleClients and assigns it with appropriate resources.
     * <ul>
     * <li>Each SingleClient can have its own post body file and request header file.
     * <li>Each SingleClient will have its own output file and output header file.
     * </ul>
     * <br>
     * Handling of output file
     * <ul>
     * <li>If output file is specified, it is appended with {@code <SingleClientIndex>.html}
     * <li>If header output file is specified, it is appended with {@code <SingleClientIndex>.html}
     * </ul>
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
        client = new SingleClient[count];

        for (int i = 0; i < count; i++)
        {
            client[i] = new SingleClient(context, httpClient, cmdArg);

            String currClientId = String.format("%04d", (i + 1));

            if (cmdArg.outputFile != null)
                client[i].setFileResponse(new File(cmdArg.outputFile + currClientId + ".out"));

            if (cmdArg.outputFileHeader != null)
                client[i].setFileResponseHeader(new File(cmdArg.outputFileHeader + currClientId + ".out"));

            if (cmdArg.httpMethod == HttpMethod.POST)
            {
                if (cmdArg.postBody != null)
                    client[i].setPostBody(cmdArg.postBody);
                else if (cmdArg.listPostBodyFile.size() == 1)
                    client[i].setFilePostBody(cmdArg.listPostBodyFile.get(0));
                else if (i < cmdArg.listPostBodyFile.size())
                    client[i].setFilePostBody(cmdArg.listPostBodyFile.get(i));
                else
                    client[i].setPostBody("");
            }

            if (cmdArg.listHeader.size() > 0)
                client[i].setRequestHeader(cmdArg.listHeader);
            else if (cmdArg.listRequestHeaderFile.size() > 0)
            {
                if (cmdArg.listRequestHeaderFile.size() == 1)
                    client[i].setFileRequestHeader(cmdArg.listRequestHeaderFile.get(0));
                else if (i < cmdArg.listRequestHeaderFile.size())
                    client[i].setFileRequestHeader(cmdArg.listRequestHeaderFile.get(i));
            }
        }
    }

    /**
     * Synchronizes all the SingleClients - Ensures all the SingleClients are in a wait
     * state and then releases (notifies) them all. This ensures parallelism.
     * The sequence of events are as follows:
     * <ul>
     * <li>Create a thread for every {@code client} instance.
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
     * @param client An array of SingleClient, each having its own request properties.
     * @throws InterruptedException
     * @throws MalformedURLException
     */
    private void syncParallelSingleClients() throws InterruptedException, MalformedURLException
    {
        CyclicBarrier syncBarrier = new CyclicBarrier(client.length);

        for (SingleClient currSingleClient : client)
            currSingleClient.setCyclicBarrier(syncBarrier);

        executor = Executors.newFixedThreadPool(client.length);
        for (int i = 0; i < client.length; ++i)
            executor.submit(client[i]);

        /**
         * Once the last thread invokes barrier.await(), all thread come out of wait.
         * When all threads come out of wait, the number of threads waiting on barrier shall be zero.
         * Check if Number of threads waiting on the barrier is 0.
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
        for (int i = 0; i < client.length; ++i)
            client[i].abortResponseBodyProcessing();

        executor.shutdownNow();
    }
}
