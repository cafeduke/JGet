/* $Header: otd_test/src/com.oracle.otdqacommon/util/TestUtil.java /main/11 2014/09/16 22:07:56 rbseshad Exp $ */

/* Copyright (c) 2011, 2014, Oracle and/or its affiliates. 
All rights reserved.*/

/*
   DESCRIPTION
    <short description of component this file declares/defines>

   PRIVATE CLASSES
    <list of private classes defined - with one-line descriptions>

   NOTES
    <other useful comments, qualifications, etc.>

    MODIFIED   (MM/DD/YY)
    rbseshad    
    rbseshad    08/14/12 - Add assertContainsAll API.
    rbseshad    12/19/11 - Add assertLogMessages API.
    rbseshad    09/08/11 - Add APIs to assert deviation from 2D array.
    rbseshad    07/17/11 - Add assertPercentDeviation for a single value. 
    rbseshad    05/13/11 - Disable logging error for getOSRequestCount.
    rbseshad    05/12/11 - Add getPercentDeviation API.
    mseelam     05/02/11 - Modified the function names to getOSRequestCount, getOSRequestCount. 
    rbseshad    04/26/11 - Add @Author annotation.
    rbseshad    03/27/11 - Add more doc.
    rbseshad    02/22/11 - Creation.
 */

package com.oracle.ohsqa.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import com.github.cafeduke.jreportng.LoggerFactory;

public class TestUtil
{
    public static final File DIR_JREPORT_TEST_RESOURCE = new File("src/main/resources");

    private TestUtil()
    {

    }

    /**
     * Wrapper for System.out.println
     * @param str String to print on {@link System#out}
     */
    public static void println(String str)
    {
        System.out.println(str);
    }

    /**
     * Constructs a file whose path is similar to that of the {@code testClass}'s
     * package structure, but, has the root folder as {@link TestProperties#DirBenchmark}.
     * 
     * <p>
     * Example: If {@code testClass} is {@code test.oracle.otd.loadbalance.RoundRobin} the benchmark
     * file should reside at {@link TestProperties#DirBenchmark}{@code /test/oracle/otd/loadbalance/<fileName>}.
     * 
     * @param testClass The test class that needs the benchmark.
     * @param fileName name of the benchmark file.
     * @return A file object to path {@link TestProperties#DirBenchmark}{@code /test/oracle/otd/loadbalance/<fileName>}
     */
    public static File getBenchmark(Class<? extends TestCase> testClass, String fileName)
    {
        File file = new File(TestProperties.DirBenchmark.getAbsolutePath());
        for (String currToken : testClass.getPackage().getName().split("\\."))
            file = new File(file, currToken);
        return new File(file, fileName);
    }

    /**
     * Assert if response code is as expected.
     */
    public static void assertResponseCode(int actualRespCode, int expectedRespCode)
    {
        Assert.assertEquals(actualRespCode, expectedRespCode);
    }

    /**
     * Invoke assertResponseOK (new int [] {respCode}, 1);
     */
    public static void assertResponseOK(int respCode)
    {
        assertResponseOK(new int[] { respCode }, 1);
    }

    /**
     * Assert if the response is non 200
     * 
     * @param respCode Actual response code.
     */
    public static void assertResponseNotOK(int respCode)
    {
        Assert.assertNotEquals(respCode, HttpURLConnection.HTTP_OK);
    }

    /**
     * Invoke assertResponse(respCode, expectedRespCount, HttpURLConnection.HTTP_OK);
     * <br>
     * See {@link #assertResponse(int[], int, int)}
     */
    public static void assertResponseOK(int respCode[], int expectedRespCount)
    {
        assertResponse(respCode, expectedRespCount, HttpURLConnection.HTTP_OK);
    }

    /**
     * Assert response code.
     * <br>
     * The following assertions are made
     * <ul>
     * <li>The size of {@code respCode} MUST be equal to {@code expectedRespCount}
     * <li>All response codes {@code respCode} MUST be {@code expectedRespCode}
     * </ul>
     * 
     * @param respCode Array of response code
     * @param expectedRespCount Expected size of response code array
     * @param expectedRespCode Expected resposne code.
     */
    public static void assertResponse(int respCode[], int expectedRespCount, int expectedRespCode)
    {
        Assert.assertEquals(respCode.length, expectedRespCount);
        for (int i = 0; i < respCode.length; ++i)
            Assert.assertEquals(respCode[i], expectedRespCode);
    }

