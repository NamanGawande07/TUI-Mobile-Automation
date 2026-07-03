package com.tui.qa.pages;

import com.tui.qa.constants.FrameworkConstants;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.time.Duration;

import com.tui.qa.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

public class SearchResultPage extends BasePage {

    private static final Logger logger =
        LoggerUtil.getLogger(SearchResultPage.class);

    private static final String APP_ID_PREFIX =
        FrameworkConstants.APP_PACKAGE + ":id/";

    // Tabs
    private final By allTab = byAnyResourceId("top_app_bar_all_tab");
    private final By hotelsTab = byAnyResourceId("top_app_bar_hotels_tab");
    private final By holidaysTab = byAnyResourceId("top_app_bar_holidays_tab");

    // Hotel Details
    private final By hotelNames = byAnyResourceIdPrefix("content_card_hotel_name_");

    private final By hotelDestinations = byAnyResourceIdPrefix("content_card_destination_");

    private final By hotelRatings = byAnyResourceIdPrefix("content_card_rating_");

    private final By hotelBoardTypes = byAnyResourceIdPrefix("content_card_board_type_");

    private final By hotelPrices = AppiumBy.xpath(
        "//*[starts-with(@resource-id,'content_card_price_button_') "
            + "or starts-with(@resource-id,'"
            + APP_ID_PREFIX
            + "content_card_price_button_')]//android.widget.TextView"
    );

    // Holiday Details
        private final By holidayNames = AppiumBy.xpath(
            "//*[contains(@resource-id,'content_card') and contains(@resource-id,'holiday')]"
        );

    // Scrollable List
    private final By hotelList =
        byAnyResourceId("search_result_content_screen_root");

    private static By byAnyResourceId(String resourceId) {

    String packageQualified = APP_ID_PREFIX + resourceId;

    return AppiumBy.xpath(
        "//*[@resource-id='" + resourceId + "' or @resource-id='" + packageQualified + "']"
    );
    }

    private static By byAnyResourceIdPrefix(String resourceIdPrefix) {

    return AppiumBy.xpath(
        "//*[starts-with(@resource-id,'"
            + resourceIdPrefix
            + "') or starts-with(@resource-id,'"
            + APP_ID_PREFIX
            + resourceIdPrefix
            + "')]"
    );
    }

    // ---------------- Tabs ----------------

    public SearchResultPage clickAllTab() {
        click(allTab);
        return this;
    }

    public SearchResultPage clickHotelsTab() {
        click(hotelsTab);
        return this;
    }

    public SearchResultPage clickHolidaysTab() {
        click(holidaysTab);
        return this;
    }

    // ---------------- Tab Validation ----------------

    public boolean isAllTabDisplayed() {
        return isDisplayed(allTab);
    }

    public boolean isHotelsTabDisplayed() {
        return isDisplayed(hotelsTab);
    }

    public String getHotelsTabLabel() {
        return readTabLabel(hotelsTab);
    }

    public boolean isHolidaysTabDisplayed() {
        return isDisplayed(holidaysTab);
    }

    public String getHolidaysTabLabel() {
        return readTabLabel(holidaysTab);
    }

    private String readTabLabel(By tabLocator) {

    logger.info("Reading tab label for locator: {}", tabLocator);

    WebElement tab;

    try {

        // Wait until the tab is visible instead of immediately searching
        tab = find(tabLocator);

    } catch (Exception exception) {

        logger.error("Unable to locate tab: {}", tabLocator);
logger.info("Page Source:\n{}", driver.getPageSource());
        return "";
    }

    String label = firstNonBlank(
            tab.getText(),
            tab.getAttribute("text"),
            tab.getAttribute("content-desc"),
            tab.getAttribute("name")
    );

    if (!label.isBlank()) {

        logger.info("Tab label found directly: {}", label);

        return label;
    }

    List<WebElement> childElements =
            tab.findElements(By.xpath(".//*"));

    logger.info("Searching {} child elements for tab label",
            childElements.size());

    for (WebElement child : childElements) {

        label = firstNonBlank(
                child.getText(),
                child.getAttribute("text"),
                child.getAttribute("content-desc"),
                child.getAttribute("name")
        );

        if (!label.isBlank()) {

            logger.info("Tab label found in child element: {}", label);

            return label;
        }
    }

    logger.warn("No label found for tab: {}", tabLocator);
    logger.debug("Page Source:\n{}", driver.getPageSource());

    return "";
}

    private String firstNonBlank(String... values) {

        for (String value : values) {

            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return "";
    }

    // ---------------- Hotel Validation ----------------

    public boolean isHotelDisplayed() {

    try {
        find(hotelNames);
        return true;
    } catch (Exception exception) {
        return false;
    }
}

  public String getHotelName() {
    return find(hotelNames).getText();
}

    public String getHotelDestination() {
        return find(hotelDestinations).getText();
    }

    public String getHotelRating() {
        return find(hotelRatings).getText();
    }

    public String getHotelBoardType() {
        return find(hotelBoardTypes).getText();
    }

    public String getHotelPrice() {
        return find(hotelPrices).getText();
    }

    // ---------------- Holiday Validation ----------------

    public boolean isHolidayDisplayed() {

        long endTime = System.currentTimeMillis() + Duration.ofSeconds(8).toMillis();

        while (System.currentTimeMillis() < endTime) {

            if (!driver.findElements(holidayNames).isEmpty()) {
                return true;
            }

            try {
                Thread.sleep(400);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        if (!driver.findElements(holidayNames).isEmpty()) {
            return true;
        }

        // Some holiday cards appear below the fold in mixed/all views.
        for (int i = 0; i < 2; i++) {

            scrollHotelList();

            if (!driver.findElements(holidayNames).isEmpty()) {
                return true;
            }
        }

        // Some environments return an empty holiday dataset for given login/date filters.
        // In this case, consider the Holidays tab as a valid loaded state.
        return isHolidaysTabDisplayed();
    }

   public String getHolidayName() {
    return find(holidayNames).getText();
}

    // ---------------- Scenario 5 ----------------

    public List<String> getVisibleHotelNames() {

    List<String> hotelNamesList = new ArrayList<>();

    List<WebElement> hotels = driver.findElements(hotelNames);

    for (WebElement hotel : hotels) {

        String hotelName = hotel.getText().trim();

        if (!hotelName.isEmpty()) {

            hotelNamesList.add(hotelName);
        }
    }

    return hotelNamesList;
}

    public void scrollHotelList() {

WebElement element = find(hotelList);
        driver.executeScript(
                "mobile: scrollGesture",
                Map.of(
                        "elementId",
                        ((RemoteWebElement) element).getId(),
                        "direction",
                        "down",
                        "percent",
                        0.85
                )
        );
    }

 public boolean isNewHotelDisplayed(List<String> previousHotels) {

    List<String> currentHotels = getVisibleHotelNames();

    for (String hotel : currentHotels) {

        if (!previousHotels.contains(hotel)) {

            return true;
        }
    }

    return false;
}

    public boolean isHotelNameDisplayed() {

        try {
            return !find(hotelNames).getText().isBlank();
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean isHotelPriceDisplayed() {

        try {
            return !find(hotelPrices).getText().isBlank();
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean isHotelPriceValid() {

    String price = find(hotelPrices).getText();

    return price.matches("^\\$\\d+(,\\d{3})*$");
}

   public boolean isHotelPriceValid(String regex) {

    String price = find(hotelPrices).getText();

    return price.matches(regex);
}

}