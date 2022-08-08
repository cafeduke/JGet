package com.github.cafeduke.jreq.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class has utilities required by the test tools 
 * 
 * @author CafeDuke 
 */
public class ToolsUtil
{
    /**
     * Validate that the value (at <b>index+1</b>) for switch (at <b>index</b>) is a valid URL.
     * 
     * @param arg Array of arguments.
     * @param index Index of the switch.
     * @return An URL object
     * @throws MalformedURLException If URL is malformed
     */
    public static URL validateArgUrl(String arg[], int index) throws MalformedURLException
    {
        return new URL(Util.getSwitchValue(arg, index));
    }

    /**
     * Validate that the value (at <b>index+1</b>) for switch (at <b>index</b>) is a valid Integer.
     * 
     * @param arg Array of arguments.
     * @param index Index of the switch.
     * @return int obtained by parsing String at <b>index+1</b>
     */
    public static int validateArgInteger(String arg[], int index)
    {
        return Integer.parseInt(Util.getSwitchValue(arg, index));
    }

    /**
     * Validate that the value (at <b>index+1</b>) for switch (at <b>index</b>) is a valid Long.
     * 
     * @param arg Array of arguments.
     * @param index Index of the switch.
     * @return long obtained by parsing String at <b>index+1</b>
     */
    public static long validateArgLong(String arg[], int index)
    {
        return Long.parseLong(Util.getSwitchValue(arg, index));
    }

    /**
     * Validate that the value (at <b>index+1</b>) for switch (at <b>index</b>) is comma separated list of values.
     * 
     * @param arg Array of arguments.
     * @param index Index of the switch.
     * @return List of String.
     */
    public static List<String> validateArgStrings(String arg[], int index)
    {
        return Arrays.asList(Util.getSwitchValue(arg, index).split(","));
    }

    /**
     * Validate that the value (at <b>index+1</b>) for switch (at <b>index</b>) is a valid existing file.
     * 
     * @param arg Array of arguments.
     * @param index Index of the switch.
     * @return File object.
     * @throws FileNotFoundException Exception thrown if the file given by arg is not found
     */
    public static File validateArgFile(String arg[], int index) throws FileNotFoundException
    {
        File file = new File(Util.getSwitchValue(arg, index));
        if (!file.exists())
            throw new FileNotFoundException("File  " + file.getAbsolutePath() + " not found");
        return file;
    }

    /**
     * Validate that the value (at <b>index+1</b>) for switch (at <b>index</b>) is comma separated list of valid existing files.
     * 
     * @param arg Array of arguments.
     * @param index Index of the switch.
     * @return List of File objects.
     * @throws FileNotFoundException File provided in argument is not found.
     */
    public static List<File> validateArgFiles(String arg[], int index) throws FileNotFoundException
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
     * Create a default logger to be used by tools independent of the test framework.
     * 
     * @param loggerName Logger name - Typically path to class file.
     * @param logFilename File name to log entries.
     * @return Logger object.
     */
    public static Logger getLogger(String loggerName, String logFilename)
    {
        Logger logger = Logger.getLogger(loggerName);

        if (logger.getHandlers() == null || logger.getHandlers().length == 0)
        {
            FileHandler fileHandler = null;
            try
            {
                fileHandler = new FileHandler(logFilename);
            }
            catch (IOException ioe)
            {
                Util.die("Error creating fileHandler to file " + logFilename, ioe);
            }
            TextFormatter textFormatter = new TextFormatter();
            fileHandler.setFormatter(textFormatter);
            logger.setUseParentHandlers(false);
            logger.addHandler(fileHandler);
        }

        if (Boolean.getBoolean("debug"))
            logger.setLevel(Level.FINEST);
        else
            logger.setLevel(Level.INFO);

        return logger;
    }
}
