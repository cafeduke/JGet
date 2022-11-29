package com.github.cafeduke.jget.common;

import org.testng.annotations.BeforeClass;

import com.github.cafeduke.jget.JGet;

public class TestCase
{
    public static final String PackagePrefix = "com.github.cafeduke";

    public static final String PackagePrefixDot = "com.github.cafeduke.";

    public final String TestPrefix = getClass().getName().replaceFirst(PackagePrefixDot, "");

    protected JGet jget = null;

    @BeforeClass
    public void setup()
    {
        jget = JGet.getInstance();
    }
}
