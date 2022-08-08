package com.github.cafeduke.jreq;

import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.github.cafeduke.jreq.ArgProcessor.MultiThreadMode;
import com.github.cafeduke.jreq.common.ToolsUtil;
import com.github.cafeduke.jreq.common.Util;

/**
 * A Java based HTTP Client that exposes APIs to send multiple sequential/simultaneous requests and optionally capture the response body/headers.
 *
 * <ul>
 * <li>JReq is capable of sending requests of all request method types (Get, Post, Head, Delete, Put, Trace and Options).
 * <li>JReq can operate in the following threading mode.
 * <ul>
 * <li>Single Client (SC) - A single thread is used to send requests sequentially.
 * <li>Multiple Similar Clients (MSC): Spawns multiple threads, all threads requesting the same URL.
 * <li>Multiple Unique Clients (MUC): Spawns multiple threads, each thread picks a URL from an input URL file.
 * </ul>
 * <li>Each JReq thread can send its own set of request headers and/or request body.
 * <li>JReq can optionally maintain cookie based session.
 * </ul>
 * 
 * @author Raghunandan.Seshadri
 */
public class JReq
{
    /**
     * The request header name which will have a unique value for every JReq instance.
     */
    public static final String CLIENT_ID_HEADER = "ClientId";

    /**
     * In case of Non-blocking requests, this timeout specifies the max number of seconds
     * to wait for all threads to return with response code.
     */
    public static final int TIMEOUT_RESPONSE_CODE = 5 * 60;

    /**
     * Request Manager for this JReq instance
     */
    private RequestManager requestManager = null;

    /**
     * List of JReq Arguments used by all send methods.
     */
    private List<String> listCommonArg = new ArrayList<String>();

    /**
     * Default JReq Logger
     */
    private static final Logger DEFAULT_LOGGER = ToolsUtil.getLogger(JReq.class.getName(), "JReq.log");

    /**
     * JReq context
     */
    private final Context context = new Context();

    static
    {
        setHTTPProperties();
    }

    private JReq()
    {
        context.setLogger(DEFAULT_LOGGER);
        setSessionBinding(false);
    }

    /**
     * Get a new instance of JReq.
     * 
     * Every new instance of JReq sends a unique request ID. This helps origin server differentiate
     * among requests from different instances.
     * 
     * @return JReq instance
     */
    public static JReq getInstance()
    {
        return new JReq();
    }

    /**
     * @return A builder used to build arguments for JReq
     */
    public static JReq.ArgBuilder newBuilder()
    {
        return new JReq.ArgBuilder();
    }

    /**
     * Main method to enable JReq to also run as a stand alone HTTP client.
     * 
     * @param arg Arguments to main.
     * @throws Exception An exception (if any) sending request.
     */
    public static void main(String arg[]) throws Exception
    {
        JReq.getInstance().sendRequest(arg);
    }

    /**
     * Return the status line for the response object
     * 
     * @param <T> the response body type
     * @param response HttpResponse object
     * @return The status line of the format {@code HTTP/<version> <status message>}
     */
    public static <T> String getStatusLine(HttpResponse<T> response)
    {
        String version = response.version() == Version.HTTP_2 ? "2" : "1.1";
        return String.format("HTTP/%s %d", version, response.statusCode());
    }

    /**
     * Send request with arguments {@code arg}
     * 
     * @param arg Arguments for JReq
     * @return Array of response codes.
     */
    public int[] sendRequest(String arg[])
    {
        return sendRequest(JReq.getArgAsList(arg));
    }

    /**
     * Send request with arguments {@code listArg}
     * <b>Note:</b> All sendRequest/sendQuietRequest finally resolve to this method.
     * 
     * @param listArg List of arguments
     * @return Array of response codes.
     */
    public int[] sendRequest(List<String> listArg)
    {
        listArg.addAll(listCommonArg);
        context.logger.info("Executing JReq: " + Util.join(listArg, ' '));

        requestManager = new RequestManager(context, listArg.toArray(new String[0]));
        return requestManager.sendRequest();
    }

    /* ------------------------------------------------------------------- */
    /* JReq Getters/Setters                                                */
    /* ------------------------------------------------------------------- */

    /**
     * Set a custom logger for JReq
     * 
     * @param logger The logger object to be used.
     */
    public void setLogger(Logger logger)
    {
        context.setLogger(logger);
    }

