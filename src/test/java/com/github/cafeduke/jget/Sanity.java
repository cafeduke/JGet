package com.github.cafeduke.jget;

import static com.github.cafeduke.jget.common.TestUtil.assertResponseOK;
import static com.github.cafeduke.jget.common.TestUtil.println;

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

import com.github.cafeduke.jget.ArgProcessor.MultiThreadMode;
import com.github.cafeduke.jget.common.TestCase;

public class Sanity extends TestCase
{
    @Test
    public void ping()
    {
        String outHeader = TestPrefix + ".ping.head.out";
        JGet.ArgBuilder builder = JGet.newBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .showAllHeaders()
            .quiet();
        int respCode = jget.sendRequest(builder.build())[0];
        println("[" + TestPrefix + "] ResponseCode=" + respCode);
        assertResponseOK(respCode);
    }

    @Test(dependsOnMethods = "ping")
    public void pings()
    {
        String outHeader = TestPrefix + ".pings.head";
        List<String> listArg = JGet.newBuilder()
            .url("https://www.google.co.in/")
            .outputHeadersToFile(outHeader)
            .mode(MultiThreadMode.MSC)
            .threadCount(5)
            .showAllHeaders()
            .quiet()
            .build();
        int respCode[] = jget.sendRequest(listArg);
        println("[" + TestPrefix + "] ResponseCode=" + Arrays.toString(respCode));
        assertResponseOK(respCode, 5);
    }

    @Test
    public void doPost() throws JSONException, IOException
    {
        String output = TestPrefix + ".doPost.json";
        JGet.ArgBuilder builder = JGet.newBuilder()
            .url("https://postman-echo.com/post")
            .outputToFile(output)
            .doPost()
            .postBody("item=Apple&count=12");
        int respCode = jget.sendRequest(builder.build())[0];
        assertResponseOK(respCode);

        JSONObject jsonRoot = new JSONObject(FileUtils.readFileToString(new File(output), Charset.defaultCharset()));
        JSONObject jsonForm = jsonRoot.getJSONObject("form");
        Assert.assertEquals(jsonForm.getInt("count"), 12);
        Assert.assertEquals(jsonForm.getString("item"), "Apple");
    }
}
