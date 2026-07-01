package com.tui.qa.driver;

import com.tui.qa.utils.LoggerUtil;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.Logger;

public final class DriverManager {

    private static final Logger logger =
            LoggerUtil.getLogger(DriverManager.class);

    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void setDriver(AppiumDriver driver) {

        DRIVER.set(driver);

        logger.info("Driver stored in ThreadLocal.");
    }

    public static AppiumDriver getDriver() {

        return DRIVER.get();
    }

    public static void quitDriver() {

        AppiumDriver driver = DRIVER.get();

        if (driver != null) {

            logger.info("Closing Android Driver.");

            driver.quit();

            DRIVER.remove();

            logger.info("Driver removed from ThreadLocal.");
        }
    }
}