package com.github.cafeduke.jreq.common;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;

public class TestUtil
{

    public static void main(String arg[])
    {
        System.out.println(TestUtil.class.getSimpleName());
    }

    public static void println(String str)
    {
        System.out.println(str);
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
}
