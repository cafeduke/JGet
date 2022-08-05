package com.github.cafeduke.jreq;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.github.cafeduke.jreq.ArgProcessor.HttpMethod;
import com.github.cafeduke.jreq.ArgProcessor.MultiThreadMode;
import com.github.cafeduke.jreq.ArgProcessor.OutputMode;
import com.github.cafeduke.jreq.JReq.Context;
import com.github.cafeduke.jreq.common.Util;

/**
 * A request is encapsulated as a SingleClient. Each SingleClient has its own
 * set of request parameters like request headers, post body, response output file
 * and the like.
 * 
 * SingleClient implements Runnable and hence invoking start () on the thread will
 * take care of sending request and capturing the response.
 * 
 * @author Raghunandan.Seshadri
 */
public class SingleClient implements Runnable
{

    /* Attributes WITH setter */

    /**
     * A context object obtained from JReq
     */
    private JReq.Context context = null;

    /**
     * The HttpClient object to be used by all SingleClient
     */
    private HttpClient httpClient = null;

    /**
     * Reference to the command line argument processor.
     * Many arguments set are applicable to all SingleClients. Such options can be read from cmdArg.
     */
    private ArgProcessor cmdArg = null;

    /**
     * URL to be used by this SingleClient.
     */
    private URL url = null;

    /**
     * Object used by this SingleClient for synchronization.
     * All SingleClients MUST reference the same sync object.
     */
    private CyclicBarrier syncBarrier = null;

    /**
     * File to be used by this SingleClient to store response.
     */
    private File fileOutput = null;

    /**
     * File to be used by this SingleClient to store response headers.
     */
    private File fileOutputHeader = null;

    /**
     * The post body to be used by this SingleClient.
     */
    private String postBody = null;

    /**
     * File to be read by this SingleClient to fetch post body.
     */
    private File filePostBody = null;

    /**
     * List of request headers to be sent by this SingleClient.
     */
    private List<String> listHeader = new ArrayList<String>();

    /**
     * File having request headers to be read and sent by this SingleClient.
     */
    private File fileRequestHeader = null;

    /* Attributes WITHOUT setter */

    /**
     * Response code for the request sent by this SingleClient.
     */
    private int respCode = -1;

    /**
     * Logger used by this SingleClient.
     */
    private Logger logger = null;

    /**
     * Properties object to store request meta data.
     */
    private Properties propConnInfo = new Properties();

    /**
     * If true response body processing shall be aborted
     */
    private boolean abortResponseBodyProcessing = false;

    /* Static Attributes */

    /**
     * List of response headers to be ignored/filtered before writing to response file.
     */
    public static String headerToIngore[] = new String[] {
            "Date", "Connection", "Keep-Alive", "Server"
    };

    static
    {
        Arrays.sort(headerToIngore);
    }

    /**
     * Create a SingleClient object - Takes care of one request.
     * 
     * @param context JReq context.
     * @param httpClient HttpClient object to be used by all SingleClients
     * @param cmdArg Argument object.
     * @throws MalformedURLException Exception if the URL is malformed
     */
    public SingleClient(Context context, HttpClient httpClient, ArgProcessor cmdArg) throws MalformedURLException
    {
        this.context = context;
        this.httpClient = httpClient;
        this.logger = context.getLogger();
        this.cmdArg = cmdArg;

        if (cmdArg.url == null)
            this.url = (cmdArg.uri == null) ? null : constructURL(cmdArg.uri);
        else
            this.url = constructURL(cmdArg.url.toString());

        if (cmdArg.outputFile != null)
            this.fileOutput = new File(cmdArg.outputFile);

        if (cmdArg.outputFileHeader != null)
            this.fileOutputHeader = new File(cmdArg.outputFileHeader);

        if (cmdArg.postBody != null)
            this.postBody = cmdArg.postBody;
        else if (cmdArg.listPostBodyFile.size() == 1)
            this.filePostBody = cmdArg.listPostBodyFile.get(0);

        if (cmdArg.listHeader.size() > 0)
            this.listHeader.addAll(cmdArg.listHeader);

        if (cmdArg.listRequestHeaderFile.size() == 1)
            this.fileRequestHeader = cmdArg.listRequestHeaderFile.get(0);

        if (cmdArg.proxyAuth != null)
        {
            ProxyAuthenticator authenticator = new ProxyAuthenticator(cmdArg.proxyAuth);
            Authenticator.setDefault(authenticator);
        }
    }

