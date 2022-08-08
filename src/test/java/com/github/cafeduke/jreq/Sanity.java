package com.github.cafeduke.jreq;

import static com.github.cafeduke.jreq.common.TestUtil.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
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

    @Test
    public void doPost() throws JSONException, IOException
    {
        String output = TestPrefix + ".doPost.json";
        JReq.ArgBuilder builder = JReq.newBuilder()
            .url("https://httpbin.org/post")
            .outputToFile(output)
            .doPost()
            .postBody("item=Apple&count=12");
        int respCode = jreq.sendRequest(builder.build())[0];
        assertResponseOK(respCode);

        JSONObject jsonRoot = new JSONObject(FileUtils.readFileToString(new File(output), Charset.defaultCharset()));
        JSONObject jsonForm = (JSONObject) jsonRoot.getJSONObject("form");
        Assert.assertEquals(jsonForm.getInt("count"), 12);
        Assert.assertEquals(jsonForm.getString("item"), "Apple");
    }
}
