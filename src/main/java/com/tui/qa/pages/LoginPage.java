package com.tui.qa.pages;

import com.tui.qa.config.ConfigReader;
import com.tui.qa.constants.FrameworkConstants;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginPage extends BasePage {

    private static final int MAX_MONTH_NAVIGATION_ATTEMPTS = 18;
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER =
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TEXT_FORMATTER =
        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter ALT_DATE_TEXT_FORMATTER =
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
    private static final Pattern MONTH_YEAR_PATTERN =
        Pattern.compile("(January|February|March|April|May|June|July|August|September|October|November|December)\\s+\\d{4}");
    private static final int MAX_DATE_SELECTION_ATTEMPTS = 2;

    // Login Form
    private final By loginFormRoot = byAnyResourceId("login_form_screen_root");
    private final By usernameField = byAnyResourceId("username_input_field");
    private final By passwordField = byAnyResourceId("password_input_field");
    private final By dateOfBirthField = byAnyResourceId("date_of_birth_field");
        private final By dateOfBirthCalendarIcon =
            byAnyResourceId("date_of_birth_field_calendar_icon");
    private final By submitButton = byAnyResourceId("login_form_submit_button");
    private final By nextMonthButton =
            AppiumBy.accessibilityId("Change to next month");
    private final By previousMonthButton =
            AppiumBy.accessibilityId("Change to previous month");
    private final By confirmButton =
            byAnyResourceId("date_of_birth_dialog_confirm_button");

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
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    public LoginPage selectDateOfBirth(String dateOfBirth) {

        LocalDate targetDate = resolveTargetDate(dateOfBirth);
        YearMonth targetMonth = YearMonth.from(targetDate);

        for (int selectionAttempt = 0; selectionAttempt < MAX_DATE_SELECTION_ATTEMPTS; selectionAttempt++) {

            click(dateOfBirthField);

            if (driver.findElements(confirmButton).isEmpty()) {
                click(dateOfBirthCalendarIcon);
            }

            waitForVisibility(confirmButton);

            int navigationAttempts = 0;

            while (navigationAttempts < MAX_MONTH_NAVIGATION_ATTEMPTS) {

                YearMonth visibleMonth = readVisibleMonth();

                if (visibleMonth != null && visibleMonth.equals(targetMonth)) {
                    break;
                }

                if (visibleMonth != null && visibleMonth.isAfter(targetMonth)) {
                    click(previousMonthButton);
                } else {
                    click(nextMonthButton);
                }

                navigationAttempts++;
            }

            if (clickTargetDate(targetDate)) {
                click(confirmButton);
                return this;
            }

            YearMonth visibleMonth = readVisibleMonth();

            if (visibleMonth != null
                    && visibleMonth.equals(targetMonth)
                    && clickDayCellWhenTargetMonthVisible(targetDate.getDayOfMonth())) {
                click(confirmButton);
                return this;
            }

            driver.navigate().back();
        }

        throw new IllegalStateException("Unable to select date of birth: " + targetDate);
    }

    private WebElement findTargetDateByContentDesc(LocalDate targetDate) {

        List<WebElement> nodesWithContentDesc = driver.findElements(
                AppiumBy.xpath("//*[@content-desc]")
        );

        for (WebElement element : nodesWithContentDesc) {
            String contentDesc = element.getAttribute("content-desc");

            if (contentDesc == null || contentDesc.isBlank()) {
                continue;
            }

            String normalized = contentDesc
                    .replace(",", " ")
                    .replaceAll("\\s+", " ")
                    .toLowerCase(Locale.ENGLISH)
                    .trim();

                String monthName = targetDate.getMonth().name().toLowerCase(Locale.ENGLISH);
                monthName = monthName.substring(0, 1).toUpperCase(Locale.ENGLISH)
                    + monthName.substring(1).toLowerCase(Locale.ENGLISH);

                int targetDay = targetDate.getDayOfMonth();
                int targetMonth = targetDate.getMonthValue();
                int targetYear = targetDate.getYear();

                boolean hasTargetYear =
                    normalized.matches(".*(^|\\D)" + targetYear + "(\\D|$).*");
                boolean hasTargetDay =
                    normalized.matches(".*(^|\\D)0?" + targetDay + "(\\D|$).*");
                boolean hasTargetMonthText =
                    normalized.contains(monthName.toLowerCase(Locale.ENGLISH));
                boolean hasTargetMonthNumeric =
                    normalized.matches(".*(^|\\D)0?" + targetMonth + "(\\D|$).*");

                if (hasTargetYear
                    && hasTargetDay
                    && (hasTargetMonthText || hasTargetMonthNumeric)
                    && element.isDisplayed()
                    && element.isEnabled()) {
                return element;
            }
        }

        return null;
    }

            private boolean clickDayCellWhenTargetMonthVisible(int targetDay) {

            String targetDayText = String.valueOf(targetDay);
            String targetDayPadded = String.format("%02d", targetDay);

        List<WebElement> dayCells = driver.findElements(
                AppiumBy.xpath("//*[@text='" + targetDayText + "' or @text='" + targetDayPadded + "']")
        );

        for (WebElement dayCell : dayCells) {
            if (dayCell.isDisplayed() && dayCell.isEnabled()) {
                dayCell.click();
                return true;
            }
        }

        return false;
    }

    private boolean clickTargetDate(LocalDate targetDate) {

        String dateText = targetDate.format(DATE_TEXT_FORMATTER);
        String altDateText = targetDate.format(ALT_DATE_TEXT_FORMATTER);

        By[] candidateLocators = new By[] {
                AppiumBy.androidUIAutomator("new UiSelector().textContains(\"" + dateText + "\")"),
                AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + dateText + "\")"),
                AppiumBy.androidUIAutomator("new UiSelector().textContains(\"" + altDateText + "\")"),
                AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + altDateText + "\")")
        };

        for (By locator : candidateLocators) {
            List<WebElement> matches = driver.findElements(locator);

            for (WebElement match : matches) {
                if (match.isDisplayed() && match.isEnabled()) {
                    match.click();
                    return true;
                }
            }
        }

        WebElement byContentDesc = findTargetDateByContentDesc(targetDate);

        if (byContentDesc != null) {
            byContentDesc.click();
            return true;
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

    private YearMonth readVisibleMonth() {

        List<WebElement> nodesWithContentDesc = driver.findElements(
                AppiumBy.xpath("//*[@content-desc]")
        );

        for (WebElement element : nodesWithContentDesc) {

            String contentDesc = element.getAttribute("content-desc");

            if (contentDesc == null || contentDesc.isBlank()) {
                continue;
            }

            Matcher matcher = MONTH_YEAR_PATTERN.matcher(contentDesc);

            if (matcher.find()) {

                try {
                    return YearMonth.parse(matcher.group(0), MONTH_YEAR_FORMATTER);
                } catch (DateTimeParseException ignored) {
                    // Continue to check other nodes.
                }
            }
        }

        return null;
    }

    public LoginPage tapSubmit() {
        click(submitButton);
        return this;
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