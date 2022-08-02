package com.github.cafeduke;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.github.cafeduke.common.ToolsUtil;
import com.github.cafeduke.common.Util;

/**
 * A Java based HTTP Client that exposes APIs to send multiple sequential/simultaneous requests and optionally capture the response body/headers.
 *
 * <ul>
 * <li>Fireduke is capable of sending requests of all request method types (Get, Post, Head, Delete, Put, Trace and Options).
 * <li>Fireduke can operate in the following threading mode.
 * <ul>
 * <li>Single Client (SC) - A single thread is used to send requests sequentially.
 * <li>Multiple Similar Clients (MSC): Spawns multiple threads, all threads requesting the same URL.
 * <li>Multiple Unique Clients (MUC): Spawns multiple threads, each thread picks a URL from an input URL file.
 * </ul>
 * <li>Each Fireduke thread can send its own set of request headers and/or request body.
 * <li>Fireduke can optionally maintain cookie based session.
 * </ul>
 * 
 * @author Raghunandan.Seshadri
 */
public class Fireduke
{
    /**
     * The request header name which will have a unique value for every Fireduke instance.
     */
    public static final String CLIENT_ID_HEADER = "ClientId";

    /**
     * In case of Non-blocking requests, this timeout specifies the max number of seconds
     * to wait for all threads to return with response code.
     */
    public static final int TIMEOUT_RESPONSE_CODE = 5 * 60;

    /**
     * Request Manager for this Fireduke instance
     */
    private RequestManager requestManager = null;

    /**
     * List of Fireduke Arguments used by all send methods.
     */
    private List<String> listCommonArg = new ArrayList<String>();

    /**
     * Default Fireduke Logger
     */
    private static final Logger DEFAULT_LOGGER = ToolsUtil.getLogger(Fireduke.class.getName(), "Fireduke.log");

    /**
     * Fireduke context
     */
    private final Context context = new Context();

    static
    {
        setHTTPProperties();
    }

    private Fireduke()
    {
        context.setLogger(DEFAULT_LOGGER);
        setSessionBinding(false);
    }

    /**
     * Get a new instance of Fireduke.
     * 
     * Every new instance of Fireduke sends a unique request ID. This helps origin server differentiate
     * among requests from different instances.
     * 
     * @return Fireduke instance
     */
    public static Fireduke getInstance()
    {
        return new Fireduke();
    }

    /**
     * Main method to enable Fireduke to also run as a stand alone HTTP client.
     * 
     * @param arg Arguments to main.
     * @throws Exception An exception (if any) sending request.
     */
    public static void main(String arg[]) throws Exception
    {
        Fireduke.getInstance().sendRequest(arg);
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
     * Send request using {@link ArgBuilder}
     * 
     * @param builder Send HTTP(S) request
     * @return Instance of ArgBuilder
     */
    public int[] sendRequest(ArgBuilder builder)
    {
        return sendRequest(builder.build());
    }

    /**
     * Send request with arguments {@code arg}
     * 
     * @param arg Arguments for Fireduke
     * @return Array of response codes.
     */
    public int[] sendRequest(String arg[])
    {
        return sendRequest(Fireduke.getArgAsList(arg));
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
        context.logger.info("Executing Fireduke: " + Util.join(listArg, ' '));

        requestManager = new RequestManager(context, listArg.toArray(new String[0]));
        return requestManager.sendRequest();
    }

    /* ------------------------------------------------------------------- */
    /* Fireduke Getters/Setters                                                */
    /* ------------------------------------------------------------------- */

    /**
     * Set a custom logger for Fireduke
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
     * default, Fireduke APIs follow redirects automatically.
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
     * Sets the blocking mode. By default, Fireduke APIs are blocking.
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
     * <li>Specific to a given Fireduke instance
     * <li>Needs to be shared with other instances like RequestManager or SingleClient.
     * <li>Can be ONLY modified by Fireduke instance.
     * </ul>
     */
    static class Context
    {
        private Logger logger = Fireduke.DEFAULT_LOGGER;

        private String clientId = UUID.randomUUID().toString();

        private Map<String, String> mapCookie = new Hashtable<String, String>();

        private HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();

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
         * @return Logger set by client OR default Fireduke logger.
         */
        public Logger getLogger()
        {
            return logger;
        }

        /**
         * @return Unique ID for Fireduke instance.
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

        /**
         * @return The Builder used to build the HttpClient that shall be used by all SingleClients.
         */
        public HttpClient.Builder getHttpClientBuilder()
        {
            return httpClientBuilder;
        }
    }

}
