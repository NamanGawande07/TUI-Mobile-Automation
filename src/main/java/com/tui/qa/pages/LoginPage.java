package com.tui.qa.pages;

import com.tui.qa.config.ConfigReader;
import com.tui.qa.constants.FrameworkConstants;
import com.tui.qa.utils.LoggerUtil;
import com.tui.qa.utils.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class LoginPage extends BasePage {

    private static final Logger logger =
            LoggerUtil.getLogger(LoginPage.class);

    private static final DateTimeFormatter DIALOG_TEXT_INPUT_FORMATTER =
        DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
    private static final int MAX_DOB_INPUT_ATTEMPTS = 3;
    private static final int LOGIN_SCREEN_READY_TIMEOUT_SECONDS = 90;

    // Login Form
    private final By loginFormRoot = byAnyResourceId("login_form_screen_root");
    private final By usernameField = byAnyResourceId("username_input_field");
    private final By passwordField = byAnyResourceId("password_input_field");
    private final By dateOfBirthField = byAnyResourceId("date_of_birth_field");
        private final By dateOfBirthCalendarIcon =
                byAnyResourceId("date_of_birth_field_calendar_icon");
        private final By dateOfBirthCalendarIconButton =
                AppiumBy.xpath("//android.view.View[@resource-id='date_of_birth_field_calendar_icon']/android.widget.Button");
        private final By dateOfBirthFieldFallback =
                AppiumBy.xpath("//*[contains(@resource-id,'date_of_birth') and (@clickable='true' or @focusable='true')]");
        private final By dateOfBirthCalendarIconFallback =
                AppiumBy.xpath("//*[contains(@resource-id,'calendar') and (@clickable='true' or @focusable='true')]");
    private final By submitButton = byAnyResourceId("login_form_submit_button");
        private final By allowButtonText =
            AppiumBy.androidUIAutomator(
                "new UiSelector().classNameMatches(\"android.widget.(Button|TextView)\")"
                    + ".textMatches(\"(?i)allow|while using the app|only this time|continue|ok\")"
            );
        private final By onboardingButtonText =
            AppiumBy.androidUIAutomator(
                "new UiSelector().classNameMatches(\"android.widget.(Button|TextView)\")"
                    + ".textMatches(\"(?i)skip|next|get started|start|continue\")"
            );
        private final By datePickerDialog = byAnyResourceId("date_of_birth_dialog");
        private final By dialogDateInputField =
            AppiumBy.xpath("//android.view.View[@resource-id='date_of_birth_dialog']//android.widget.EditText");
            private final By dialogAnyEditTextField =
                AppiumBy.xpath("//android.widget.EditText");
            private final By focusedEditTextField =
                AppiumBy.androidUIAutomator(
                    "new UiSelector().className(\"android.widget.EditText\").focused(true)"
                );
        private final By switchToTextInputMode =
            AppiumBy.accessibilityId("Switch to text input mode");
        private final By switchToCalendarInputMode =
            AppiumBy.accessibilityId("Switch to calendar input mode");
        private final By switchModeFallbackButton =
            AppiumBy.xpath("//android.view.View[@resource-id='date_of_birth_dialog']//android.view.View[@content-desc='Switch to text input mode']/following-sibling::android.widget.Button");
    private final By confirmButton =
            byAnyResourceId("date_of_birth_dialog_confirm_button");
        private final By confirmButtonChild =
            AppiumBy.xpath("//android.view.View[@resource-id='date_of_birth_dialog_confirm_button']/android.widget.Button");
        private final By systemConfirmButton =
            AppiumBy.id("android:id/button1");
        private final By textConfirmButton =
            AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"android.widget.Button\").textMatches(\"(?i)ok|done|confirm\")"
            );

    private static By byAnyResourceId(String resourceId) {

    String packageQualified = FrameworkConstants.APP_PACKAGE + ":id/" + resourceId;

    return AppiumBy.xpath(
        "//*[@resource-id='" + resourceId + "' or @resource-id='" + packageQualified + "']"
    );
    }

    // Validation
    public boolean isLoginPageDisplayed() {
    return isDisplayed(loginFormRoot);
    }

    // Actions
    public LoginPage enterUsername(String username) {
        waitForLoginScreenReady();
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        dismissKeyboardIfPresent();
        return this;
    }

    public LoginPage selectDateOfBirth(String dateOfBirth) {

        LocalDate targetDate = resolveTargetDate(dateOfBirth);
        openDatePickerDialog();
        waitForVisibility(datePickerDialog);
        ensureDialogTextInputMode();
        typeDateInDialog(targetDate);
        confirmDateSelectionIfNeeded();

        return this;
    }

    private void ensureDialogTextInputMode() {

        // When this element is visible, picker is already in text mode.
        if (isDisplayed(switchToCalendarInputMode)) {
            return;
        }

        if (clickIfPresent(switchToTextInputMode)
                || clickIfPresent(switchModeFallbackButton)) {
            return;
        }

        throw new IllegalStateException("Unable to switch date picker to text input mode.");
    }

    private void typeDateInDialog(LocalDate targetDate) {

        String dateValue = targetDate.format(DIALOG_TEXT_INPUT_FORMATTER);
        String dateDigits = digitsOnly(dateValue);

        for (int attempt = 1; attempt <= MAX_DOB_INPUT_ATTEMPTS; attempt++) {

            WebElement input = findDialogInputElement();
            input.click();

            clearAndType(input, dateDigits);

            if (!isDatePresentInDialogInput(dateValue)) {
                typeUsingFocusedField(dateDigits);
            }

            dismissKeyboardIfPresent();

            if (isDatePresentInDialogInput(dateValue)) {
                return;
            }
        }

        throw new IllegalStateException("Date value was not populated in DOB dialog input.");
    }

    private WebElement findDialogInputElement() {

        List<WebElement> primaryInputs = driver.findElements(dialogDateInputField);

        for (WebElement input : primaryInputs) {
            if (input.isDisplayed() && input.isEnabled()) {
                return input;
            }
        }

        List<WebElement> fallbackInputs = driver.findElements(dialogAnyEditTextField);

        for (WebElement input : fallbackInputs) {
            if (input.isDisplayed() && input.isEnabled()) {
                return input;
            }
        }

        throw new IllegalStateException("DOB dialog input field is not available.");
    }

    private void clearAndType(WebElement input, String dateValue) {

        try {
            input.clear();
        } catch (Exception ignored) {
            // Continue and attempt to overwrite.
        }

        input.sendKeys(dateValue);
    }

    private void typeUsingFocusedField(String dateValue) {

        List<WebElement> focusedInputs = driver.findElements(focusedEditTextField);

        if (focusedInputs.isEmpty()) {
            return;
        }

        WebElement focused = focusedInputs.get(0);

        clearAndType(focused, dateValue);
    }

    private boolean isDatePresentInDialogInput(String expectedDate) {

        String expectedDigits = digitsOnly(expectedDate);

        List<WebElement> inputs = driver.findElements(dialogAnyEditTextField);

        for (WebElement input : inputs) {

            if (!input.isDisplayed()) {
                continue;
            }

            String text = safeAttr(input, "text");
            String contentDesc = safeAttr(input, "content-desc");
            String value = safeAttr(input, "value");

            if (matchesExpectedDigits(text, expectedDigits)
                    || matchesExpectedDigits(contentDesc, expectedDigits)
                    || matchesExpectedDigits(value, expectedDigits)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesExpectedDigits(String candidate, String expectedDigits) {

        if (candidate == null || candidate.isBlank()) {
            return false;
        }

        return digitsOnly(candidate).contains(expectedDigits);
    }

    private String safeAttr(WebElement element, String name) {

        try {
            String value = element.getAttribute(name);
            return value == null ? "" : value;
        } catch (Exception ignored) {
            return "";
        }
    }

    private String digitsOnly(String text) {

        return text.replaceAll("\\D", "");
    }

    private void confirmDateSelectionIfNeeded() {

        if (clickIfPresent(confirmButtonChild)
                || clickIfPresent(confirmButton)
                || clickIfPresent(systemConfirmButton)
                || clickIfPresent(textConfirmButton)) {
            return;
        }

        // Some picker variants auto-apply selection with no confirm CTA.
    }

    private void openDatePickerDialog() {

        dismissKeyboardIfPresent();

        if (clickIfPresent(dateOfBirthCalendarIconButton)) {
            return;
        }

        if (clickIfPresent(dateOfBirthCalendarIcon)) {
            return;
        }

        if (clickIfPresent(dateOfBirthField)) {
            return;
        }

        try {
            click(dateOfBirthField);
            return;
        } catch (RuntimeException ignored) {
            // Fall back to alternate strategies when the primary click is blocked.
        }

        try {
            click(dateOfBirthCalendarIcon);
            return;
        } catch (RuntimeException ignored) {
            // Fall back to alternate strategies when the icon click is unavailable.
        }

        if (clickDatePickerByScrollableSearch()) {
            return;
        }

        if (clickIfPresent(dateOfBirthField)
                || clickIfPresent(dateOfBirthCalendarIcon)
                || clickIfPresent(dateOfBirthFieldFallback)
                || clickIfPresent(dateOfBirthCalendarIconFallback)
                || clickFirstMatchingNodeWithText("date")
                || clickFirstMatchingNodeWithText("birth")) {
            return;
        }

        throw new IllegalStateException("Unable to open date picker dialog.");
    }

    private boolean clickDatePickerByScrollableSearch() {

        By[] locators = new By[] {
                dateOfBirthField,
                dateOfBirthCalendarIcon,
                dateOfBirthFieldFallback,
                dateOfBirthCalendarIconFallback
        };

        for (By locator : locators) {

            try {
                driver.findElement(
                        AppiumBy.androidUIAutomator(
                                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceIdMatches(\".*date_of_birth.*\"))"
                        )
                );
            } catch (Exception ignored) {
                // Continue to direct locator click attempts.
            }

            if (clickIfPresent(locator)) {
                return true;
            }
        }

        return false;
    }

    private void dismissKeyboardIfPresent() {

        try {
            driver.executeScript("mobile: hideKeyboard");
        } catch (WebDriverException ignored) {
            // Keyboard may already be hidden on some devices/emulators.
        }
    }

    private boolean clickIfPresent(By locator) {

        List<WebElement> candidates = driver.findElements(locator);

        for (WebElement candidate : candidates) {
            if (candidate.isDisplayed() && candidate.isEnabled()) {
                candidate.click();
                return true;
            }
        }

        return false;
    }

    private boolean clickFirstMatchingNodeWithText(String textFragment) {

        List<WebElement> nodes = driver.findElements(
                AppiumBy.xpath("//*[@text or @content-desc]")
        );

        for (WebElement node : nodes) {

            String text = node.getAttribute("text");
            String contentDesc = node.getAttribute("content-desc");

            String merged = ((text == null ? "" : text) + " "
                    + (contentDesc == null ? "" : contentDesc)).toLowerCase(Locale.ENGLISH);

            if (merged.contains(textFragment.toLowerCase(Locale.ENGLISH))
                    && node.isDisplayed()
                    && node.isEnabled()) {
                node.click();
                return true;
            }
        }

        return false;
    }


    private LocalDate resolveTargetDate(String providedDate) {

        String candidate = providedDate;

        if (candidate == null || candidate.isBlank()) {
            candidate = ConfigReader.getOptionalProperty("dateOfBirth");
        }

        if (candidate == null || candidate.isBlank()) {
            throw new IllegalStateException(
                    "No date of birth provided. Pass it in test data or config.properties (dateOfBirth)."
            );
        }

        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ISO_LOCAL_DATE
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(candidate.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // Try next format.
            }
        }

        throw new IllegalStateException(
                "Unsupported date format for dateOfBirth: " + candidate
        );
    }

    public LoginPage tapSubmit() {
        click(submitButton);
        return this;
    }

    private void waitForLoginScreenReady() {

        long timeoutAt = System.currentTimeMillis()
                + (LOGIN_SCREEN_READY_TIMEOUT_SECONDS * 1000L);

        while (System.currentTimeMillis() < timeoutAt) {

            if (isElementReady(loginFormRoot) || isElementReady(usernameField)) {
                return;
            }

            // Best-effort handling for permission and onboarding overlays.
            if (clickIfPresent(allowButtonText) || clickIfPresent(onboardingButtonText)) {
                continue;
            }

            dismissKeyboardIfPresent();

            try {
                if (driver instanceof AndroidDriver androidDriver) {
                    logger.info("Waiting for login screen. Current activity: {}", androidDriver.currentActivity());
                    logger.info("Waiting for login screen. Current package: {}", androidDriver.getCurrentPackage());
                }
            } catch (Exception ignored) {
                // Activity/package may not be available on all driver states.
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        captureLoginStartupDiagnostics();

        throw new IllegalStateException("Login screen was not ready within timeout.");
    }

    private boolean isElementReady(By locator) {

        List<WebElement> elements = driver.findElements(locator);

        for (WebElement element : elements) {
            if (element.isDisplayed() && element.isEnabled()) {
                return true;
            }
        }

        return false;
    }

    private void captureLoginStartupDiagnostics() {

        try {
            String source = driver.getPageSource();
            logger.error(
                    "Login startup diagnostics | username_input_field present in page source: {}",
                    source.contains("username_input_field")
            );
        } catch (Exception exception) {
            logger.error("Unable to capture login startup page source.", exception);
        }

        try {
            String screenshotPath = ScreenshotUtil.captureScreenshot("login_screen_not_ready");
            logger.error("Login startup screenshot captured at: {}", screenshotPath);
        } catch (Exception exception) {
            logger.error("Unable to capture login startup screenshot.", exception);
        }
    }

    public void login(String username,
                      String password,
                      String dateOfBirth) {

        enterUsername(username)
                .enterPassword(password)
                .selectDateOfBirth(dateOfBirth)
                .tapSubmit();
    }
}