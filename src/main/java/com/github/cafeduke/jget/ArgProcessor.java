package com.github.cafeduke.jget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.github.cafeduke.jget.common.Util;

/**
 * This class encapsulates JGet arguments.
 * Handles the processing of arguments with includes parsing,
 * validation and reporting invalid usage.
 *
 * @author Raghunandan.Seshadri
 */
public class ArgProcessor
{
    /**
     * An enumeration of standard HTTP Headers supported using arguments.
     */
    public enum HttpHeader
    {
        /**
         * HTTP header -- Host
         */
        HOST,

        /**
         * HTTP header -- Cookie
         */
        COOKIE,

        /**
         * HTTP header -- Accept-Encoding
         */
        ACCEPT_ENCODING,

        /**
         * HTTP header -- If-None-Match
         */
        IF_NONE_MATCH,

        /**
         * HTTP header -- If-Modified-Since
         */
        IF_MODIFIED_SINCE,

        /**
         * HTTP header -- Range
         */
        RANGE,

        /**
         * HTTP header -- UserAgent
         */
        USER_AGENT,

        /**
         * HTTP header -- ClientCert
         */
        CLIENT_CERT,

        /**
         * HTTP header -- KeepAlive
         */
        KEEP_ALIVE;

        @Override
        public String toString()
        {
            switch (this)
            {
                case HOST:
                    return "Host";
                case COOKIE:
                    return "Cookie";
                case ACCEPT_ENCODING:
                    return "Accept-Encoding";
                case IF_NONE_MATCH:
                    return "If-None-Match";
                case IF_MODIFIED_SINCE:
                    return "If-Modified-Since";
                case RANGE:
                    return "Range";
                case USER_AGENT:
                    return "User-Agent";
                case CLIENT_CERT:
                    return "SSL-Client-Cert";
                case KEEP_ALIVE:
                    return "Connection";
                default:
                    throw new IllegalArgumentException("Uknown option " + this);
            }
        }

        /**
         * @return Return the JGet switch/options for the HTTP header
         */
        public String toArg()
        {
            switch (this)
            {
                case HOST:
                    return "-H|-hostHeader";
                case COOKIE:
                    return "-c|-cookie";
                case ACCEPT_ENCODING:
                    return "-e|-encoding";
                case IF_NONE_MATCH:
                    return "-inm";
                case IF_MODIFIED_SINCE:
                    return "-ims";
                case RANGE:
                    return "-R|-range";
                case USER_AGENT:
                    return "-b|-browser";
                case CLIENT_CERT:
                    return "-cert|-clientCert";
                case KEEP_ALIVE:
                    return "-k|-keepAlive";
                default:
                    throw new IllegalArgumentException("Uknown option " + this);
            }
        }
    }

    /**
     * An enumeration of standard HTTP Methods supported using arguments.
     */
    public static enum HttpMethod
    {
        /**
         * HTTP method -- Get
         */
        GET,

        /**
         * HTTP method -- Post
         */
        POST,

        /**
         * HTTP method -- Head
         */
        HEAD,

        /**
         * HTTP method -- Put
         */
        PUT,

        /**
         * HTTP method -- Delete
         */
        DELETE,

        /**
         * HTTP method -- Trace
         */
        TRACE,

        /**
         * HTTP method -- Options
         */
        OPTIONS;

        /**
         * @return Return the JGet switch/options for the HTTP method
         */
        public String toArg()
        {
            switch (this)
            {
                case GET:
                    return "-get";
                case POST:
                    return "-P|-post";
                case HEAD:
                    return "-head";
                case PUT:
                    return "-put";
                case DELETE:
                    return "-delete";
                case TRACE:
                    return "-trace";
                case OPTIONS:
                    return "-options";
                default:
                    throw new IllegalArgumentException("Uknown option " + this);
            }
        }
    }

    /**
     * An enumeration of output modes.
     */
    public static enum OutputMode
    {
        /**
         * Write output to standard output
         */
        STDOUT,

        /**
         * Write output to file
         */
        FILE_WRITE,