    /**
     * Assert that regular expression <b>searchStr</b> is found among
     * the <b>log</b> entries.
     * 
     * @param log Array of log entries.
     * @param searchStr Regular expression to be searched among logs.
     * @param isRegExp If true, <b>toSearch</b> is a regular expression.
     */
    public static void assertMatch(File file, String searchStr, boolean isRegExp)
    {
        assertMatch(file, new String[] { searchStr }, isRegExp);
    }

    /**
     * Assert that there is a log entry for all regular expressions specified
     * in <b>searchStr</b> array.
     * 
     * @param log Array of log entries.
     * @param arrSearch Strings to be found
     * @param isRegExp If true, <b>toSearch</b> is a regular expression.
     */
    public static void assertMatch(File file, String arrSearch[], boolean isRegExp)
    {
        try
        {
            String log[] = FileUtils.readLines(file, Charset.defaultCharset()).toArray(new String[0]);
            boolean mesgFound[] = new boolean[arrSearch.length];
            int countOfMatches = 0;

            for (String currLog : log)
            {
                if (countOfMatches == mesgFound.length)
                    break;

                for (int searchIndex = 0; searchIndex < arrSearch.length; ++searchIndex)
                {
                    if (mesgFound[searchIndex])
                        continue;

                    String currSearch = arrSearch[searchIndex];
                    boolean found = (isRegExp) ? currLog.matches(currSearch) : currLog.contains(currSearch);
                    if (found)
                    {
                        countOfMatches++;
                        mesgFound[searchIndex] = true;
                    }
                }
            }

            for (int i = 0; i < mesgFound.length; ++i)
                Assert.assertTrue(mesgFound[i], "Message '" + arrSearch[i] + "' not found in server logs.");
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Read from file failed", e);
        }
    }

    /**
     * Assert that <b>source</b> string contains ANY of the substrings <b>substr</b>
     * 
     * @param source Source string.
     * @param substr Sub Strings.
     */
    public static void assertContainsAny(String source, String... substr)
    {
        boolean found = false;

        for (String currSubstr : substr)
        {
            if (source.contains(currSubstr))
            {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found, "None of SubStrings=" + Arrays.toString(substr) + " were found in Source=" + source);
    }

    /**
     * Assert that the <b>source</b> string contains ALL the substrings <b>substr</b>.
     * 
     * @param source Source string.
     * @param substr Sub Strings.
     */
    public static void assertContainsAll(String source, String... substr)
    {
        for (String currSubstr : substr)
            Assert.assertTrue(source.contains(currSubstr), "Substring not found. Source=" + source + " SubStr=" + currSubstr);
    }

    public static void sleepInMilli(int milli)
    {
        try
        {
            Thread.sleep(milli);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void sleep(int sec)
    {
        sleepInMilli(sec * 1000);
    }

    public static void sleepAndLog(Logger logger, String mesg, int sleepInMilli)
    {
        sleepInMilli(sleepInMilli);
        System.out.println(mesg);
        logger.info(mesg);
    }

    /**
     * Invoke exitFatal (message, e, testClass);
     */
    public static void exitFatal(String message, Exception e, Class<?> testClass)
    {
        exitFatal(message, e, testClass, null);
    }

    /**
     * Exit all tests with fatal {@code message}. Create a .dif file with error {@code message} and stack trace.
     * Append {@code difPrefix} to the name of the .dif file. Exception {@code e} should have the stack trace.
     * 
     * @param message Fatal Error message
     * @param difPrefix Dif prefix
     * @param e Fatal exception.
     */
    public static void exitFatal(String message, Exception e, Class<?> testClass, String prefix)
    {
        prefix = (prefix == null) ? "" : ("." + prefix);
        String fileName = testClass.getName() + prefix + ".fatal.dif";
        Logger logger = LoggerFactory.getJReportLogger(testClass);
        try
        {
            message = "[FATAL] " + message;
            PrintWriter out = new PrintWriter(new FileWriter(fileName));
            e.printStackTrace(out);
            out.close();
            logger.log(Level.SEVERE, message, e);
        }
        catch (IOException ie)
        {
            logger.log(Level.SEVERE, "Error writing fatal dif '" + fileName + "'");
        }
        finally
        {
            Util.die(message, e);
        }
    }
}