    @Override
    public void run()
    {
        try
        {
            logger.fine("Thread started run");
            if (cmdArg.multiThreadMode == MultiThreadMode.SC)
                makeConnections();
            else
            {
                logger.fine("Shall wait for sync");
                syncBarrier.await(60, TimeUnit.SECONDS);
                logger.fine("Out of wait");
                long beginTime = System.currentTimeMillis();
                makeConnections();
                long endTime = System.currentTimeMillis();
                long timeTaken = (endTime - beginTime) / 1000;
                logger.fine("URL=" + this.url + ", ThreadTimeTaken=" + timeTaken + " sec");
            }
            logger.fine("Thread finished run");
        }
        catch (Exception e)
        {
            if (cmdArg.enableErrorLog)
                logger.log(Level.SEVERE, "Error running SingleClient", e);
        }
    }

    /**
     * @return Response code of this SingleClient's request.
     */
    public int getResponseCode()
    {
        return respCode;
    }

    /**
     * Set flag to abort response processing
     */
    public void abortResponseBodyProcessing()
    {
        abortResponseBodyProcessing = true;
    }

    /**
     * Performs the following operations
     * <ul>
     * <li>Open a HTTP connection to the set URL
     * <li>Prepare Request
     * <ul>
     * <li>Set connection time outs
     * <li>Set request headers
     * <li>Set request post body
     * </ul>
     * <li>Connect to the server
     * <li>Process the response obtained from the server
     * <ul>
     * <li>Read response headers and response body
     * <li>Write the response headers and boy to appropriate output stream.
     * </ul>
     * <li>Handle compression
     * <ul>
     * <li>Uncompress the compressed response in case of GZip compression
     * <li>Record the compression properties
     * </ul>
     * </ul>
     * 
     * @throws URISyntaxException
     */
    private void makeConnections() throws IOException, InterruptedException, URISyntaxException
    {
        long sumTurnAroundTime = 0;
        for (int currReqCount = 1; currReqCount <= cmdArg.repeatCountPerThread; currReqCount++)
        {
            logger.fine("Started request[" + currReqCount + "] " + url.toString());

            try
            {
                long timeBeforeConn = System.currentTimeMillis();

                /* Build HttpRequest */
                HttpRequest request = prepareRequest();

                /* Send request */
                propConnInfo.setProperty("client.http.version", httpClient.version().toString());
                propConnInfo.setProperty("request.http.version", request.version().toString());
                HttpResponse<InputStream> response = httpClient.send(request, BodyHandlers.ofInputStream());

                /* Process Response */
                propConnInfo.setProperty("response.http.version", response.version().toString());
                processResponse(response);

                /* Post Response: Handle compression*/
                handleCompression(response);

                long timeAfterConn = System.currentTimeMillis();
                sumTurnAroundTime += (timeAfterConn - timeBeforeConn);
            }
            catch (IOException | InterruptedException e)
            {
                if (respCode == -1)
                {
                    respCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
                    logger.fine("RespCode = " + respCode);
                }

                PrintStream outBodyStream = getOutputStreamToLogError();
                if (outBodyStream != null)
                {
                    outBodyStream.println();
                    outBodyStream.println("_____ StackTrace _____");
                    e.printStackTrace(outBodyStream);
                    outBodyStream.close();
                }
                throw e;
            }
            logger.fine("Finished request[" + currReqCount + "]");
        }

        String strTurnAroundTime = "" + (long) (sumTurnAroundTime / cmdArg.repeatCountPerThread);
        propConnInfo.setProperty("TurnAroundTime", strTurnAroundTime);

        if (!propConnInfo.isEmpty())
            writeMetaData();
    }

