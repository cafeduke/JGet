package com.oracle.ohsqa.neg;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.oracle.ohsqa.common.TestCase;

public class TestFailure extends TestCase
{
    @Test
    public void testA()
    {
        logger.info("Basic failure test");
        Assert.assertTrue(false, "Assertion failed");
    }
}
