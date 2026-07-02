package com.tui.qa.constants;

import com.tui.qa.config.ConfigReader;
import com.tui.qa.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

public final class FrameworkConstants {

    private static final Logger logger =
            LoggerUtil.getLogger(FrameworkConstants.class);

    static {
        logger.info("Loading framework configuration.");
    }

    private FrameworkConstants() {
    }

    // Platform Configuration
    public static final String PLATFORM_NAME =
            ConfigReader.getProperty("platformName");

    public static final String AUTOMATION_NAME =
            ConfigReader.getProperty("automationName");

    public static final String PLATFORM_VERSION =
            ConfigReader.getOptionalProperty("platformVersion");

    // Device Configuration
    public static final String DEVICE_NAME =
            ConfigReader.getOptionalProperty("deviceName");

    public static final String UDID =
            ConfigReader.getOptionalProperty("udid");

    // Application Configuration
    public static final String APP =
            ConfigReader.getProperty("app");

    public static final String APP_PACKAGE =
            ConfigReader.getProperty("appPackage");

    public static final String APP_ACTIVITY =
            ConfigReader.getOptionalProperty("appActivity");

    // Appium Configuration
    public static final String APPIUM_SERVER =
            ConfigReader.getProperty("appiumServerURL");

    // Wait Configuration
    public static final int IMPLICIT_WAIT =
            Integer.parseInt(ConfigReader.getProperty("implicitWait"));

    public static final int EXPLICIT_WAIT =
            Integer.parseInt(ConfigReader.getProperty("explicitWait"));
}