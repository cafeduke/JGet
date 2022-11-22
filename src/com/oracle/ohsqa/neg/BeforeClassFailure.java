package com.oracle.ohsqa.neg;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.oracle.ohsqa.common.TestCase;

public class BeforeClassFailure extends TestCase
{
    @BeforeClass
    public void setup()
    {
        logger.info("setup -- @BeforeClass failed");
        Assert.assertTrue(false, "Setup failed");
    }

    @Test
    public void test()
    {
        logger.info("Test -- setup failed");
    }

    @AfterClass
    public void cleanup()
    {
        logger.info("cleanup");
        Assert.assertTrue(false, "Setup failed");
    }
}
