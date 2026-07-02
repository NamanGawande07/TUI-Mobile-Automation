package com.tui.qa.hooks;

import com.tui.qa.driver.DriverInitializer;
import com.tui.qa.driver.DriverManager;
import com.tui.qa.utils.LoggerUtil;
import com.tui.qa.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

    private static final Logger logger =
            LoggerUtil.getLogger(Hooks.class);

    private int stepCounter;

    @Before
    public void setUp(Scenario scenario) {

        logger.info("Starting Scenario: {}", scenario.getName());

        stepCounter = 0;

        DriverInitializer.initializeDriver();
    }

    @AfterStep
    public void captureStepEvidence(Scenario scenario) {

        if (DriverManager.getDriver() == null) {
            return;
        }

        stepCounter++;

        String safeScenarioName = toSafeName(scenario.getName());
        String screenshotName = safeScenarioName + "_step_" + stepCounter;

        ScreenshotUtil.captureScreenshot(screenshotName);
    }

    @After
    public void tearDown(Scenario scenario) {

        try {

            if (scenario.isFailed()) {

                logger.error("Scenario Failed: {}", scenario.getName());

                if (DriverManager.getDriver() != null) {

                    byte[] screenshot =
                            ((TakesScreenshot) DriverManager.getDriver())
                                    .getScreenshotAs(OutputType.BYTES);

                    scenario.attach(
                            screenshot,
                            "image/png",
                            scenario.getName()
                    );

                        ScreenshotUtil.captureScreenshot(
                            toSafeName(scenario.getName()) + "_FAILED"
                        );
                }

            } else {

                logger.info("Scenario Passed: {}", scenario.getName());
            }

        } finally {

            DriverManager.quitDriver();

            logger.info("Driver closed successfully.");
        }
    }

    private String toSafeName(String value) {

        return value
                .replaceAll("[^a-zA-Z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}