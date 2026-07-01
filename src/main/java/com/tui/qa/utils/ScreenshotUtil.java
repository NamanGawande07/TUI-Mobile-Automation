package com.tui.qa.utils;

import com.tui.qa.driver.DriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    public static String captureScreenshot(String screenshotName) {

        File source = ((TakesScreenshot) DriverManager.getDriver())
                .getScreenshotAs(OutputType.FILE);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String destination =
                "reports/screenshots/"
                        + screenshotName
                        + "_"
                        + timestamp
                        + ".png";

        try {

            FileUtils.copyFile(source, new File(destination));

        } catch (IOException e) {

            throw new RuntimeException("Unable to save screenshot.", e);
        }

        return destination;
    }
}