    /**
     * Prepare connection {@code conn} for request
     * <ul>
     * <li>Set connection timeouts
     * <li>Set request headers
     * <li>Set request post body
     * </ul>
     * 
     * @param conn
     * @throws IOException
     * @throws URISyntaxException
     */
    private HttpRequest prepareRequest() throws IOException, URISyntaxException
    {
        logger.fine("Prepare for request");

        long timeBefore = System.currentTimeMillis();

        HttpRequest.Builder builderReq = HttpRequest.newBuilder().uri(url.toURI());

        /* Prepare request headers */
        prepareRequestHeaders(builderReq);

        /* Prepare request body */
        HttpRequest.BodyPublisher bodyPublisher = prepareRequestBody(builderReq);

        /* Build HTTPRequest */
        HttpRequest request = builderReq.timeout(Duration.ofMillis(cmdArg.timeoutSocket)).method(cmdArg.httpMethod.name(), bodyPublisher).build();

        long timeAfter = System.currentTimeMillis();
        propConnInfo.setProperty("RequestDataSendDuration", "" + (timeAfter - timeBefore));

        return request;
    }

    /**
     * Add request header fields to connection.
     * 
     * If request header parameter file is provided, read the request
     * headers from it.These headers will overwrite any headers of same
     * name previously provided through different command line switch
     * 
     * @param conn HTTP URL Connection
     */
    private void prepareRequestHeaders(HttpRequest.Builder builderReq) throws IOException
    {
        for (String currHeaderName : cmdArg.mapHeaderValue.keySet())
            // conn.setRequestProperty(currHeaderName, cmdArg.mapHeaderValue.get(currHeaderName));
            builderReq = builderReq.setHeader(currHeaderName, cmdArg.mapHeaderValue.get(currHeaderName));

        prepareRequestCookie(builderReq);

        if (fileRequestHeader != null || listHeader.size() > 0)
        {
            logger.fine("Add request headers");
            List<String> listFinalHeader = new ArrayList<String>();

            if (listHeader.size() > 0)
                listFinalHeader.addAll(listHeader);

            if (fileRequestHeader != null)
            {
                String currHeader = null;
                BufferedReader inRequestHeader = new BufferedReader(new FileReader(fileRequestHeader));
                while ((currHeader = inRequestHeader.readLine()) != null)
                    listFinalHeader.add(currHeader);
                inRequestHeader.close();
            }

            for (String currHeader : listFinalHeader)
            {
                String headerPart[] = Util.cut(currHeader, ':');
                if (headerPart == null)
                    throw new IllegalArgumentException("Did not find ':' while reading header '" + currHeader + "'");
                // conn.setRequestProperty(headerPart[0], headerPart[1]);
                builderReq = builderReq.setHeader(headerPart[0], headerPart[1]);
            }
        }
    }

    /**
     * If session binding is enabled, add cookies present in JReq context to HttpURLConnection.
     * 
     * @param conn HttpURLConnection object
     */
    private void prepareRequestCookie(HttpRequest.Builder builderReq)
    {
        if (!cmdArg.enableSessionBinding || context.getCookies().isEmpty())
            return;

        StringBuilder builder = new StringBuilder();
        for (String cookieName : context.getCookies().keySet())
            builder.append(cookieName).append("=").append(context.getCookies().get(cookieName)).append("; ");

        String cookieValue = builder.substring(0, builder.lastIndexOf(";"));
        // conn.setRequestProperty("Cookie", cookieValue);
        builderReq = builderReq.setHeader("Cookie", cookieValue);
        logger.fine("[prepareRequestCookie] Cookie: " + cookieValue);
    }

