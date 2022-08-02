package com.github.cafeduke.jreq.common;

import org.testng.annotations.BeforeClass;

import com.github.cafeduke.jreq.JReq;

public class TestCase
{
    public static final String PackagePrefix = "com.github.cafeduke";

    public static final String PackagePrefixDot = "com.github.cafeduke.";

    public final String TestPrefix = getClass().getName().replaceFirst(PackagePrefixDot, "");

    protected JReq fireduke = null;

    @BeforeClass
    public void setup()
    {
        fireduke = JReq.getInstance();
    }
}
