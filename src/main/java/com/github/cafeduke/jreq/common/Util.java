package com.github.cafeduke.jreq.common;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class provides generic utility functions.
 * 
 * @author Raghunandan.Seshadri
 */
public class Util
{
    private Util()
    {

    }

    /**
     * Line separator
     */
    public static final String LineSep = System.getProperty("line.separator");

    /**
     * File separator
     */
    public static final String FileSep = File.separator;

    /**
     * Path separator
     */
    public static final String PathSep = File.pathSeparator;

    /**
     * Concatenate the Strings in the {@code list} using {@code delim}
     * 
     * @param list Strings to be concatenated.
     * @param delim Delimiter
     * @return Resultant concatenated string.
     */
    public static String join(List<String> list, char delim)
    {
        return StringUtils.join(list, delim);
    }

    /**
     * Concatenate the {@code num} using delimiter {@code delim}.
     * 
     * @param num Array of numbers.
     * @param delim Delimiter
     * @return Resultant string after concatenation.
     */
    public static String join(int num[], char delim)
    {
        return StringUtils.join(num, delim);
    }

    /**
     * Concatenate the Strings in the array {@code token} using {@code delim}
     * 
     * @param token Strings to be concatenated.
     * @param delim Delimiter
     * @return Resultant concatenated string.
     */
    public static String join(String token[], char delim)
    {
        if (token == null || token.length == 0)
            return "";
        return StringUtils.join(token, delim);
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
            throw new IllegalStateException("Interuppted during sleep");
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
            throw new IllegalStateException("Interuppted during sleep");
        }
    }

    /**
     * @param filename Filename to load properties from
     * @return Load properties from a file and return them.
     * @throws IOException Exception reading file
     */
    public static Properties loadProperties(String filename) throws IOException
    {
        return loadProperties(new File(filename));
    }

    /**
     * @param file File to load properties from
     * @return Load properties from a file and return them.
     * @throws IOException Exception reading file
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
     * @throws IOException Exception reading file
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
     * @param arr Two dimensional array
     * @return String representation of 2 dimensional array.
     */
    public static String toString(int arr[][])
    {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < arr.length; ++i)
            buffer.append(Arrays.toString(arr[i])).append(Util.LineSep);
        return buffer.toString();
    }

    /**
     * Convert an array of Strings with integral values to an integer array.
     * 
     * @param token Strings with integral values
     * @return Array of integers
     */
    public static int[] toIntArray(String token[])
    {
        return Stream.of(token).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * Convert an array of Strings with integral values (1 implies true, 0 implies false) to a boolean array.
     * 
     * @param token Strings with integral values
     * @return Array of boolean
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
        return IntStream.of(value).sum();
    }

    /**
     * @param date Date to be formatted
     * @return Format <b>date</b> as per access-log format and return the resultant date.
     */
    public static String getFormattedDate(Date date)
    {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        return ft.format(date);
    }

    /**
     * @return Current date as per access-log format.
     */
    public static String getFormattedDate()
    {
        return getFormattedDate(new Date());
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
    public static void die(String mesg, Throwable e)
    {
        if (mesg == null)
            mesg = "Error occured during test run";

        if (e == null)
            e = new Exception(mesg);

        System.out.println(mesg);
        e.printStackTrace();
        System.exit(1);
    }

    /**
     * Return free ports in range
     * 
     * @param rangeMin Range minimum value
     * @param rangeMax Range maximum value
     * @param count Number of free ports needed
     * @return Array of free ports
     */
    public static int[] getFreePorts(int rangeMin, int rangeMax, int count)
    {
        int currPortCount = 0;
        int port[] = new int[count];

        for (int currPort = rangeMin; currPortCount < count && currPort <= rangeMax; ++currPort)
            if (isPortFree(currPort))
                port[currPortCount++] = currPort;

        if (currPortCount < count)
            throw new IllegalStateException("Could not find " + count + " free ports to allocate within range " +
                    rangeMin + "-" + rangeMax + ".");
        return port;
    }

    /**
     * Check if the current <b>port</b> is free ( Not bound by any process ).
     *
     * @param port The port that needs to be checked if free.
     * @return true if the port is free, false otherwise.
     */
    public static boolean isPortFree(int port)
    {
        ServerSocket socket = null;
        try
        {
            socket = new ServerSocket(port);
            socket.close();
        }
        catch (IOException e)
        {
            return false;
        }
        return true;
    }

    /**
     * Get the IP address for a given host
     * 
     * @param hostName The name of the host
     * @return IP address of the host
     */
    public static String getIPAddress(String hostName)
    {
        String ip = null;
        try
        {
            InetAddress thisIp = InetAddress.getByName(hostName);
            ip = thisIp.getHostAddress();
            System.out.println("IP:" + ip);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * Get the hostname for the given IP address
     * 
     * @param ipAddress IP address of the host
     * @return name of the host
     */
    public static String getNetworkInterfaceName(String ipAddress)
    {
        try
        {
            InetAddress localHost = InetAddress.getByName(ipAddress);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            for (InterfaceAddress address : networkInterface.getInterfaceAddresses())
                if (address.getAddress().getHostAddress().equalsIgnoreCase(ipAddress))
                    return networkInterface.getDisplayName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert the stack trace to a string
     * 
     * @param e Exception raised
     * @return String having stack trace information.
     */
    public static String getStacktrace(Exception e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * The normalized path
     * 
     * @param first Path as string to be normalized
     * @param more Optional directory names and/or relative paths
     * @return Normalized string
     */
    public static String getNormalizePath(String first, String... more)
    {
        if (first == null)
            return null;
        return Paths.get(first, more).normalize().toFile().getPath().replace("\\", "/");
    }
}
