package com.oracle.ohsqa.h2;

import static com.oracle.ohsqa.common.TestUtil.*;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.github.cafeduke.jreq.ArgProcessor.MultiThreadMode;
import com.github.cafeduke.jreq.JReq;
import com.github.cafeduke.jreq.JReq.ArgBuilder;
import com.oracle.ohsqa.common.TestCase;

public class Http2Sanity extends TestCase
{
    @Test
    public void ping()
    {
        String outHeader = TestPrefix + ".ping.head.out";
        ArgBuilder builder = JReq.newBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .showAllHeaders()
            .quiet();
        int respCode = jreq.sendRequest(builder.build())[0];
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
        ArgBuilder builder = JReq.newBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .mode(MultiThreadMode.MSC)
            .threadCount(5)
            .showAllHeaders()
            .quiet();
        int respCode[] = jreq.sendRequest(builder.build());
        println("[" + TestPrefix + "] ResponseCode=" + Arrays.toString(respCode));
        assertResponseOK(respCode, 5);
    }
}
