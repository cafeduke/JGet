/* $Header: otd_test/src/com.oracle.otdqacommon/util/Util.java /main/8 2014/04/06 11:14:54 rbseshad Exp $ */

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
 mseelam     09/10/12 - Added a new methos writeFile
 rbseshad    08/08/12 - Add API to convert string tokens to int.
 rbseshad    08/08/12 - Add API to calculate summation
 rbseshad    09/08/11 - Add API to slice 2D array
 mseelam     07/22/11 - Adding DateTime to print. 
 rbseshad    07/14/11 - Add method slice
 mseelam     05/30/11 - Add method print 
 rbseshad    05/03/11 - Rename threadSleep to sleepInMilli
 mseelam     04/29/11 - Adding sleep function
 rbseshad    04/26/11 - Add @Author annotation
 rbseshad    02/22/11 - Creation
 */

package com.oracle.ohsqa.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArchUtils;

/**
 * This class provides generic utility functions.
 * 
 * @author Raghunandan.Seshadri
 * @owner Raghunandan.Seshadri
 */
public class Util
{

    private Util()
    {

    }

    /**
     * Line separator that matches both Unix and Windows styles.
     */
    public static final String lineSepRegexWinUnix = "(\r)?\n";

    /**
     * Line separator
     */
    public static final String lineSep = System.getProperty("line.separator");

    /**
     * File separator
     */
    public static final String fileSep = File.separator;

    /**
     * Path separator
     */
    public static final String parthSep = File.pathSeparator;

    public static void main(String arg[])
    {
        System.out.println(ArchUtils.getProcessor().getArch().getLabel());
        System.out.println(ArchUtils.getProcessor().getType());

        System.out.print("Total CPU:");
        System.out.println(Runtime.getRuntime().availableProcessors());
        System.out.println("os.name=" + System.getProperty("os.arch"));
        System.out.println("os.version=" + System.getProperty("os.version"));
    }

    /**
     * Concatenate the Strings in the {@code list} using {@code delim}
     * 
     * @param list Strings to be concatenated.
     * @param delim Delimiter
     * @return Resultant concatenated string.
     */
    public static String join(List<String> list, String delim)
    {
        return Util.join(list.toArray(new String[0]), delim);
    }

    /**
     * Concatenate the {@code num} using delimiter {@code delim}.
     * 
     * @param num Array of numbers.
     * @param delim Delimiter
     * @return Resultant string after concatenation.
     */
    public static String join(int num[], String delim)
    {
        StringBuilder builder = new StringBuilder();
        int lastIndex = num.length - 1;
        for (int index = 0; index < lastIndex; ++index)
            builder.append(num[index]).append(delim);
        builder.append(num[lastIndex]);
        return builder.toString();
    }

    /**
     * Concatenate the Strings in the array {@code token} using {@code delim}
     * 
     * @param token Strings to be concatenated.
     * @param delim Delimiter
     * @return Resultant concatenated string.
     */
    public static String join(String token[], String delim)
    {
        if (token == null || token.length == 0)
            return "";

        StringBuilder builder = new StringBuilder();
        int lastIndex = token.length - 1;
        for (int index = 0; index < lastIndex; ++index)
            builder.append(token[index]).append(delim);
        builder.append(token[lastIndex]);
        return builder.toString();
    }

    /**
     * Cut the string into two halves based on the first occurrence of the {@code delim}.
     * Trim both the halves and return them in an array.
     * 
     * @param str String to be cut
     * @param delim Delimiter that determines where to cut.
     * @return Two halves cut near {@code delim} and trimmed.
     */
    public static String[] cut(String str, char delim)
    {
        int index = str.indexOf(delim);
        if (index == -1)
            return null;

        String firstHalf = str.substring(0, index).trim();
        String secondHalf = str.substring(index + 1).trim();

        return new String[] { firstHalf, secondHalf };
    }

    /**
     * Read lines from a file and return them.
     * 
     * @param file File to read from.
     * @return A list of lines read.
     * @throws IOException
     */
    public static List<String> getLines(File file) throws IOException
    {
        List<String> listLine = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String currLine = null;
        while ((currLine = in.readLine()) != null)
            listLine.add(currLine);
        in.close();
        return listLine;
    }