        /**
         * Append file
         */
        FILE_APPEND,

        /**
         * Quiet mode -- do not output
         */
        QUIET;

        /**
         * @return Return the JGet switch/options for the output mode
         */
        public String toArg()
        {
            switch (this)
            {
                case STDOUT:
                    return "-s|-stdout";
                case FILE_WRITE:
                    return "-o|-output";
                case FILE_APPEND:
                    return "-a|-append";
                case QUIET:
                    return "-q|-quiet";
                default:
                    throw new IllegalArgumentException("Uknown argument " + this);
            }
        }

        @Override
        public String toString()
        {
            switch (this)
            {
                case STDOUT:
                    return "Standard ouput";
                case FILE_WRITE:
                    return "Overwrite file";
                case FILE_APPEND:
                    return "Append file";
                case QUIET:
                    return "Quiet mode";
                default:
                    throw new IllegalArgumentException("Uknown argument " + this);
            }
        }
    }

    /**
     * An enumeration of multiple thread modes.
     * <ul>
     * <li>SC = Single Client
     * <li>MSC = Multiple Similar Clients
     * <li>MUC = Multiple Unique Clients
     * </ul>
     */
    public static enum MultiThreadMode
    {
        /**
         * Single thread {@link SingleClient Client}
         */
        SC,

        /**
         * Multiple Similar {@link SingleClient Clients}
         */
        MSC,

        /**
         * Multiple Unique {@link SingleClient Clients}
         */
        MUC;
    }

    /**
     * Show usage and exit
     */
    public static final String HELP = "-h|-help";
    boolean showHelp = false;

    /**
     * The JGet version as per pom.xml
     */
    public static final String JGET_VERSION = "-v|-version";

    /**
     * The JGet version string
     */
    public static final String JGET_VERSION_STRING = "1.4";
    private boolean showVersion = false;

    /**
     * The HTTP Protocol version preferred by the client
     */
    public static final String HTTP_PROTOCOL_VERSION = "-http";
    HttpClient.Version httpVersion = HttpClient.Version.HTTP_1_1;

    /**
     * URL to be requested
     */
    public static final String URL = "-u";
    URL url;

    /**
     * File having URLs or URIs
     * <br>
     * URL format: {@code http[s]://<host>:<port>/<path>}
     * <br>
     * URI format: {@code /<prefix>}
     */
    public static final String URI_FILE = "-f";
    File fileURI = null;

    /**
     * Host name
     */
    public static final String HOST = "-h|-host";
    String host = null;

    /**
     * Port number
     */
    public static final String PORT = "-p|-port";
    Long port = null;

    /**
     * URI
     * <br>
     * URI format: {@code /<path>}
     */
    public static final String URI = "-uri";
    String uri;

    /**
     * Map of request headers to values
     */
    Hashtable<String, String> mapHeaderValue = new Hashtable<String, String>();

    /**
     * Request headers.
     */
    public static final String REQUEST_HEADER = "-hdr|-header";
    List<String> listHeader = new ArrayList<String>();

    /**
     * File(s) having request header(s)
     * <br>
     * Multiple files can be provided in MultiThreadMode.
     */
    public static final String REQUEST_HEADER_FILE = "-rqh|-requestHeaderFile";
    List<File> listRequestHeaderFile = new ArrayList<File>();

    /**
     * Login name for authentication.
     */
    public static final String LOGIN_NAME = "-login";
    String login = null;

    /**
     * Password for authentication.
     */
    public static final String PASSWORD = "-password";
    String password = null;

    /**
     * Proxy host name.
     */
    public static final String PROXY_HOST = "-proxyHost|-proxy";
    String proxyHost = null;

    /**
     * Proxy authentication.
     */
    public static final String PROXY_AUTH = "-proxyAuth";
    String proxyAuth = null;

    /**
     * HTTP Method used for the request.
     */
    HttpMethod httpMethod = HttpMethod.GET;

    /**
     * Post body.
     */
    public static final String POST_BODY = "-pb|-postBody";
    String postBody = null;