    /**
     * If {@code trackSession} is true, session is managed using Cookies.
     * 
     * @param trackSession If true, session is managed using Cookies.
     */
    public void setSessionBinding(boolean trackSession)
    {
        if (trackSession)
            listCommonArg.add(ArgProcessor.ENABLE_SESSION_BINDING);
        else
        {
            listCommonArg.remove(ArgProcessor.ENABLE_SESSION_BINDING);
            context.clearCookies();
        }
    }

    /**
     * Sets if HTTP redirects (requests with response code 3xx) should be automatically followed. By
     * default, JReq APIs follow redirects automatically.
     * 
     * If true, follow the HTTP redirects otherwise return the 3xx response.
     * 
     * @param followRedirect If true, follow the HTTP redirects otherwise return the 3xx response.
     */
    public void setFollowRedirect(boolean followRedirect)
    {
        if (followRedirect)
            listCommonArg.remove(ArgProcessor.DISABLE_FOLLOW_REDIRECT);
        else
            listCommonArg.add(ArgProcessor.DISABLE_FOLLOW_REDIRECT);
    }

    /**
     * Sets the blocking mode. By default, JReq APIs are blocking.
     * 
     * If true
     * <ul>
     *   <li>Wait until all threads have finished execution or have timed out.
     *   <li>Methods like sendXxxRequest () will not return until all threads have completed execution.
     *   <li>Array returned will have response code corresponding to the request.
     * </ul>
     * 
     * If false
     * <ul>
     *   <li>Does NOT wait for threads to finish execution.
     *   <li>Methods like sendXxxRequest () will return almost immediately.
     *   <li>Array returned may be null as the threads might not have completed execution.
     *   <li>Poll using {@link #getResponseCode()} to retrieve response code.
     * </ul>
     * 
     * @param isBlocking If true, requests shall be blocking
     */
    public void setBlockingMode(boolean isBlocking)
    {
        if (isBlocking)
            listCommonArg.remove(ArgProcessor.NON_BLOCKING_REQUEST);
        else
            listCommonArg.add(ArgProcessor.NON_BLOCKING_REQUEST);
    }