    /**
     * Add request body to the connection.
     * 
     * @param conn HTTP URL Connection
     * @throws IOException
     */
    private HttpRequest.BodyPublisher prepareRequestBody(HttpRequest.Builder builderReq) throws IOException
    {
        HttpRequest.BodyPublisher bodyPublisher = null;

        if (cmdArg.httpMethod == HttpMethod.POST || cmdArg.httpMethod == HttpMethod.PUT)
        {
            logger.fine("Add request body");
            // conn.setDoOutput(true);

            if (cmdArg.chunkLen > -1)
            {
                listHeader.add("Transfer-Encoding:chunked");
                // conn.setChunkedStreamingMode(cmdArg.chunkLen);
            }

            if (cmdArg.byteSendDelay > 0)
            {
                InputStream in = (postBody != null) ? new ByteArrayInputStream(postBody.getBytes()) : new FileInputStream(filePostBody);
                bodyPublisher = HttpRequest.BodyPublishers.ofByteArrays(() -> new SlowReqBodyIterator(in));
            }
            else
            {
                bodyPublisher = (postBody != null) ? HttpRequest.BodyPublishers.ofString(postBody) : HttpRequest.BodyPublishers.ofFile(filePostBody.toPath());
            }
        }
        else
            bodyPublisher = HttpRequest.BodyPublishers.noBody();

        return bodyPublisher;
    }

    /**
     * A class that simulates a slow client that sleeps for an interval
     * after sending few bytes from request body.
     */
    private class SlowReqBodyIterator implements Iterator<byte[]>
    {
        InputStream in = null;
        int offset = 0;
        byte buffer[] = new byte[4096];
        boolean hasNext = true;

        public SlowReqBodyIterator(InputStream in)
        {
            this.in = in;
        }

        @Override
        public boolean hasNext()
        {
            return hasNext;
        }

        @Override
        public byte[] next()
        {
            try
            {
                int count = in.read(buffer, offset, buffer.length);
                if (count < 0)
                {
                    hasNext = false;
                    return new byte[0];
                }
                Util.sleepInMilli(cmdArg.byteSendDelay);
                return Arrays.copyOfRange(buffer, 0, count);
            }
            catch (IOException e)
            {
                throw new IllegalStateException("Error simulating slow client during send", e);
            }
        }
    }

    /**
     * Process the response obtained from the server
     * <ul>
     * <li>Read response headers and response body
     * <li>Write the response headers and boy to appropriate output stream.
     * </ul>
     * 
     * @throws Exception
     */
    private void processResponse(HttpResponse<InputStream> response) throws IOException
    {
        logger.fine("Process response");

        long timeBefore = System.currentTimeMillis();
        BufferedInputStream inConnStream = null;
        PrintStream outHeaderStream = null;
        PrintStream outBodyStream = null;

        try
        {
            /**
             * Set respCode to the highest among several
             * request repeats ( ArgProcessor.repeatCountPerThread)
             */
            int respCodeThisRequest = response.statusCode();

            if (respCodeThisRequest == -1)
                respCodeThisRequest = HttpURLConnection.HTTP_INTERNAL_ERROR;

            if (respCode == -1 || respCodeThisRequest > respCode)
                respCode = respCodeThisRequest;

            inConnStream = new BufferedInputStream(response.body());

            /* Open response header and response body stream */
            outHeaderStream = getOutputHeaderStream();
            outBodyStream = getOutputBodyStream();

            /* Output headers and body  */
            processResponseHeaders(response, outHeaderStream, outBodyStream);

            processResponseBody(inConnStream, outBodyStream);
        }
        /* Close response header and response body stream */
        finally
        {
            if (inConnStream != null)
                inConnStream.close();

            if (outHeaderStream != null)
                outHeaderStream.close();

            if (outBodyStream != null)
                outBodyStream.flush();

            if (cmdArg.outputMode == OutputMode.FILE_WRITE || cmdArg.outputMode == OutputMode.FILE_APPEND)
                outBodyStream.close();

            long timeAfter = System.currentTimeMillis();
            propConnInfo.setProperty("ResponseDataReceiveDuration", "" + (timeAfter - timeBefore));
        }
    }

