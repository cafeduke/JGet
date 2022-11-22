package com.oracle.ohsqa.basic;

import org.testng.annotations.Test;

import com.oracle.ohsqa.common.TestCase;

public class HelloWorld extends TestCase
{
    @Test
    public void testA()
    {
        logger.info("HelloWorld");
    }
}
