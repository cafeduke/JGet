package com.github.cafeduke;

import java.util.ArrayList;
import java.util.List;

import com.github.cafeduke.ArgProcessor.MultiThreadMode;
import com.github.cafeduke.common.Util;

/**
 * An argument builder for Fireduke
 * 
 * @author Raghunandan.Seshadri
 */
public class ArgBuilder
{
    private List<String> listArg = new ArrayList<>();

    /**
     * Create an instance of ArgBuilder
     */
    public ArgBuilder()
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
     * 
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
     * @return this ArgBuilder instance
     */
    public ArgBuilder doGet()
    {
        listArg.add("-get");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder doPost()
    {
        listArg.add("-post");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder doHead()
    {
        listArg.add("-head");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder doPut()
    {
        listArg.add("-put");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder doDelete()
    {
        listArg.add("-delete");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder doTrace()
    {
        listArg.add("-trace");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder doOptions()
    {
        listArg.add("-options");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder showHeaders()
    {
        listArg.add("-sh");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder showAllHeaders()
    {
        listArg.add("-sah");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder dontFollowRedirect()
    {
        listArg.add("-disableFollowRedirect");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder dontWaitForResponse()
    {
        listArg.add("-nonBlock");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder dontLogError()
    {
        listArg.add("-disableErrorLog");
        return this;
    }

    /**
     * @return this ArgBuilder instance
     */
    public ArgBuilder dontSendClientId()
    {
        listArg.add("-disableClientId");
        return this;
    }
}
