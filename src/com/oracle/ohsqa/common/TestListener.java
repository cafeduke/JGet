package com.oracle.ohsqa.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.testng.ITestClass;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class TestListener implements ITestListener
{
    @Override
    public void onTestSuccess(ITestResult result)
    {
        genSuc(result);
    }

    @Override
    public void onTestFailure(ITestResult result)
    {
        genDif(result);
    }

    @Override
    public void onTestSkipped(ITestResult result)
    {
        genSkipDif(result);
    }

    /**
     * Generate a .suc file indicating success.
     *
     * @param result Test result object
     * @throws IOException
     */
    private void genSuc(ITestResult result)
    {
        File fileSuc = new File(TestProperties.DirWork, getResultName(result) + ".suc");
        try
        {
            fileSuc.createNewFile();
        }
        catch (IOException e)
        {
            Class<?> testClass = result.getTestClass().getRealClass();
            TestUtil.exitFatal("Error creating .suc file " + fileSuc.getAbsolutePath(), e, testClass);
        }
    }

    /**
     * Generate a dif due to test case skip.
     *
     * @param result Test result object
     */
    private void genSkipDif(ITestResult result)
    {
        File fileDif = new File(TestProperties.DirWork, getResultName(result) + ".skip.dif");
        try
        {
            PrintWriter out = new PrintWriter(fileDif);
            out.println("Test skipped as its dependent methods failed.");
            out.close();
        }
        catch (IOException e)
        {
            Class<?> testClass = result.getTestClass().getRealClass();
            TestUtil.exitFatal("Error creating .dif file " + fileDif.getAbsolutePath(), e, testClass);
        }
    }

    /**
     * Generate a .dif file with the stack trace.
     *
     * @param result Test result object
     * @throws IOException
     */
    private void genDif(ITestResult result)
    {
        File fileDif = new File(TestProperties.DirWork, getResultName(result) + ".dif");
        try
        {
            Throwable throwable = result.getThrowable();
            if (throwable == null)
                return;
            PrintWriter out = new PrintWriter(fileDif);
            throwable.printStackTrace(out);
            out.close();
        }
        catch (IOException e)
        {
            Class<?> testClass = result.getTestClass().getRealClass();
            TestUtil.exitFatal("Error creating .dif file " + fileDif.getAbsolutePath(), e, testClass);
        }
    }

    /**
     * @param result TestResult object
     * @return Return the name of the .suc/.dif file to be created.
     */
    private String getResultName(ITestResult result)
    {
        ITestNGMethod testMethod = result.getMethod();
        ITestClass testClass = (ITestClass) result.getTestClass();

        String name = testClass.getName().replaceFirst(TestProperties.PackagePrefix + ".", "");
        if (testClass.getTestMethods().length > 1)
            name = name + "-" + testMethod.getMethodName();

        Object obj[] = null;
        if ((obj = result.getParameters()).length > 0)
            name = name + "." + obj[0];

        return name;
    }

}
