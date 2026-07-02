package com.tui.qa.stepdefinitions;

import com.tui.qa.data.JsonReader;
import com.tui.qa.models.SearchData;
import com.tui.qa.pages.SearchResultPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import org.testng.Assert;

public class SearchResultsSteps {

    private final SearchResultPage searchResultPage =
            new SearchResultPage();

    private final SearchData searchData =
            JsonReader.read("searchData.json", SearchData.class);

private List<String> previousHotels;
    // Hotels

    @When("the user taps on the Hotels tab")
    public void theUserTapsOnTheHotelsTab() {

        searchResultPage.clickHotelsTab();
    }

    @Then("hotel results should be displayed")
    public void hotelResultsShouldBeDisplayed() {

        Assert.assertTrue(
                searchResultPage.isHotelDisplayed(),
                "Hotel results are not displayed."
        );

        Assert.assertEquals(
            searchResultPage.getHotelsTabLabel(),
            searchData.getHotelsTabLabel(),
            "Hotels tab label does not match expected test data."
        );
    }

    

    @When("the user taps on the Holidays tab")
    public void theUserTapsOnTheHolidaysTab() {

        searchResultPage.clickHolidaysTab();
    }

    @Then("holiday results should be displayed")
    public void holidayResultsShouldBeDisplayed() {

        Assert.assertTrue(
                searchResultPage.isHolidayDisplayed(),
                "Holiday results are not displayed."
        );

        Assert.assertEquals(
            searchResultPage.getHolidaysTabLabel(),
            searchData.getHolidaysTabLabel(),
            "Holidays tab label does not match expected test data."
        );
    }

    // All

    @When("the user taps on the All tab")
    public void theUserTapsOnTheAllTab() {

        searchResultPage.clickAllTab();
    }

    @Then("both hotel and holiday results should be displayed")
    public void bothHotelAndHolidayResultsShouldBeDisplayed() {

        Assert.assertTrue(
                searchResultPage.isHotelDisplayed(),
                "Hotel results are not displayed."
        );

        Assert.assertTrue(
                searchResultPage.isHolidayDisplayed(),
                "Holiday results are not displayed."
        );
    }

    // Scenario 5 - Lazy Loading

  @When("the user scrolls down the hotel list")
public void theUserScrollsDownTheHotelList() {

    previousHotels =
            searchResultPage.getVisibleHotelNames();

    searchResultPage.scrollHotelList();
}

    @Then("a new hotel card should be displayed")
    public void aNewHotelCardShouldBeDisplayed() {

       Assert.assertTrue(
        searchResultPage.isNewHotelDisplayed(previousHotels),
        "No new hotel card was loaded after scrolling."
);
    }

    @Then("the hotel name should be displayed")
    public void theHotelNameShouldBeDisplayed() {

        Assert.assertTrue(
                searchResultPage.isHotelNameDisplayed(),
                "Hotel name is not displayed."
        );
    }

    @Then("the hotel price should be displayed")
    public void theHotelPriceShouldBeDisplayed() {

        Assert.assertTrue(
                searchResultPage.isHotelPriceDisplayed(),
                "Hotel price is not displayed."
        );
    }

    @Then("the hotel price should be in valid format")
    public void theHotelPriceShouldBeInValidFormat() {

        Assert.assertTrue(
                searchResultPage.isHotelPriceValid(searchData.getPriceRegex()),
                "Hotel price format is invalid."
        );
    }

}