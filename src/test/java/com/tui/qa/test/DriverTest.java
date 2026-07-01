package com.tui.qa.test;

import com.tui.qa.driver.DriverInitializer;
import com.tui.qa.driver.DriverManager;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class DriverTest {

    @Test
    public void launchDriver() {

        DriverInitializer.initializeAndroidDriver();

        Assert.assertNotNull(
                DriverManager.getDriver(),
                "Driver should be initialized successfully."
        );
    }

    @AfterMethod
    public void tearDown() {

        DriverManager.quitDriver();
    }
}