    /**
     * Process response headers read from {@code conn}
     * 
     * @param conn HTTP URL connection
     * @param outHeaderStream Stream to write response headers.
     * @param outBodyStream Stream to write response body.
     */
    private void processResponseHeaders(HttpResponse<InputStream> response, PrintStream outHeaderStream, PrintStream outBodyStream)
    {
        Map<String, List<String>> mapHeaderValue = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

        /* Convert the read only connection's Map into case-insensitive key holding TreeMap without a null key */
        Map<String, List<String>> rawMap = new HashMap<>();
        rawMap.putAll(response.headers().map());
        rawMap.put("", rawMap.remove(null));
        mapHeaderValue.putAll(rawMap);

        processResponseCookie(mapHeaderValue);

        /* Return, if response headers need not be captured */
        if (!cmdArg.showResponseHeader)
            return;

        /* Write response line */
        String statusLine = getStatusLine(response);

        if (outHeaderStream != null)
            outHeaderStream.println("Status: " + statusLine);
        else if (outBodyStream != null)
            outBodyStream.println(statusLine);

        /* Write response headers */

        if (cmdArg.showResponseHeader)
        {
            for (String currHeaderName : mapHeaderValue.keySet())
            {
                boolean ignoreHeader = false;

                /* First header is response line with a null header name*/
                if (currHeaderName == null || currHeaderName.equals(""))
                    continue;

                /* If show all headers is NOT specified, filtering is needed. */
                if (!cmdArg.showAllResponseHeader)
                {
                    if (cmdArg.listParticularHeader.size() > 0)
                    {
                        /* Do NOT ignore a header if present in listParticularHeader list, otherwise IGNORE it */
                        ignoreHeader = cmdArg.listParticularHeader.contains(currHeaderName) ? false : true;
                    }
                    else
                    {
                        /* Filter headers that are generally not required */
                        if (Arrays.binarySearch(headerToIngore, currHeaderName) >= 0)
                            ignoreHeader = true;
                    }
                }

                if (!ignoreHeader)
                {
                    String currHeaderValue = Util.join(mapHeaderValue.get(currHeaderName), ',');
                    String currHeader = currHeaderName + ": " + currHeaderValue;

                    if (outHeaderStream != null)
                        outHeaderStream.println(currHeader);
                    else if (outBodyStream != null)
                        outBodyStream.println(currHeader);
                }
            }

            /* Write new line if header and body go to the same file */
            if (outHeaderStream == null)
                outBodyStream.println();
        }
    }

    /**
     * If session binding is enabled, process the response headers map to extract all cookies
     * from Set-Cookie header value and populate the cookie map in JReq context.
     * 
     * @param mapHeaderValue Map having response headers
     */
    private void processResponseCookie(Map<String, List<String>> mapHeaderValue)
    {
        if (!cmdArg.enableSessionBinding)
            return;

        List<String> listCookie = mapHeaderValue.get("Set-Cookie");
        if (listCookie == null || listCookie.size() == 0)
            return;

        for (String currCookie : listCookie)
        {
            currCookie = currCookie.replaceAll(";.*", "").trim();
            String keyValue[] = currCookie.split("=");
            if (keyValue.length == 1)
                keyValue = new String[] { keyValue[0], "" };
            context.getCookies().put(keyValue[0], keyValue[1]);
        }

        logger.fine("[processResponseCookie] CookieMap = " + context.getCookies());
    }

    /**
     * Process response body read from {@code inConnStream}.
     * 
     * The processing of the response body MUST happen even if outBodyStream is null ( quit request)
     * - We are NOT storing/writing the response body, however, we STILL have to read the ENTIRE response.
     * 
     * @param inConnStream Stream to HTTP URL connection
     * @param outBodyStream Stream to write the response body.
     * @throws IOException
     */
    private void processResponseBody(BufferedInputStream inConnStream, PrintStream outBodyStream) throws IOException
    {
        byte buffer[] = (cmdArg.byteReceiveDelay > 0) ? new byte[4096] : new byte[1];
        int count = -1;

        while ((count = inConnStream.read(buffer)) != -1)
        {
            if (abortResponseBodyProcessing)
            {
                logger.fine("Aborting response body processing.");
                break;
            }

            if (cmdArg.byteReceiveDelay > 0)
                Util.sleepInMilli(cmdArg.byteReceiveDelay);

            if (outBodyStream != null)
            {
                outBodyStream.write(buffer, 0, count);
                outBodyStream.flush();
            }
        }
    }

