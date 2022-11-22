package com.oracle.ohsqa.binding;

import static com.oracle.ohsqa.common.TestUtil.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.cafeduke.jreq.JReq;
import com.github.cafeduke.jreq.JReq.ArgBuilder;
import com.oracle.ohsqa.common.TestCase;
import com.oracle.ohsqa.common.Util;

public class SessionBinding extends TestCase
{
    private JReq user1, user2;

    private ArgBuilder builderBase;

    public static final String URLDukeCart = "http://localhost:18801/DukeApp/cart.jsp";

    /**
     *
     */
    @BeforeClass
    public void setup()
    {
        user1 = jreq;
        user2 = getJReq();
        builderBase = JReq.newBuilder()
            .url(URLDukeCart)
            .doPost();
    }

    @Test
    public void assertBind() throws IOException
    {

        user1.setSessionBinding(true);
        user2.setSessionBinding(true);

        // Add to cart
        sendCartRequest(user1, "item=Apple&count=7", TestPrefix + ".u1r1.properties");
        sendCartRequest(user2, "item=Apple&count=3", TestPrefix + ".u2r1.properties");
        sendCartRequest(user1, "item=Mango&count=5", TestPrefix + ".u1r2.properties");
        sendCartRequest(user2, "item=Orange&count=8", TestPrefix + ".u2r2.properties");
        sendCartRequest(user1, "item=Apple", TestPrefix + ".u1r3.properties");
        sendCartRequest(user2, "item=Apple", TestPrefix + ".u2r3.properties");

        // List cart
        user1.sendRequest(JReq.newBuilder().url(URLDukeCart).outputToFile(TestPrefix + ".u1.cart.properties").build());
        user2.sendRequest(JReq.newBuilder().url(URLDukeCart).outputToFile(TestPrefix + ".u2.cart.properties").build());

        // Assert cart entries
        Properties cartUser1 = Util.loadProperties(TestPrefix + ".u1.cart.properties");
        Properties cartUser2 = Util.loadProperties(TestPrefix + ".u2.cart.properties");
        Assert.assertEquals(cartUser1.getProperty("item.Apple"), "8");
        Assert.assertEquals(cartUser1.getProperty("item.Mango"), "5");
        logger.info("Cart entries of user1 are fine");
        Assert.assertEquals(cartUser2.getProperty("item.Apple"), "4");
        Assert.assertEquals(cartUser2.getProperty("item.Orange"), "8");
        logger.info("Cart entries of user2 are fine");
    }

    private void sendCartRequest(JReq user, String body, String output) throws IOException
    {
        // Create a list of all arguments
        ArgBuilder builderAppend = JReq.newBuilder().postBody(body).outputToFile(output);
        List<String> listArg = new ArrayList<>(builderBase.build());
        listArg.addAll(builderAppend.build());

        assertResponseOK(user.sendRequest(listArg)[0]);
        Properties prop = Util.loadProperties(output);
        logger.info(output + "=" + prop.toString());
    }

    @AfterClass
    public void cleanup()
    {
        ArgBuilder builder = JReq.newBuilder()
            .url("http://localhost:18801/DukeApp/cart.jsp?ctrl=rm")
            .doPost();

        user1.sendRequest(builder.outputToFile(TestPrefix + ".u1.end.out").build());
        user2.sendRequest(builder.outputToFile(TestPrefix + ".u2.end.out").build());
    }
}
