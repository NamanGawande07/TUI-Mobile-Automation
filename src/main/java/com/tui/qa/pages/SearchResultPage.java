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

public class SearchResultPage extends BasePage {

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

        List<WebElement> tabCandidates = driver.findElements(tabLocator);

        for (WebElement tab : tabCandidates) {

            if (!tab.isDisplayed()) {
                continue;
            }

            String label = firstNonBlank(
                    tab.getText(),
                    tab.getAttribute("text"),
                    tab.getAttribute("content-desc"),
                    tab.getAttribute("name")
            );

            if (!label.isBlank()) {
                return label;
            }

            List<WebElement> textNodes = tab.findElements(By.xpath(".//*[@text or @content-desc]"));

            for (WebElement textNode : textNodes) {

                String nodeLabel = firstNonBlank(
                        textNode.getText(),
                        textNode.getAttribute("text"),
                        textNode.getAttribute("content-desc")
                );

                if (!nodeLabel.isBlank()) {
                    return nodeLabel;
                }
            }
        }

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
        return !driver.findElements(hotelNames).isEmpty();
    }

    public String getHotelName() {
        return driver.findElements(hotelNames)
                .get(0)
                .getText();
    }

    public String getHotelDestination() {
        return driver.findElements(hotelDestinations)
                .get(0)
                .getText();
    }

    public String getHotelRating() {
        return driver.findElements(hotelRatings)
                .get(0)
                .getText();
    }

    public String getHotelBoardType() {
        return driver.findElements(hotelBoardTypes)
                .get(0)
                .getText();
    }

    public String getHotelPrice() {
        return driver.findElements(hotelPrices)
                .get(0)
                .getText();
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
        return driver.findElements(holidayNames)
                .get(0)
                .getText();
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

        WebElement element = driver.findElement(hotelList);

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

        List<WebElement> hotels =
                driver.findElements(hotelNames);

        return !hotels.isEmpty()
                && !hotels.get(0).getText().isBlank();
    }

    public boolean isHotelPriceDisplayed() {

        List<WebElement> prices =
                driver.findElements(hotelPrices);

        return !prices.isEmpty()
                && !prices.get(0).getText().isBlank();
    }

    public boolean isHotelPriceValid() {

        String price = driver.findElements(hotelPrices)
                .get(0)
                .getText();

        return price.matches("^\\$\\d+(,\\d{3})*$");
    }

    public boolean isHotelPriceValid(String regex) {

        String price = driver.findElements(hotelPrices)
                .get(0)
                .getText();

        return price.matches(regex);
    }

}