    /**
     * Handle compressed response
     * <ul>
     * <li>Uncompress the compressed response in case of GZip compression
     * <li>Record the compression properties
     * </ul>
     * 
     * @param conn
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void handleCompression(HttpResponse<InputStream> response) throws FileNotFoundException, IOException
    {
        String encoding = response.headers().firstValue("content-encoding").orElse("");
        if (encoding.isEmpty() || !encoding.equalsIgnoreCase("gzip") || cmdArg.outputFile == null)
            return;

        File fileResponse = new File(cmdArg.outputFile);
        File fileResponseUnzip = new File(cmdArg.outputFile + ".unzip");

        GZIPInputStream inZipStream = new GZIPInputStream(new FileInputStream(fileResponse));
        FileOutputStream outZipStream = new FileOutputStream(fileResponseUnzip);
        byte buffer[] = new byte[4096];
        int count = -1;
        while ((count = inZipStream.read(buffer)) != -1)
            outZipStream.write(buffer, 0, count);
        inZipStream.close();
        outZipStream.close();

        long sizeOriginal = fileResponseUnzip.length();
        long sizeCompressed = fileResponse.length();
        double percentCompress = -1;
        if (sizeOriginal != 0)
            percentCompress = ((sizeOriginal - sizeCompressed) / (double) sizeOriginal) * 100;

        String strOriginal = "" + sizeOriginal;
        String strCompressed = "" + sizeCompressed;
        String strPercentCompress = String.format("%.2f", percentCompress);

        propConnInfo.setProperty("ContentEncoding", encoding);
        propConnInfo.setProperty("OriginalSize", strOriginal);
        propConnInfo.setProperty("CompressedSize", strCompressed);
        propConnInfo.setProperty("PercentCompress", strPercentCompress);

        fileResponse.delete();
        fileResponseUnzip.renameTo(fileResponse);
    }

    /**
     * @return Stream to write response headers read from HTTP connection.
     * @throws FileNotFoundException
     */
    private PrintStream getOutputHeaderStream() throws FileNotFoundException
    {
        boolean autoFlush = true;
        return (fileOutputHeader == null) ? null : new PrintStream(new FileOutputStream(fileOutputHeader), autoFlush);
    }

    /**
     * @return Stream to write response body read from HTTP connection.
     * @throws FileNotFoundException
     */
    private PrintStream getOutputBodyStream() throws FileNotFoundException
    {
        PrintStream outBodyStream = null;
        boolean autoFlush = true;

        if (cmdArg.outputMode == OutputMode.STDOUT)
            outBodyStream = System.out;
        else if (cmdArg.outputMode == OutputMode.FILE_WRITE)
            outBodyStream = new PrintStream(new FileOutputStream(fileOutput), autoFlush);
        else if (cmdArg.outputMode == OutputMode.FILE_APPEND)
            outBodyStream = new PrintStream(new FileOutputStream(fileOutput, true), autoFlush);

        return outBodyStream;
    }

    /**
     * @return Stream to append any error that happened during response body processing.
     * @throws FileNotFoundException
     */
    private PrintStream getOutputStreamToLogError() throws FileNotFoundException
    {
        PrintStream outBodyStream = null;
        boolean autoFlush = true;

        if (cmdArg.outputMode == OutputMode.STDOUT)
            outBodyStream = System.out;
        else if (cmdArg.outputMode == OutputMode.FILE_WRITE || cmdArg.outputMode == OutputMode.FILE_APPEND)
            outBodyStream = new PrintStream(new FileOutputStream(fileOutput, true), autoFlush);
        return outBodyStream;
    }

    /**
     * Construct the status line from the response
     * 
     * @param <T> the response body type
     * @param response HTTPResponse object
     * @return Status line with HTTP version, path and response code.
     */
    public static <T> String getStatusLine(HttpResponse<T> response)
    {
        String version = response.version() == Version.HTTP_2 ? "2" : "1.1";
        int statusCode = response.statusCode();
        String reason = Optional.of(HttpStatus.getStatusText(statusCode)).orElseThrow();
        return String.format("HTTP/%s %d %s", version, statusCode, reason);
    }

