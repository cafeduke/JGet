package com.oracle.ohsqa.packB;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.oracle.ohsqa.common.TestCase;
import com.oracle.ohsqa.common.TestUtil;

public class ClassB2 extends TestCase
{
    @BeforeClass
    public void setup()
    {
        TestUtil.sleepAndLog(logger, "Setup", 25);
    }

    @Test
    public void test()
    {
        TestUtil.sleepAndLog(logger, "Test " + getClass().getSimpleName(), 300);
    }

    @AfterClass
    public void clean()
    {
        TestUtil.sleepAndLog(logger, "Clean", 25);
    }
}
