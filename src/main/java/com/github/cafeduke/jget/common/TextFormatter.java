package com.github.cafeduke.jget.common;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * This class is used to format log as plain text.
 * 
 * @author Raghunandan.Seshadri
 */
public class TextFormatter extends Formatter
{
    /**
     * Instance of Text formatter.
     */
    public TextFormatter()
    {
    }

    @Override
    public String format(LogRecord record)
    {
        StringBuilder builder = new StringBuilder();
        String column[] = getColumns(record);

        for (int i = 0; i < column.length - 1; ++i)
            builder.append("[" + column[i] + "] ");

        builder.append(Util.LineSep);
        builder.append(column[column.length - 1] + Util.LineSep);

        Throwable throwable = record.getThrown();
        if (throwable != null)
        {
            builder.append(throwable.toString() + Util.LineSep);
            StackTraceElement element[] = throwable.getStackTrace();
            for (StackTraceElement currElement : element)
                builder.append(currElement.toString() + Util.LineSep);
        }
        builder.append(Util.LineSep);

        return builder.toString();
    }

    /**
     * @param record LogRecord to be formatted.
     * @return Column values.
     */
    private String[] getColumns(LogRecord record)
    {
        /* Date */
        //SimpleDateFormat dateFormat = new SimpleDateFormat ("EEE, dd-MMM-yyyy HH:mm:ss.SSS z");
        String strDate = Util.getFormattedDate();

        /* Level */
        String strLogLevel = record.getLevel().toString();

        /* Class */
        String strClass = record.getSourceClassName();

        /* Method */
        String strMethod = record.getSourceMethodName();

        /* ThreadID */
        String strThreadID = "" + record.getThreadID();

        /* Message */
        String strMessage = record.getMessage();

        String field[] = { strDate, strLogLevel, strClass, strMethod, strThreadID, strMessage };
        return field;
    }

    @Override
    public String getHead(Handler h)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("##" + Util.LineSep);
        builder.append("# [Date][Verbosity][Class][Method][ThreadId]" + Util.LineSep);
        builder.append("# [Message]" + Util.LineSep);
        builder.append("##" + Util.LineSep);
        builder.append(Util.LineSep);
        return builder.toString();
    }

}
