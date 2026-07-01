package com.tui.qa.pages;

import com.tui.qa.constants.FrameworkConstants;
import com.tui.qa.driver.DriverManager;
import com.tui.qa.utils.LoggerUtil;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final AppiumDriver driver;
    protected final WebDriverWait wait;

    private static final Logger logger =
            LoggerUtil.getLogger(BasePage.class);

    protected BasePage() {

        this.driver = DriverManager.getDriver();

        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(FrameworkConstants.EXPLICIT_WAIT)
        );
    }

    protected WebElement find(By locator) {

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    protected WebElement getElement(By locator) {

        return find(locator);
    }

    protected void click(By locator) {

        logger.info("Clicking on element: {}", locator);

        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String value) {

        logger.info("Entering text into element: {}", locator);

        WebElement element = find(locator);

        element.clear();

        element.sendKeys(value);
    }

    protected void clear(By locator) {

        find(locator).clear();
    }

    protected String getText(By locator) {

        return find(locator).getText();
    }

    protected String getAttribute(By locator, String attribute) {

        return find(locator).getAttribute(attribute);
    }

    protected boolean isDisplayed(By locator) {

        try {

            return find(locator).isDisplayed();

        } catch (TimeoutException e) {

            return false;
        }
    }

    protected boolean isEnabled(By locator) {

        return find(locator).isEnabled();
    }

    protected void waitForVisibility(By locator) {

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    protected void waitForClickable(By locator) {

        wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );
    }

    protected void waitForInvisibility(By locator) {

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(locator)
        );
    }
}