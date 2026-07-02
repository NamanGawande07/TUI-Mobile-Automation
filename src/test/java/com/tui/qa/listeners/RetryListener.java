package com.tui.qa.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class RetryListener implements IAnnotationTransformer {

    @Override
    @SuppressWarnings("rawtypes")
    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod) {

        if (testClass != null
                && "com.tui.qa.runner.TestRunner".equals(testClass.getName())) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}