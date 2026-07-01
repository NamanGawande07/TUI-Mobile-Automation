package com.tui.qa.utils;

import com.tui.qa.constants.FrameworkConstants;
import com.tui.qa.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitUtils {

    private WaitUtils() {
    }

    private static WebDriverWait getWait() {

        return new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(FrameworkConstants.EXPLICIT_WAIT)
        );
    }

    public static WebElement waitForVisibility(By locator) {

        return getWait().until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    public static WebElement waitForClickable(By locator) {

        return getWait().until(
                ExpectedConditions.elementToBeClickable(locator)
        );
    }

    public static boolean waitForInvisibility(By locator) {

        return getWait().until(
                ExpectedConditions.invisibilityOfElementLocated(locator)
        );
    }
}