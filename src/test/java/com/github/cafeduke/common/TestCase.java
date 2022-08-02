package com.github.cafeduke.common;

import org.testng.annotations.BeforeClass;

import com.github.cafeduke.Fireduke;

public class TestCase
{
    public static final String PackagePrefix = "com.github.cafeduke";

    public static final String PackagePrefixDot = "com.github.cafeduke.";

    public final String TestPrefix = getClass().getName().replaceFirst(PackagePrefixDot, "");

    protected Fireduke fireduke = null;

    @BeforeClass
    public void setup()
    {
        fireduke = Fireduke.getInstance();
    }
}
