package com.oracle.ohsqa.common;

import java.util.logging.Logger;

public class HttpServer
{

    public static final String DEFAULT_INSTANCE_NAME = "ohs1";

    /**
     * Reference to all ohs instances.
     */
    private static final HttpServer httpServer[] = new HttpServer[TestProperties.OHSInstanceCount];

    /*
     * ---------------------------------------------------------------
     * Static Block Initialization
     * ---------------------------------------------------------------
     */

    static
    {
        init();
    }

    /**
     * Create all ohs instances
     * 
     * @throws IOException
     * @throws MalformedObjectNameException
     */
    public static void init()
    {
        for (int i = 0; i < httpServer.length; ++i)
            httpServer[i] = new HttpServer("OHS_INSTANCE" + (i + 1));

        for (int i = 0; i < httpServer.length; ++i)
            httpServer[i].configManager.init();
    }

    /*
     * ---------------------------------------------------------------
     * Instance Variables
     * ---------------------------------------------------------------
     */

    private String name;

    private Logger logger = null;

    private String host, httpPort, httpsPort, productRoot;

    private Class<? extends TestCase> testClass = null;

    private ConfigManager configManager = null;

    private HttpServer(String name)
    {
        this.name = name;
        host = TestProperties.getProperty(name + "_HOST");
        httpPort = TestProperties.getProperty(name + "_HTTP_PORT");
        httpsPort = TestProperties.getProperty(name + "_HTTPS_PORT");
        productRoot = TestProperties.getProperty(name + "_HOME");

        configManager = new ConfigManager(this);
    }

    /*
     * ---------------------------------------------------------------
     * Setters
     * ---------------------------------------------------------------
     */

    /**
     * Set logger for current ohs.
     *
     * @param logger Logger object.
     */
    public void setTestClass(Class<? extends TestCase> testClass)
    {
        this.testClass = testClass;
    }

    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /*
     * ---------------------------------------------------------------
     * Getters
     * ---------------------------------------------------------------
     */

    /**
     * @param id Instance ID
     * @return Traffic director instance with <b>id</b>
     */
    public static HttpServer getInstance(int id)
    {
        int index = id - 1;
        if (index >= httpServer.length)
            throw new IllegalArgumentException("Index=" + index + " is out of range. MaxOTDInstances =" + httpServer.length);
        return httpServer[index];
    }

    /**
     * @return Array of all ohs instances.
     */
    public static HttpServer[] getInstances()
    {
        return httpServer;
    }

    /**
     * @return If set return logger else return the global logger.
     */
    public Logger getLogger()
    {
        return logger;
    }

    /**
     * @return Current test class.
     */
    public Class<?> getTestClass()
    {
        return (testClass == null) ? TestListener.class : testClass;
    }

    /**
     * @return Name of the ohs (Eg: OTD1)
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return Traffic director's host name.
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @return Traffic director's HTTP listen port.
     */
    public String getHttpPort()
    {
        return httpPort;
    }

    /**
     * @return Traffic director's listen port depending on the current mode.
     */
    public String getHttpsPort()
    {
        return httpsPort;
    }

    /**
     * @return OHS config home
     */
    public String getConfigHome()
    {
        return Util.getNormalizePath(productRoot, "config/fmwconfig/components/OHS");
    }

    /**
     * @return OHS instance home
     */
    public String getInstanceHome()
    {
        return getInstanceHome(DEFAULT_INSTANCE_NAME);
    }

    /**
     * @return OHS instance home
     */
    public String getInstanceHome(String instanceName)
    {
        return Util.getNormalizePath(getConfigHome(), "instances", instanceName);
    }

}
