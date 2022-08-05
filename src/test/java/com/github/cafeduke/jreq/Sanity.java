package com.github.cafeduke.jreq;

import static com.github.cafeduke.jreq.common.TestUtil.*;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.github.cafeduke.jreq.ArgProcessor.MultiThreadMode;
import com.github.cafeduke.jreq.common.TestCase;

public class Sanity extends TestCase
{
    @Test
    public void ping()
    {
        String outHeader = TestPrefix + ".ping.head.out";
        JReq.ArgBuilder builder = JReq.newBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .showAllHeaders()
            .quiet();
        int respCode = jreq.sendRequest(builder.build())[0];
        println("[" + TestPrefix + "] ResponseCode=" + respCode);
        assertResponseOK(respCode);
    }

    @Test(dependsOnMethods = "ping")
    public void pings()
    {
        String outHeader = TestPrefix + ".pings.head";
        List<String> listArg = JReq.newBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .mode(MultiThreadMode.MSC)
            .threadCount(5)
            .showAllHeaders()
            .quiet()
            .build();
        int respCode[] = jreq.sendRequest(listArg);
        println("[" + TestPrefix + "] ResponseCode=" + Arrays.toString(respCode));
        assertResponseOK(respCode, 5);
    }
}