    /**
     * Enable chunked request
     */
    public static final String CHUNK_LEN = "-chunklen";
    int chunkLen = -1;

    /**
     * Number of milliseconds to sleep after sending each byte of post body request.
     */
    public static final String POST_BODY_BYTE_SEND_DELAY = "-byteSendDelay";
    long byteSendDelay = 0;

    /**
     * Number of milliseconds to sleep after receiving each byte of response body.
     */
    public static final String POST_BODY_BYTE_RECEIVE_DELAY = "-byteReceiveDelay";
    long byteReceiveDelay = 0;

    /**
     * File(s) having post body.
     * <br>
     * Multiple files can be provided in MultiThreadMode.
     */
    public static final String POST_BODY_FILE = "-pbf|-postBodyFile";
    List<File> listPostBodyFile = new ArrayList<File>();

    /**
     * If set, the request is sent using SSL.
     */
    public static final String SSL = "-ssl";
    boolean isSSL = false;

    /**
     * A Java key store having client certificates.
     */
    public static final String KEYSTORE = "-keystore";
    File fileKeystore = null;

    /**
     * A Java key store password.
     */
    public static final String KEYSTORE_PASSWORD = "-storepass";
    String passwordKeyStore = null;

    /**
     * Ciphers (comma separated list) to be used by JGet
     */
    String ciphers = null;
    public static final String CIPHERS = "-ciphers";

    /**
     * TLS version to be used by JGet.
     * Version: 1.3|1.2|1.1|1
     */
    String tlsVersion = null;
    public static final String TLS_VERSION = "-tls";

    /**
     * The mode for writing HTTP response.
     */
    public static final String HEADER_OUTPUT_FILE = "-ho|-headerOutput";
    OutputMode outputMode = OutputMode.STDOUT;

    /**
     * File into which HTTP response shall be written.
     */
    String outputFile = null;

    /**
     * File into which HTTP response headers shall be written.
     */
    String outputFileHeader = null;

    /**
     * File having the response codes for the request sent.
     */
    public static final String RESPONSE_CODE_OUTPUT = "-rco|-respCodeOutput";
    File fileRespCode = null;

    /**
     * If true, response headers will be included in the output.
     */
    public static final String SHOW_HEADER = "-sh|-showHeader";
    boolean showResponseHeader = false;

    /**
     * If true, all response headers will be included in the output.
     */
    public static final String SHOW_ALL_HEADER = "-sah|-showAllHeader";
    boolean showAllResponseHeader = false;

    /**
     * Only the response headers mentioned in the list will be included in the output.
     */
    public static final String SHOW_PARTICULAR_HEADER = "-sph";
    List<String> listParticularHeader = new ArrayList<String>();

    /**
     * Specifies the current mode of multi-threading.
     */
    public static final String MULTI_THREAD_MODE = "-mode";
    MultiThreadMode multiThreadMode = MultiThreadMode.SC;

    /**
     * Number of threads to be spawned.
     */
    public static final String THREAD_COUNT = "-n|-threadCount";
    int threadCount = 1;

    /**
     * Number of requests to be issued per thread.
     */
    public static final String THREAD_REPEAT_COUNT = "-r|-repeat";
    int repeatCountPerThread = 1;

    /**
     * If true, the turn around time of each thread is recorded.
     */
    public static final String RECORD_META_DATA = "-meta|-metaData";
    boolean recordMetaData = false;

    /**
     * Default socket {@link java.net.http.HttpRequest.Builder#timeout(java.time.Duration) timeout}.
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 2 * 60 * 60 * 1000;

    /**
     * Socket {@link java.net.http.HttpRequest.Builder#timeout(java.time.Duration) timeout}.
     */
    public static final String SOCKET_TIMEOUT = "-socketTimeout";
    int timeoutSocket = DEFAULT_SOCKET_TIMEOUT;

    /**
     * Default timeout after which the thread will stop reading response.
     */
    public static final int DEFAULT_RESPONSE_BODY_TIMEOUT = 2 * 60 * 60 * 1000;