    /**
     * Write meta-data details recorded during request.
     */
    private void writeMetaData() throws IOException
    {
        if (!cmdArg.recordMetaData)
            return;

        String fileMetaData = null;

        if (fileOutput == null)
            fileMetaData = "jreq.properties";
        else
        {
            fileMetaData = fileOutput.getName();
            int index = fileMetaData.lastIndexOf('.');
            if (index != -1)
                fileMetaData = fileMetaData.substring(0, index);
            fileMetaData = fileMetaData + ".jreq.properties";
        }
        propConnInfo.store(new FileWriter(fileMetaData), "Connection Meta Data");
    }

    /**
     * @param key Meta data key
     * @return The integral meta data for the given <b>key</b>
     */
    public int getIntegerMetaData(String key)
    {
        String strValue = propConnInfo.getProperty(key);
        return (strValue == null) ? 0 : Integer.valueOf(strValue);
    }

    /**
     * Set URI
     * 
     * @param uri URI to be set. Format {@code /<path prefix>}
     * @throws MalformedURLException If URI is malformed
     */
    public void setURI(String uri) throws MalformedURLException
    {
        this.url = constructURL(uri);
    }

    /**
     * The {@code URI} should be in the format "/<path prefix>".
     * 
     * <br>
     * Complete the URI by prepending
     * <ul>
     *   <li>HTTP Mode {@code http | https}
     *   <li>Host name
     *   <li>Port number
     * </ul>
     * 
     * @param URI URI having path prefix
     * @return Constructed URL  
     * @throws MalformedURLException If URI is malformed
     */
    private URL constructURL(String URI) throws MalformedURLException
    {
        if (URI == null)
            throw new IllegalArgumentException("URI cannot be null");

        if (URI.startsWith("http://") || URI.startsWith("https://"))
            return new URL(URI);

        URL currURL = null;
        String strSSL = cmdArg.isSSL ? "s" : "";
        boolean prefixLine = URI.startsWith("/") ? true : false;
        if (prefixLine)
        {
            if (cmdArg.host == null || cmdArg.port == null)
                throw new IllegalArgumentException("URI '" + URI + "' is identified as prefix, for which " +
                        "<host> and <port> arguments are needed but NOT found." + Util.LineSep +
                        "Host :" + cmdArg.host + " Port :" + cmdArg.port);
            currURL = new URL("http" + strSSL + "://" + cmdArg.host + ":" + cmdArg.port + URI);
        }
        else
            currURL = new URL(URI);
        return currURL;
    }

    /**
     * @param syncBarrier Set sync object used to synchronize parallel threads.
     */
    public void setCyclicBarrier(CyclicBarrier syncBarrier)
    {
        this.syncBarrier = syncBarrier;
    }

    /**
     * @param fileResponse Set file having response body (and headers)
     */
    public void setFileResponse(File fileResponse)
    {
        this.fileOutput = fileResponse;
    }

    /**
     * @param fileResponseHeader Set file having response headers.
     */
    public void setFileResponseHeader(File fileResponseHeader)
    {
        this.fileOutputHeader = fileResponseHeader;
    }

    /**
     * @param postBody Set post body.
     */
    public void setPostBody(String postBody)
    {
        this.postBody = postBody;
    }

    /**
     * @param filePostBody Set file having post body.
     */
    public void setFilePostBody(File filePostBody)
    {
        this.filePostBody = filePostBody;
    }

    /**
     * @param listHeader Set request headers
     */
    public void setRequestHeader(List<String> listHeader)
    {
        this.listHeader.addAll(listHeader);
    }

    /**
     * @param fileRequestHeader Set file having request headers.
     */
    public void setFileRequestHeader(File fileRequestHeader)
    {
        this.fileRequestHeader = fileRequestHeader;
    }

    private class ProxyAuthenticator extends Authenticator
    {
        private String proxyUsername = null;

        private String proxyPassword = null;

        /**
         * Return instance of proxy authenticator.
         * 
         * @param proxyAuth Format: {@code <proxy username>:<proxy password> }
         */
        public ProxyAuthenticator(String proxyAuth)
        {
            String tokenProxyAuth[] = Util.cut(proxyAuth, ':');
            proxyUsername = tokenProxyAuth[0];
            proxyPassword = tokenProxyAuth[1];
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
        }
    }

}
