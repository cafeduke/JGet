package com.oracle.ohsqa.h2;

import static com.oracle.ohsqa.common.TestUtil.*;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.github.cafeduke.jget.ArgProcessor.MultiThreadMode;
import com.github.cafeduke.jget.JGet;
import com.github.cafeduke.jget.JGet.ArgBuilder;
import com.oracle.ohsqa.common.TestCase;

public class Http2Sanity extends TestCase
{
    @Test
    public void ping()
    {
        String outHeader = TestPrefix + ".ping.head.out";
        ArgBuilder builder = JGet.newBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .showAllHeaders()
            .quiet();
        int respCode = jget.sendRequest(builder.build())[0];
        println("[" + TestPrefix + "] ResponseCode=" + respCode);
        assertResponseOK(respCode);

        Integer x = Integer.valueOf(0);
        String str = "5";
        Integer y = Integer.parseInt(str);
        Integer sum = Integer.sum(x, y);
    }

    @Test(dependsOnMethods = "ping")
    public void pings()
    {
        String outHeader = TestPrefix + ".pings.head";
        ArgBuilder builder = JGet.newBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .mode(MultiThreadMode.MSC)
            .threadCount(5)
            .showAllHeaders()
            .quiet();
        int respCode[] = jget.sendRequest(builder.build());
        println("[" + TestPrefix + "] ResponseCode=" + Arrays.toString(respCode));
        assertResponseOK(respCode, 5);
    }
}