    /**
     * Set HTTP system properties
     */
    public static void setHTTPProperties()
    {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    /**
     * Set the Java Key Store (JKS) for the instance
     * 
     * @param keyStore Path to the JKS.
     * @param storePass Password for the JKS.
     */
    public static void setKeyStore(String keyStore, String storePass)
    {
        /* Set key store properties */
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStoreType", "JKS");
        System.setProperty("javax.net.ssl.keyStorePassword", storePass);

        /* Set trust store properties  */
        System.setProperty("javax.net.ssl.trustStore", keyStore);
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStorePassword", storePass);

        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
    }

    /**
     * Header name and value - This header will be added to all requests with this instance.
     * 
     * @param name Header name
     * @param value Header value
     */
    public void addHeader(String name, String value)
    {
        updateArgWithHeaders(listCommonArg, new String[] { name + ":" + value });
    }

    /**
     * Get the current status of response codes immediately. Each response code
     * maps to the corresponding request.
     * 
     * @return An array of response codes or null if no response code is available yet.
     */
    public int[] getCurrResponseCode()
    {
        return (requestManager == null) ? null : requestManager.getResponseCode();
    }

    /**
     * Invoke {@code getResponseCode(TIMEOUT_RESPONSE_CODE, true); }
     * See {@link #getResponseCode(int, boolean)}
     * 
     * @return An array of response codes or null if no response code is available.
     */
    public int[] getResponseCode()
    {
        return getResponseCode(TIMEOUT_RESPONSE_CODE, true);
    }

    /**
     * Invoke getResponseCode(timeout, true);
     * <br>See {@link #getResponseCode(int, boolean)} for details.
     * 
     * @param timeout Max number of seconds to poll for response codes to be available.
     * @return An array of response codes or null if no response code is available.
     */
    public int[] getResposneCode(int timeout)
    {
        return getResponseCode(timeout, true);
    }

    /**
     * Invoke getResponseCode(timeout, 2, waitForAllThreads); <br>
     * See {@link #getResponseCode(int, int, boolean)} for details.
     * 
     * @param timeout Max number of seconds to poll for response codes to be available.
     * @param waitForAllThreads If true, wait until all threads have response code or timeout.
     * @return An array of response codes or null if no response code is available.
     */
    public int[] getResponseCode(int timeout, boolean waitForAllThreads)
    {
        return getResponseCode(timeout, 2, waitForAllThreads);
    }

    /**
     * An array of response codes or null if no response code is available event after
     * {@code timeout}.
     * 
     * After every {@code pollInterval} seconds check if the response codes are available. If so, the
     * response codes are returned. If not, sleep for {@code pollInterval} and retry again. This is
     * iterated until {@code timeout} seconds are reached.
     * 
     * If waitForAllThreads is true, check if response codes are available from all threads. If so, the
     * response codes are returned. If waitForAllThreads is false, it is sufficient if a subset of threads
     * have response codes for the function to return.
     * 
     * @param timeout Max number of seconds to poll for response codes to be available.
     * @param pollInterval Time to sleep in seconds before retrying again.
     * @param waitForAllThreads If true, wait until all threads have response code or timeout.
     * @return An array of response codes or null if no response code is available.
     */
    public int[] getResponseCode(int timeout, int pollInterval, boolean waitForAllThreads)
    {
        int responseCode[] = null;

        for (int currTime = 0; currTime < timeout; currTime += pollInterval)
        {
            if ((responseCode = getCurrResponseCode()) != null)
            {
                if (!waitForAllThreads)
                    break;

                boolean allThreadsFinished = true;
                for (int currRespCode : responseCode)
                {
                    if (currRespCode == -1)
                    {
                        allThreadsFinished = false;
                        break;
                    }
                }

                if (allThreadsFinished)
                    break;
            }
            Util.sleep(pollInterval);
        }

        return responseCode;
    }

    /**
     * Abort request threads from further processing of response body.
     */
    public void abortResponseBodyProcessing()
    {
        if (requestManager == null)
            return;
        requestManager.abortResponseBodyProcessing();
    }

    private void updateArgWithHeaders(List<String> listArg, String header[])
    {
        for (String currHeader : header)
        {
            listArg.add("-hdr");
            listArg.add(currHeader);
        }
    }

    /**
     * Add arguments in {@code arg} to argument list.
     * 
     * @param arg Array of arguments
     */
    private static List<String> getArgAsList(String arg[])
    {
        List<String> listArg = new ArrayList<String>();
        listArg.addAll(Arrays.asList(arg));
        return listArg;
    }

    /**
     * A Context class holds context that
     * <ul>
     * <li>Specific to a given JReq instance
     * <li>Needs to be shared with other instances like RequestManager or SingleClient.
     * <li>Can be ONLY modified by JReq instance.
     * </ul>
     */
    static class Context
    {
        private Logger logger = JReq.DEFAULT_LOGGER;

        private String clientId = UUID.randomUUID().toString();

        private Map<String, String> mapCookie = new Hashtable<String, String>();

        private Context()
        {
        }

        /* Private methods/setters */

        private void clearCookies()
        {
            mapCookie.clear();
        }

        private void setLogger(Logger logger)
        {
            this.logger = logger;
        }

        /* Public getters */

        /**
         * @return Logger set by client OR default JReq logger.
         */
        public Logger getLogger()
        {
            return logger;
        }

        /**
         * @return Unique ID for JReq instance.
         */
        public String getClientId()
        {
            return clientId;
        }

        /**
         * @return Map where cookie name is mapped to its value.
         */
        public Map<String, String> getCookies()
        {
            return mapCookie;
        }
    }

    /**
     * A class to build JReq arguments 
     * @author CafeDuke
     */
    public static class ArgBuilder
    {
        private List<String> listArg = new ArrayList<>();

        /**
         * Create an instance of ArgBuilder
         */
        private ArgBuilder()
        {

        }

        /**
         * @return this ArgBuilder instance
         */
        public List<String> build()
        {
            return listArg;
        }

        /**
         * @param url URL to be requested
         * @return this ArgBuilder instance
         */
        public ArgBuilder url(String url)
        {
            listArg.add("-u");
            listArg.add(url);
            return this;
        }

        /**
         * @param host Host name
         * @return this ArgBuilder instance
         */
        public ArgBuilder host(String host)
        {
            listArg.add("-h");
            listArg.add(host);
            return this;
        }

        /**
         * @param port Port number
         * @return this ArgBuilder instance
         */
        public ArgBuilder port(int port)
        {
            listArg.add("-p");
            listArg.add(String.valueOf(port));
            return this;
        }

        /**
         * @param login Login for authentication
         * @return this ArgBuilder instance
         */
        public ArgBuilder login(String login)
        {
            listArg.add("-login");
            listArg.add(login);
            return this;
        }

        /**
         * @param password Password for authentication
         * @return this ArgBuilder instance
         */
        public ArgBuilder password(String password)
        {
            listArg.add("-password");
            listArg.add(password);
            return this;
        }

        /**
         * @param server The proxy server to be used. 
         * @return this ArgBuilder instance
         */
        public ArgBuilder proxy(String server)
        {
            listArg.add("-proxy");
            listArg.add(server);
            return this;
        }

        /**
         * @param auth Authentication for proxy. Format {@code Username:Password}
         * @return this ArgBuilder instance
         */
        public ArgBuilder proxyAuth(String auth)
        {
            listArg.add("-proxyAuth");
            listArg.add(auth);
            return this;
        }

        /**
         * @param keystore The JKS keystore to be used by client
         * @return this ArgBuilder instance
         */
        public ArgBuilder keystore(String keystore)
        {
            listArg.add("-keystore");
            listArg.add(keystore);
            return this;
        }

        /**
         * @param storepass The password for JKS keystore
         * @return this ArgBuilder instance
         */
        public ArgBuilder storepass(String storepass)
        {
            listArg.add("-storepass");
            listArg.add(storepass);
            return this;
        }

        /**
         * @param file Output file to save response.
         * @return this ArgBuilder instance
         */
        public ArgBuilder outputToFile(String file)
        {
            listArg.add("-o");
            listArg.add(file);
            return this;
        }

        /**
         * @param file Output file to save response headers.
         * @return this ArgBuilder instance
         */
        public ArgBuilder outputHeadersToFile(String file)
        {
            listArg.add("-ho");
            listArg.add(file);
            return this;
        }

        /**
         * @return this ArgBuilder instance
         */
        public ArgBuilder outputReponseCodeToFile()
        {
            listArg.add("-rco");
            return this;
        }

        /**
         * @param file Output file to append response.
         * @return this ArgBuilder instance
         */
        public ArgBuilder appendToFile(String file)
        {
            listArg.add("-a");
            listArg.add(file);
            return this;
        }

        /**
         * Do not output response
         * @return this ArgBuilder instance
         */
        public ArgBuilder quiet()
        {
            listArg.add("-q");
            return this;
        }

        /**
         * @param header Request headers to be added
         * @return this ArgBuilder instance
         */
        public ArgBuilder header(String... header)
        {
            for (String curr : header)
            {
                listArg.add("-hdr");
                listArg.add(curr);
            }
            return this;
        }

        /**
         * @param file The file containing request headers, one per line.
         * @return this ArgBuilder instance
         */
        public ArgBuilder headerFile(String... file)
        {
            listArg.add("-rqh");
            listArg.add(Util.join(file, ','));
            return this;
        }

        /**
         * @param postBody Post body content.
         * @return this ArgBuilder instance
         */
        public ArgBuilder postBody(String postBody)
        {
            listArg.add("-pb");
            listArg.add(postBody);
            return this;
        }

        /**
         * @param file File containing post body.
         * @return this ArgBuilder instance
         */
        public ArgBuilder postBodyFile(String file)
        {
            listArg.add("-pbf");
            listArg.add(file);
            return this;
        }

        /**
         * @param header Show headers in response
         * @return this ArgBuilder instance
         */
        public ArgBuilder showHeader(String... header)
        {
            listArg.add("-sph");
            listArg.add(Util.join(header, ','));
            return this;
        }

        /**
         * @param chunkSize Chunk size to be used.
         * @return this ArgBuilder instance
         */
        public ArgBuilder chunkSize(int chunkSize)
        {
            listArg.add("-chunklen");
            listArg.add(String.valueOf(chunkSize));
            return this;
        }

        /**
         * @param milli Sleep in milliseconds after sending each byte of request
         * @return this ArgBuilder instance
         */
        public ArgBuilder sleepPerByteSend(int milli)
        {
            listArg.add("-byteSendDelay");
            listArg.add(String.valueOf(milli));
            return this;
        }

        /**
         * @param milli Sleep in milliseconds after receiving each byte of response
         * @return this ArgBuilder instance
         */
        public ArgBuilder sleepPerByteReceive(int milli)
        {
            listArg.add("-byteReceiveDelay");
            listArg.add(String.valueOf(milli));
            return this;
        }

        /**
         * @param threadCount Number of threads to be spawned.
         * @return this ArgBuilder instance
         */
        public ArgBuilder threadCount(int threadCount)
        {
            listArg.add("-n");
            listArg.add(String.valueOf(threadCount));
            return this;
        }

        /**
         * @param repeatCount Number of times each thread repeats the request.
         * @return this ArgBuilder instance
         */
        public ArgBuilder repeatCount(int repeatCount)
        {
            listArg.add("-r");
            listArg.add(String.valueOf(repeatCount));
            return this;
        }

        /**
         * For details, see {@link ArgProcessor.MultiThreadMode}
         * @param mode The parallel mode to be used.
         * @return this ArgBuilder instance
         */
        public ArgBuilder mode(MultiThreadMode mode)
        {
            listArg.add("-mode");
            listArg.add(mode.name());
            return this;
        }

        /**
         * @return this ArgBuilder instance
         */
        public ArgBuilder recordMetaData()
        {
            listArg.add("-meta");
            return this;
        }

        /**
         * @param timeout Socket timeout
         * @return this ArgBuilder instance
         */
        public ArgBuilder timeoutSocket(int timeout)
        {
            listArg.add("-socketTimeout");
            listArg.add(String.valueOf(timeout));
            return this;
        }

        /**
         * @param timeout Response body timeout
         * @return this ArgBuilder instance
         */
        public ArgBuilder timeoutResponseBody(int timeout)
        {
            listArg.add("-respBodyTimeout");
            listArg.add(String.valueOf(timeout));
            return this;
        }

        /**
         * For details, see {@link ArgProcessor.HttpMethod#GET}
         * @return this ArgBuilder instance
         */
        public ArgBuilder doGet()
        {
            listArg.add("-get");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor.HttpMethod#POST}
         * @return this ArgBuilder instance
         */
        public ArgBuilder doPost()
        {
            listArg.add("-post");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor.HttpMethod#HEAD}
         * @return this ArgBuilder instance
         */
        public ArgBuilder doHead()
        {
            listArg.add("-head");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor.HttpMethod#PUT}
         * @return this ArgBuilder instance
         */
        public ArgBuilder doPut()
        {
            listArg.add("-put");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor.HttpMethod#DELETE}
         * @return this ArgBuilder instance
         */
        public ArgBuilder doDelete()
        {
            listArg.add("-delete");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor.HttpMethod#TRACE}
         * @return this ArgBuilder instance
         */
        public ArgBuilder doTrace()
        {
            listArg.add("-trace");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor.HttpMethod#OPTIONS}
         * @return this ArgBuilder instance
         */
        public ArgBuilder doOptions()
        {
            listArg.add("-options");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor#SHOW_HEADER}
         * @return this ArgBuilder instance
         */
        public ArgBuilder showHeaders()
        {
            listArg.add("-sh");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor#SHOW_ALL_HEADER}
         * @return this ArgBuilder instance
         */
        public ArgBuilder showAllHeaders()
        {
            listArg.add("-sah");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor#DISABLE_FOLLOW_REDIRECT}
         * @return this ArgBuilder instance
         */
        public ArgBuilder dontFollowRedirect()
        {
            listArg.add("-disableFollowRedirect");
            return this;
        }

        /**
         * Requests are blocking by default.
         * For details, see {@link ArgProcessor#NON_BLOCKING_REQUEST}
         * @return this ArgBuilder instance
         */
        public ArgBuilder dontWaitForResponse()
        {
            listArg.add("-nonBlock");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor#DISABLE_ERROR_LOG}
         * @return this ArgBuilder instance
         */
        public ArgBuilder dontLogError()
        {
            listArg.add("-disableErrorLog");
            return this;
        }

        /**
         * For details, see {@link ArgProcessor#DISABLE_CLIENT_ID}
         * @return this ArgBuilder instance
         */
        public ArgBuilder dontSendClientId()
        {
            listArg.add("-disableClientId");
            return this;
        }
    }

}
