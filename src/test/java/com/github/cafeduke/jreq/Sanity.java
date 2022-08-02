package com.github.cafeduke.jreq;

import static com.github.cafeduke.jreq.common.TestUtil.*;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.github.cafeduke.jreq.ArgBuilder;
import com.github.cafeduke.jreq.ArgProcessor.MultiThreadMode;
import com.github.cafeduke.jreq.common.TestCase;

public class Sanity extends TestCase
{
    @Test
    public void ping()
    {
        String outHeader = TestPrefix + ".ping.head.out";
        ArgBuilder builder = new ArgBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .showAllHeaders()
            .quiet();
        int respCode = fireduke.sendRequest(builder)[0];
        println("[" + TestPrefix + "] ResponseCode=" + respCode);
        assertResponseOK(respCode);
    }

    @Test(dependsOnMethods = "ping")
    public void pings()
    {
        String outHeader = TestPrefix + ".pings.head";
        ArgBuilder builder = new ArgBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .mode(MultiThreadMode.MSC)
            .threadCount(5)
            .showAllHeaders()
            .quiet();
        int respCode[] = fireduke.sendRequest(builder);
        println("[" + TestPrefix + "] ResponseCode=" + Arrays.toString(respCode));
        assertResponseOK(respCode, 5);
    }
}