    /**
     * Sleeps for number of seconds specified.
     * 
     * @param sleepTime number of seconds to sleep.
     */
    public static void sleep(int sleepTime)
    {
        try
        {
            Thread.sleep(1000 * sleepTime);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Current thread sleeps for {@code milli} seconds.
     * 
     * @param sleepTime Time in milliseconds.
     */
    public static void sleepInMilli(long sleepTime)
    {
        try
        {
            Thread.sleep(sleepTime);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param filename Filename to load properties from
     * @return Load properties from a file and return them.
     * @throws IOException
     */
    public static Properties loadProperties(String filename) throws IOException
    {
        return loadProperties(new File(filename));
    }

    /**
     * @param file File to load properties from
     * @return Load properties from a file and return them.
     * @throws IOException
     */
    public static Properties loadProperties(File file) throws IOException
    {
        Properties prop = new Properties();
        FileReader in = new FileReader(file);
        prop.load(in);
        in.close();
        return prop;
    }

    /**
     * Recursively delete contents of file/directory - {@code file}. Inclusive of {@code file}.
     * 
     * @param file The file/directory whose contents needs to be deleted.
     * @return true, if deletion is successful.
     * @throws IOException
     */
    public static void recurDelete(File file) throws IOException
    {
        FileUtils.deleteDirectory(file);
    }

    /**
     * {@code arg[index]} is expected to have the option switch for which value
     * needs to be retrieved. If {@code (index+1)} is NOT within the argument boundary
     * an exception is thrown, else the switch value is returned.
     * 
     * @param arg Array of arguments.
     * @param index Index of the switch
     * @return Value of the switch.
     */
    public static String getSwitchValue(String arg[], int index)
    {
        if (index + 1 >= arg.length)
            throw new IllegalArgumentException("Option " + arg[index] + " needs a value");
        return arg[index + 1];
    }

    /**
     * Convert a 2 dimensional array to String;
     * 
     * @param arr
     * @return String representation of 2 dimentional array.
     */
    public static String toString(int arr[][])
    {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < arr.length; ++i)
            buffer.append(Arrays.toString(arr[i])).append(Util.lineSep);
        return buffer.toString();
    }

    /**
     * Convert an array of Strings with integral values to an integer array.
     * 
     * @param token
     * @return
     */
    public static int[] toIntArray(String token[])
    {
        int value[] = new int[token.length];
        for (int i = 0; i < value.length; ++i)
            value[i] = Integer.valueOf(token[i]);
        return value;
    }

    /**
     * Convert an array of Strings with integral values to an integer array.
     * 
     * @param token
     * @return
     */
    public static boolean[] toBooleanArray(String token[])
    {
        boolean value[] = new boolean[token.length];
        for (int i = 0; i < value.length; ++i)
        {
            if (token[i].equals("1"))
                value[i] = true;
            else if (token[i].equals("0"))
                value[i] = false;
            else
                throw new IllegalArgumentException(token[i] + " is not a valud boolean eqivalent. Should be (1|0)");
        }
        return value;
    }

    /**
     * @param value Array of integers
     * @return Summation of integers in <b>value</b>
     */
    public static int summation(int value[])
    {
        int sum = 0;
        for (int currValue : value)
            sum += currValue;
        return sum;
    }

    /**
     * @return Current date and time in access log format.
     */
    public static String getDateTimeString()
    {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        return ft.format(dNow);
    }

    /**
     * Cut the {@code original} array by including elements from {@code beginIndex} to {@code endIndex}, both inclusive.
     * 
     * @param original Original array
     * @param beginIndex Index to start slicing. (inclusive)
     * @param endIndex Index to end slicing. (inclusive)
     * @return The sliced array.
     */
    public static int[] slice(int original[], int beginIndex, int endIndex)
    {
        return Arrays.copyOfRange(original, beginIndex, endIndex + 1);
    }

    /**
     * Cut the {@code original} array by including elements from {@code beginIndex} to {@code endIndex}, both inclusive.
     * 
     * @param original Original array
     * @param beginIndex Index to start slicing. (inclusive)
     * @param endIndex Index to end slicing. (inclusive)
     * @return The sliced array.
     */
    public static int[][] slice(int original[][], int beginIndex, int endIndex)
    {
        int arr[][] = new int[original.length][];

        for (int i = 0; i < arr.length; ++i)
            arr[i] = slice(original[i], beginIndex, endIndex);

        return arr;
    }

    /**
     * Writes strings in <b>token</b> separated by new line in <b>file</b>.
     * 
     * @param content Array of strings
     * @param file File to write the strings into.
     */
    public static void writeToFile(File file, String content[]) throws IOException
    {
        PrintWriter out = new PrintWriter(new FileWriter(file));
        for (String currToken : content)
            out.println(currToken);
        out.close();
    }

    public static void writeToFile(File file, String content) throws IOException
    {
        writeToFile(file, new String[] { content });
    }

    public static void writeToFile(String filePath, String content) throws IOException
    {
        writeToFile(new File(filePath), new String[] { content });
    }

    public static void writeToFile(String filePath, String content[]) throws IOException
    {
        writeToFile(new File(filePath), content);
    }

    /**
     * If {@code logger} is specified, log message into the logger else log message using STDOUT.
     * The actual class and method which is calling this log method is inferred ( using a generated
     * Throwable or existing exception) and is used for logging.
     * 
     * This is just a convenient method for logging exception and the caller is expected to take
     * appropriate action on the exception. Hence, this function throws back the exception.
     * 
     * @param mesg Message to be logged.
     * @param e Exception to be logged.
     * @param logger Logger to log the exception into.
     * @throws Exception Throws the exception logged.
     */
    public static void logError(String mesg, Exception e, Logger logger) throws Exception
    {
        if (mesg == null)
            mesg = "Error occured during test run";

        if (e == null)
            e = new Exception(mesg);

        System.out.println(mesg);
        if (logger == null)
        {
            e.printStackTrace();
        }
        else
        {
            StackTraceElement stack[] = e.getStackTrace();
            String sourceClass = "Unknown class";
            String sourceMethod = "Unknown method";
            if (stack.length > 1)
            {
                sourceClass = stack[1].getClassName();
                sourceMethod = stack[1].getMethodName();
            }
            logger.logp(Level.SEVERE, sourceClass, sourceMethod, mesg, e);
            System.out.println("Refer logs for details");
        }

        throw e;
    }

    /**
     * Calls logError (mesg, null, logger);
     */
    public static void logError(String mesg, Logger logger) throws Exception
    {
        logError(mesg, null, logger);
    }

    /**
     * Calls logError ("Error: ", e, logger);
     */
    public static void logError(Exception e, Logger logger) throws Exception
    {
        logError("Error: ", e, logger);
    }

    /**
     * Calls logError (mesg, e, null);
     */
    public static void logError(String mesg, Exception e) throws Exception
    {
        logError(mesg, e, null);
    }

    /**
     * Calls logError (mesg, null, null);
     */
    public static void logError(String mesg) throws Exception
    {
        logError(mesg, null, null);
    }

    /**
     * Calls logError ("Error: ", e, null);
     */
    public static void logError(Exception e) throws Exception
    {
        logError("Error: ", e, null);
    }

    /**
     * To delete a file
     * 
     * @param filePath String path of file to be deleted
     */
    public static boolean deleteFile(String filePath)
    {
        File file = new File(filePath);
        if (file.exists())
        {
            return file.delete();
        }

        return false;
    }

    /**
     * Log error message {@code mesg} and exit.
     *
     * @param mesg Error message
     */
    public static void die(String mesg)
    {
        die(mesg, null);
    }

    /**
     * Log error message {@code mesg}, exception {@code e} and exit.
     * 
     * @param mesg Error message
     * @param e Exception
     */
    public static void die(String mesg, Exception e)
    {
        if (mesg == null)
            mesg = "Error occured during test run";

        if (e == null)
            e = new Exception(mesg);

        System.out.println(mesg);
        e.printStackTrace();
        System.exit(1);
    }

    public static String getNormalizePath(String first, String... more)
    {
        if (first == null)
            return null;
        return Paths.get(first, more).normalize().toFile().getPath().replace("\\", "/");
    }
}