    /**
     * Timeout after which the thread will stop reading response.
     */
    public static final String RESPONSE_BODY_TIMEOUT = "-respBodyTimeout";

    int timeoutRespBody = DEFAULT_RESPONSE_BODY_TIMEOUT;

    /**
     * If true, HTTP redirects (requests with response code 3xx) will be automatically followed.
     */
    public static final String DISABLE_FOLLOW_REDIRECT = "-disableFollowRedirect";
    boolean followRedirect = true;

    /**
     * If true, wait until all threads have finished execution or have timed out.
     */
    public static final String NON_BLOCKING_REQUEST = "-nonBlock";
    boolean blockRequest = true;

    private String arg[] = null;

    /**
     * If true, a unique client ID is generated for every JGet instance
     * and is sent as a request header.
     */
    public static final String DISABLE_CLIENT_ID = "-disableClientId";
    boolean sendClientId = true;

    /**
     * If true, exception during JGet request is logged.
     */
    public static final String DISABLE_ERROR_LOG = "-disableErrorLog";
    boolean enableErrorLog = true;

    /**
     * Enable session binding. Only available for API mode.
     */
    public static final String ENABLE_SESSION_BINDING = "-sessionBinding";
    boolean enableSessionBinding = false;

    /**
     * Process arguments and validate usage
     *
     * @param arg arguments to process
     */
    public ArgProcessor(String arg[])
    {
        this.arg = arg;
    }

    /**
     * @return Returns true if argument processing is successful, false otherwise
     * @throws KeyManagementException Exceptions parsing arguments
     * @throws KeyStoreException Exceptions parsing arguments
     * @throws NoSuchAlgorithmException Exceptions parsing arguments
     * @throws CertificateException Exceptions parsing arguments
     * @throws IOException Exceptions parsing arguments
     */
    public boolean processArg() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        parseArg();

        if (arg.length == 0 || showHelp == true)
        {
            usage();
            return false;
        }

        if (showVersion == true)
        {
            System.out.println("JGet HTTP Client v" + JGET_VERSION_STRING);
            return false;
        }

