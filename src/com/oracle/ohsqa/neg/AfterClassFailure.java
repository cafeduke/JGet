package com.oracle.ohsqa.neg;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.oracle.ohsqa.common.TestCase;

public class AfterClassFailure extends TestCase
{
    @BeforeClass
    public void setup()
    {
        logger.info("setup");
    }

    @Test
    public void test()
    {
        logger.info("Test");
    }

    @AfterClass
    public void cleanup()
    {
        logger.info("cleanup");
        Assert.assertTrue(false, "clenaup failed");
    }
}
