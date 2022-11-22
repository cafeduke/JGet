package com.oracle.ohsqa.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class TestProperties
{
    /**
     * File having all Test properties
     */
    private static final String VMArgPropertyFile = "PropertyFile";

    /**
     * Load properties file provided by system property {@code TestProperties.VMArgPropertyFile}
     */
    private static Properties testProperties = null;

    private TestProperties()
    {

    }

    /**
     * Load properties
     */
    static
    {
        testProperties = loadProperties();
    }

    /**
     * ---------------------------------------------------------------------------------------------------
     * Generic Properties
     * ---------------------------------------------------------------------------------------------------
     */

    /**
     * Organization prefix for all packages
     */
    public static final String PackagePrefix = "com.oracle.ohsqa";

    /**
     * Path to Java Home.
     */
    public static final String JavaHome = System.getProperty("java.home");

    /**
     * Path to Java to be used for executing any Java based process.
     */
    public static final String Java = JavaHome + "/bin/java";

    /**
     * ---------------------------------------------------------------------------------------------------
     * Test Directories
     * ---------------------------------------------------------------------------------------------------
     */

    /**
     * Test home directory.
     */
    public static final File DirTestHome = new File(getProperty("TestHome"));

    /**
     * Configuration directory - Directory that stores configuration files.
     */
    public static final File DirConfig = new File(DirTestHome, "config");

    /**
     * Data directory - Directory to store any resource the test system may need.
     * For example Java key store
     */
    public static final File DirData = new File(DirTestHome, "data");

    /**
     * Directory storing benchmark files against which test output is compared.
     */
    public static final File DirBenchmark = new File(DirTestHome, "benchmark");

    /**
     * Work directory - This directory will have the test execution results.
     * The property defaults to {@code <DirTestHome>/work}
     */
    public static final File DirWork = new File(DirTestHome, "work");

    /**
     * CSS directory - Directory that stores files for report customization.
     */
    public static final File DirCustomReport = new File(DirData, "CustomReport");

    /**
     * Report Directory - This directory has all files that constitute the total report which includes
     * images, style sheets, java script and report directories from all TestNG runs.
     */
    public static final File DirReport = new File(DirWork, "report");

    public static final boolean DebugMode = false;

    /**
     * ---------------------------------------------------------------------------------------------------
     * Arguments For Running Tests
     * ---------------------------------------------------------------------------------------------------
     */

    /**
     * TestArgument - Number of OHS Instances to be created.
     */
    public static final int OHSInstanceCount = Integer.parseInt(getDefaultProperty("OHSInstanceCount", "1"));

    /**
     * Test Argument - Stop all the test servers after test completion.
     */
    public static final boolean StopServersAfterTestRun = Boolean.parseBoolean(getDefaultProperty("StopServersAfterTestRun", "false"));

    /**
     * ---------------------------------------------------------------------------------------------------
     * Report Directories
     * ---------------------------------------------------------------------------------------------------
     */

    /**
     * Report Directory Name - The name of the report directory having the current test run HTML files.
     */
    public static final String DirReportName = "test-type";

    /**
     * Report Directory - Home report directory for the current TestNG run.
     */
    public static final File DirReportCurrentTestRun = new File(DirReport, DirReportName);

    /**
     * Report Category - This directory holds report files from various TestNG test runs.
     */
    public static final File DirReportCategory = new File(DirReportCurrentTestRun, "category");

    /**
     * Report Logs - This directory holds log files.
     */
    public static final File DirReportLog = new File(DirReportCurrentTestRun, "logs");

    /**
     * Sequential Report Directory - This directory will have the HTML report of the sequential test run.
     */
    public static final File DirSequentialReport = new File(DirReportCategory, "sequential");

    /**
     * Sequential Report Directory - This directory will have the HTML report of the sequential test run.
     */
    public static final File DirParallelReport = new File(DirReportCategory, "parallel");

    /**
     * Create a unmodifiable list of Strings from property value who's property
     * name is given by {@code propertyName}. If property value for {@code propertyName}
     * is null the list is empty.
     * 
     * @param propertyName Property name having comma separated value
     * @return Unmodifiable list of Strings.
     */
    public static List<String> getListFromCSV(String propertyName)
    {
        List<String> list = new ArrayList<String>();

        String propertyValue = getProperty(propertyName, false);
        if (propertyValue != null)
            list = Arrays.asList(propertyValue.split(","));

        return Collections.unmodifiableList(list);
    }

    /**
     * Invokes getProperty (propertyName, true);
     * <br>
     * See {@link #getProperty(String, boolean)}
     */
    public static String getProperty(String propertyName)
    {
        return getProperty(propertyName, true);
    }

    /**
     * Returns property value for {@code propertyName}. If required is true, and
     * if property value is null, an IllegalArgumentException is thrown. If
     * required is false, property value is returned asis.
     * 
     * @param propertyName Property name
     * @param required If true, the value is expected to be non null. An
     *            exception is thrown in case of null.
     * @return Property value for {@code propertyName}
     */
    public static String getProperty(String propertyName, boolean required)
    {
        String propertyValue = testProperties.getProperty(propertyName);

        if (required && (propertyValue == null || propertyValue.equals("")))
            throw new IllegalArgumentException("Null value for PropertyName=" + propertyName);

        return propertyValue;
    }

    /**
     * If property by name {@code propertyName} is not found,
     * {@code defaultValue} is returned.
     * 
     * @param propertyName Property name to look for.
     * @param defaultValue Default property value.
     * @return Property value for {@code propertyName}, if null, defaultValue.
     */
    public static String getDefaultProperty(String propertyName, String defaultValue)
    {
        String propertyValue = testProperties.getProperty(propertyName);
        if (propertyValue == null)
            propertyValue = defaultValue;
        return propertyValue;
    }

    /**
     * Load the test system properties file
     */
    private static synchronized Properties loadProperties()
    {
        String pathTestProperty = System.getProperty(TestProperties.VMArgPropertyFile);
        if (pathTestProperty == null)
            Util.die("JVM property needs to be set. Use VM argument -D" + VMArgPropertyFile + "=<Absolute path to Test.properties>", new IllegalStateException("Properties file not found."));

        File fileTestProperties = new File(pathTestProperty);
        Properties properties = null;
        try
        {
            properties = Util.loadProperties(fileTestProperties);
        }
        catch (IOException ioe)
        {
            Util.die("Cound not load properties file " + fileTestProperties.getAbsolutePath(), ioe);
        }
        return properties;
    }
}
