package com.oracle.ohsqa.common;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.github.cafeduke.jreportng.AbstractTestCase;
import com.github.cafeduke.jreq.JReq;

public abstract class TestCase extends AbstractTestCase
{
    public static final String PackagePrefix = "com.oracle.ohsqa";

    public static final String PackagePrefixDot = "com.oracle.ohsqa.";

    public final String TestPrefix = getClass().getName().replaceFirst(PackagePrefixDot, "");

    protected HttpServer ohs = null;

    protected JReq jreq = null;

    private int id = -1;

    @BeforeClass
    @Parameters("ohs-id")
    public void beginClass(String ohsId)
    {
        Class<? extends TestCase> currTestClass = getClass();
        id = Integer.parseInt(ohsId);
        ohs = HttpServer.getInstance(id);
        ohs.setTestClass(currTestClass);
        ohs.setLogger(logger);

        logger.info(String.format("[Class=%s ohs-id=%d] Class level setup", ohs.getClass(), id));
        jreq = JReq.getInstance();
        jreq.setLogger(logger);
    }

    @AfterClass
    public void finishClass()
    {
        logger.info(String.format("[Class=%s ohs-id=%d] Class level cleanup", ohs.getClass(), id));
    }

    protected JReq getJReq()
    {
        JReq jreq = JReq.getInstance();
        jreq.setLogger(logger);
        return jreq;
    }
}