        parseSystemArg();
        validateUsage();
        return true;
    }

    private void parseArg() throws MalformedURLException, FileNotFoundException
    {
        HttpHeader currHeader = null;
        HttpMethod currMethod = null;
        OutputMode currOutputMode = null;

        for (int index = 0; index < arg.length; index++)
        {
            String currArg = arg[index];

            if (currArg.matches(JGET_VERSION))
            {
                showVersion = true;
                index++;
            }
            else if (currArg.matches(HELP))
            {
                showHelp = true;
                index++;
            }
            else if (currArg.matches(URL))
            {
                url = validateArgUrl(arg, index++);
                if (url.getProtocol().equalsIgnoreCase("https"))
                    isSSL = true;
            }
            else if (currArg.matches(URI_FILE))
            {
                fileURI = validateArgFile(arg, index++);
            }
            else if (currArg.matches(HOST))
            {
                host = Util.getSwitchValue(arg, index++);
            }
            else if (currArg.matches(PORT))
            {
                port = validateArgLong(arg, index++);
            }
            else if (currArg.matches(URI))
            {
                uri = Util.getSwitchValue(arg, index++);
            }
            else if ((currHeader = isHttpHeader(currArg)) != null)
            {
                if (currHeader == HttpHeader.RANGE)
                    mapHeaderValue.put(currHeader.toString(), "bytes=" + Util.getSwitchValue(arg, index++));
                else if (currHeader == HttpHeader.KEEP_ALIVE)
                    mapHeaderValue.put(currHeader.toString(), "Keep-Alive");
                else
                    mapHeaderValue.put(currHeader.toString(), Util.getSwitchValue(arg, index++));
            }
            else if (currArg.matches(LOGIN_NAME))
            {
                login = Util.getSwitchValue(arg, index++);
            }
            else if (currArg.matches(PASSWORD))
            {
                password = Util.getSwitchValue(arg, index++);
            }
            else if (currArg.matches(PROXY_HOST))
            {
                proxyHost = Util.getSwitchValue(arg, index++);
            }
            else if (currArg.matches(PROXY_AUTH))
            {
                proxyAuth = Util.getSwitchValue(arg, index++);
            }
            else if ((currMethod = isHttpMethod(currArg)) != null)
            {
                httpMethod = currMethod;
            }
            else if (currArg.matches(POST_BODY))
            {
                httpMethod = HttpMethod.POST;
                postBody = Util.getSwitchValue(arg, index++);
            }
            else if (currArg.matches(POST_BODY_FILE))
            {
                httpMethod = HttpMethod.POST;
                listPostBodyFile = validateArgFiles(arg, index++);
            }
            else if (currArg.matches(CHUNK_LEN))
            {
                chunkLen = validateArgInteger(arg, index++);
            }
            else if (currArg.matches(POST_BODY_BYTE_SEND_DELAY))
            {
                byteSendDelay = validateArgInteger(arg, index++);
            }
            else if (currArg.matches(SSL))
            {
                isSSL = true;
            }
            else if (currArg.matches(KEYSTORE))
            {
                fileKeystore = validateArgFile(arg, index++);
            }
            else if (currArg.matches(KEYSTORE_PASSWORD))
            {
                passwordKeyStore = Util.getSwitchValue(arg, index++);
            }
            else if (currArg.matches(CIPHERS))
            {
                ciphers = Util.getSwitchValue(arg, index++);
            }
            else if (currArg.matches(TLS_VERSION))
            {
                tlsVersion = Util.getSwitchValue(arg, index++);
            }
            else if ((currOutputMode = isOutputMode(currArg)) != null)
            {
                outputMode = currOutputMode;
                if (currOutputMode == OutputMode.FILE_WRITE || currOutputMode == OutputMode.FILE_APPEND)
                    outputFile = Util.getSwitchValue(arg, index++);
            }
            else if (currArg.matches(THREAD_REPEAT_COUNT))
            {
                repeatCountPerThread = validateArgInteger(arg, index++);
            }
            else if (currArg.matches(HEADER_OUTPUT_FILE))
            {
                outputFileHeader = Util.getSwitchValue(arg, index++);
            }
            else if (currArg.matches(RESPONSE_CODE_OUTPUT))
            {
                fileRespCode = new File(Util.getSwitchValue(arg, index++));
            }
            else if (currArg.matches(SHOW_HEADER))
            {
                showResponseHeader = true;
            }
            else if (currArg.matches(SHOW_ALL_HEADER))
            {
                showResponseHeader = true;
                showAllResponseHeader = true;
            }
            else if (currArg.matches(RECORD_META_DATA))
            {
                recordMetaData = true;
            }
            else if (currArg.matches(MULTI_THREAD_MODE))
            {
                multiThreadMode = Enum.valueOf(MultiThreadMode.class, Util.getSwitchValue(arg, index++));
            }
            else if (currArg.matches(THREAD_COUNT))
            {
                threadCount = validateArgInteger(arg, index++);
            }
            else if (currArg.matches(REQUEST_HEADER))
            {
                listHeader.add(Util.getSwitchValue(arg, index++));
            }
            else if (currArg.matches(REQUEST_HEADER_FILE))
            {
                listRequestHeaderFile = validateArgFiles(arg, index++);
            }
            else if (currArg.matches(SHOW_PARTICULAR_HEADER))
            {
                showResponseHeader = true;
                listParticularHeader = validateArgStrings(arg, index++);
            }
            else if (currArg.matches(SOCKET_TIMEOUT))
            {
                timeoutSocket = validateArgInteger(arg, index++);
            }
            else if (currArg.matches(RESPONSE_BODY_TIMEOUT))
            {
                timeoutRespBody = validateArgInteger(arg, index++);
            }
            else if (currArg.matches(DISABLE_FOLLOW_REDIRECT))
            {
                followRedirect = false;
            }
            else if (currArg.matches(NON_BLOCKING_REQUEST))
            {
                blockRequest = false;
            }
            else if (currArg.matches(DISABLE_CLIENT_ID))
            {
                sendClientId = false;
            }
            else if (currArg.matches(DISABLE_ERROR_LOG))
            {
                enableErrorLog = false;
            }
            else if (currArg.matches(ENABLE_SESSION_BINDING))
            {
                enableSessionBinding = true;
            }
            else if (currArg.matches(HTTP_PROTOCOL_VERSION))
            {
                String version = Util.getSwitchValue(arg, index++);
                if (version.equals("1.1"))
                    httpVersion = HttpClient.Version.HTTP_1_1;
                else if (version.equals("2") || version.equals("2.0"))
                    httpVersion = HttpClient.Version.HTTP_2;
                else
                    dieUsage("Option " + HTTP_PROTOCOL_VERSION + " must be 2 or 1.1 CurrentValue=" + version);
            }
            else
            {
                dieUsage("Unknown argument '" + currArg + "'");
            }
        }
    }

    private void parseSystemArg()
    {
        String propValue = null;
        if ((propValue = System.getProperty("javax.net.ssl.keyStore")) != null)
            fileKeystore = new File(propValue);

        if ((propValue = System.getProperty("javax.net.ssl.keyStorePassword")) != null)
            passwordKeyStore = propValue;
    }

    /* ______ End of Parsing Arguments ______ */

    /**
     * Validate Usage of arguments and flag error for invalid usage.
     *
     * @throws IOException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private void validateUsage() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, KeyManagementException
    {
        if (fileURI == null)
        {
            if (url == null)
            {
                if (host == null || port == null || uri == null)
                    dieUsage("MUST specify ONE among (1) URL, (2) Host, Port and URI (3)URI file");
            }
            else
            {
                if (host != null || port != null || uri != null)
                    dieUsage("Specify only ONE among (1) URL, (2) Host, Port and URI (3)URI file");
            }
        }

        if (outputFileHeader != null)
            if (!showResponseHeader)
                dieUsage("Show header " + SHOW_HEADER + " MUST be specifed for " + HEADER_OUTPUT_FILE + " option ");

        /**
         * In case of POST (-P), - If both post body file (-pbf) and post body
         * (-pb) is specified log error and exit - If neither of them is specified
         * then default to (-pb) with "" as post body
         */
        if (httpMethod == HttpMethod.POST)
        {
            if (postBody != null && listPostBodyFile.size() > 0)
                dieUsage("Only ONE among postbody (" + POST_BODY + ") OR post body file (" + POST_BODY_FILE + ") has to be used");

            if (postBody == null && (listPostBodyFile == null || listPostBodyFile.size() == 0))
                postBody = "";
        }

        /* Thread modes */
        if (multiThreadMode == MultiThreadMode.MUC)
        {
            if (fileURI == null)
                dieUsage("Input URI file MUST be specified for mode " + MultiThreadMode.MUC);
        }

        if (multiThreadMode == MultiThreadMode.MSC || multiThreadMode == MultiThreadMode.MUC)
        {
            int numberOfThread = (multiThreadMode == MultiThreadMode.MSC) ? threadCount : FileUtils.readLines(fileURI, Charset.defaultCharset()).size();
            int numberOfPostBodyFile = listPostBodyFile.size();
            int numberOfRequestHeaderFile = listRequestHeaderFile.size();

            if (numberOfPostBodyFile > numberOfThread)
                dieUsage("Number of PostBodyFiles :" + numberOfPostBodyFile + " exceeds NumberOfThreads :" + numberOfThread);

            if (numberOfRequestHeaderFile > numberOfThread)
                dieUsage("Number of ReqHeaderFiles :" + numberOfRequestHeaderFile + " exceeds NumberOfThreads :" + numberOfThread);
        }

        if (url != null && url.getProtocol().equalsIgnoreCase("https"))
            isSSL = true;

        /* Keystore */
        if (isSSL)
        {
            /**
             * Note: ^ is an XOR operator
             * If a, b are two expressions then a ^ b is true when
             * - a == true b == false
             * - a == false b == true
             */
            if (fileKeystore == null ^ passwordKeyStore == null)
                dieUsage("Please specify both -keystore and -storepass OR omit both of them.");
        }
    }

    private HttpHeader isHttpHeader(String currArg)
    {
        for (HttpHeader currHeader : HttpHeader.values())
            if (currArg.matches(currHeader.toArg()))
                return currHeader;

        return null;
    }

    private HttpMethod isHttpMethod(String currArg)
    {
        for (HttpMethod currMethod : HttpMethod.values())
            if (currArg.matches(currMethod.toArg()))
                return currMethod;
        return null;
    }

    private OutputMode isOutputMode(String currArg)
    {
        for (OutputMode currOutputMode : OutputMode.values())
            if (currArg.matches(currOutputMode.toArg()))
                return currOutputMode;
        return null;
    }

    private static URL validateArgUrl(String arg[], int index) throws MalformedURLException
    {
        return new URL(Util.getSwitchValue(arg, index));
    }

    private static int validateArgInteger(String arg[], int index)
    {
        return Integer.parseInt(Util.getSwitchValue(arg, index));
    }

    private static Long validateArgLong(String arg[], int index)
    {
        return Long.parseLong(Util.getSwitchValue(arg, index));
    }

    private static List<String> validateArgStrings(String arg[], int index)
    {
        return Arrays.asList(Util.getSwitchValue(arg, index).split(","));
    }

    private static File validateArgFile(String arg[], int index) throws FileNotFoundException
    {
        File file = new File(Util.getSwitchValue(arg, index));
        if (!file.exists())
            throw new FileNotFoundException("File  " + file.getAbsolutePath() + " not found");
        return file;
    }

    private static List<File> validateArgFiles(String arg[], int index) throws FileNotFoundException
    {
        String filename[] = Util.getSwitchValue(arg, index).split(",");
        List<File> listFile = new ArrayList<File>();

        for (String currFilename : filename)
        {
            File file = new File(currFilename);
            if (!file.exists())
                throw new FileNotFoundException("File  " + file.getAbsolutePath() + " not found");
            listFile.add(file);
        }
        return listFile;
    }

    /**
     * ReportError, print the message and also usage
     *
     * @param mesg
     */
    private static void dieUsage(String mesg)
    {
        System.out.println("Usage Error :" + mesg);
        usage();
        throw new IllegalArgumentException("Usage Error: " + mesg);
    }

    /**
     * Print Usage and exit
     */
    private static void usage()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Usage:" + Util.LineSep);
        builder.append("java JGet" + Util.LineSep);
        builder.append("     [" + ArgProcessor.JGET_VERSION + "]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.URL + " <URL>]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.URI_FILE + " <File having URLs>]" + Util.LineSep);
        builder.append("     [" + Util.LineSep);
        builder.append("        " + ArgProcessor.HOST + " <Host name> " + ArgProcessor.PORT + " <Port>" + Util.LineSep);
        builder.append("        [" + ArgProcessor.URI_FILE + " <File having URIs>]" + Util.LineSep);
        builder.append("        [" + ArgProcessor.URI + " <URI>]" + Util.LineSep);
        builder.append("     ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.LOGIN_NAME + " <Login> " + ArgProcessor.PASSWORD + " <Password>]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.PROXY_HOST + " <ProxyHost:ProxyPort> ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.PROXY_AUTH + " <Username:Password> ]" + Util.LineSep);

        builder.append("     [" + Util.LineSep);
        builder.append("        " + ArgProcessor.SSL + Util.LineSep);
        builder.append("        " + ArgProcessor.KEYSTORE + " <Path to Java Key Store (JKS)>" + Util.LineSep);
        builder.append("        " + ArgProcessor.KEYSTORE_PASSWORD + " <Password to access JKS>" + Util.LineSep);
        builder.append("     ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.HTTP_PROTOCOL_VERSION + " <HTTP protocol version 2|1.1> ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.CIPHERS + " <cipher1>[,<cipher2>,<cipher3>...<cipherN>]]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.TLS_VERSION + " <tls version Eg: 1.3|1.2|1.1|1>]" + Util.LineSep);

        for (HttpHeader currHeader : HttpHeader.values())
            builder.append("     [" + currHeader.toArg() + " <" + currHeader.toString() + " header>]" + Util.LineSep);

        for (OutputMode currMode : OutputMode.values())
            builder.append("     [" + currMode.toArg() + " <" + currMode.toString() + ">]" + Util.LineSep);

        for (HttpMethod currMethod : HttpMethod.values())
            builder.append("     [" + currMethod.toArg() + " (Use HTTP method " + currMethod.toString() + ")]" + Util.LineSep);

        builder.append("     [" + ArgProcessor.POST_BODY + " <Post body>]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.CHUNK_LEN + " <Number of bytes each chunked request body should have> ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.POST_BODY_BYTE_SEND_DELAY + " <Time in milliseonds to sleep after sending each byte of post body> ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.POST_BODY_BYTE_RECEIVE_DELAY + " <Time in milliseonds to sleep after receiving each byte of response body> ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.POST_BODY_FILE + " <post1>[|<post2>|<post3>...<postN>]] (Files having post body)" + Util.LineSep);
        builder.append("     [(" + ArgProcessor.REQUEST_HEADER + " <Header name>:<Header value> )*] (Any number of occurence of header argument)" + Util.LineSep);
        builder.append("     [" + ArgProcessor.REQUEST_HEADER_FILE + " <req1>[,<req1>,<req3>...<reqN>]] (Files having request headers)" + Util.LineSep);
        builder.append("     [" + Util.LineSep);
        builder.append("        " + ArgProcessor.SHOW_HEADER + Util.LineSep);
        builder.append("        [" + Util.LineSep);
        builder.append("         " + ArgProcessor.SHOW_ALL_HEADER + " <Show All Headers>" + Util.LineSep);
        builder.append("         " + ArgProcessor.SHOW_PARTICULAR_HEADER + "<h1>[,<h2>,<h3>...<hN> (Show Perticular Headers)" + Util.LineSep);
        builder.append("        ]" + Util.LineSep);
        builder.append("        " + ArgProcessor.HEADER_OUTPUT_FILE + "<Filename to store response headers>" + Util.LineSep);
        builder.append("     ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.RESPONSE_CODE_OUTPUT + " <Filename to store response code per request>]" + Util.LineSep);

        builder.append("     [" + ArgProcessor.THREAD_COUNT + " <Number of threads>]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.THREAD_REPEAT_COUNT + " <Number of sequential repeated requests per thread>]" + Util.LineSep);

        String modeValue = " ";
        for (MultiThreadMode currMode : MultiThreadMode.values())
            modeValue = modeValue + currMode.name() + " | ";
        modeValue = modeValue.substring(0, modeValue.length() - 2);
        builder.append("     [" + ArgProcessor.MULTI_THREAD_MODE + modeValue + "]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.RECORD_META_DATA + " (Record meta data. Stored in <output file>.jget.properties)]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.SOCKET_TIMEOUT + " <Socket timeout in milliseconds for each thread> ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.RESPONSE_BODY_TIMEOUT + " <Timeout after which all threads shall abort processing of response body." + Util.LineSep);
        builder.append("                       Applicable with MSC/MUC only.>" + Util.LineSep);
        builder.append("     [" + ArgProcessor.DISABLE_FOLLOW_REDIRECT + " (Do not follow redirection. Default=false) ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.NON_BLOCKING_REQUEST + " (Send non-blocking request. Default=false) ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.DISABLE_ERROR_LOG + " (Disable logging error messages. Default=false) ]" + Util.LineSep);
        builder.append("     [" + ArgProcessor.DISABLE_CLIENT_ID + " (Do not send the OtdClientId header. Default=false)]" + Util.LineSep);
        System.out.println(builder.toString());
    }
}
