package com.tui.qa.stepdefinitions;

import com.tui.qa.data.JsonReader;
import com.tui.qa.models.LoginData;
import com.tui.qa.models.LoginTestData;
import com.tui.qa.pages.LoginPage;
import com.tui.qa.pages.SearchResultPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.Locale;

public class LoginSteps {

    private final LoginPage loginPage = new LoginPage();
    private final SearchResultPage searchResultPage = new SearchResultPage();

        private final LoginTestData loginTestData =
            JsonReader.read("loginData.json", LoginTestData.class);

    @Given("the user launches the application")
    public void theUserLaunchesTheApplication() {

        Assert.assertTrue(
                loginPage.isLoginPageDisplayed(),
                "Login page should be displayed."
        );
    }

    @Given("the user is logged into the application")
    public void theUserIsLoggedIntoTheApplication() {

        LoginData validUser = loginTestData.getValidUser();

        loginPage.login(
            validUser.getUsername(),
            validUser.getPassword(),
            validUser.getDateOfBirth()
        );

        Assert.assertTrue(
                searchResultPage.isHotelsTabDisplayed(),
                "User is not navigated to Search Results page."
        );
    }

    @When("the user logs in with data set {string}")
    public void theUserLogsInWithDataSet(String dataSet) {

        LoginData loginData = resolveDataSet(dataSet);

        loginPage.login(
                loginData.getUsername(),
                loginData.getPassword(),
                loginData.getDateOfBirth()
        );
    }

    @Then("the login outcome should be {string}")
    public void theLoginOutcomeShouldBe(String outcome) {

        String normalizedOutcome = outcome.trim().toUpperCase(Locale.ENGLISH);

        if ("SUCCESS".equals(normalizedOutcome)) {

            Assert.assertTrue(
                    searchResultPage.isHotelsTabDisplayed(),
                    "Search Results page should be displayed for a valid login."
            );

            return;
        }

        if ("FAILURE".equals(normalizedOutcome)) {

            Assert.assertTrue(
                    loginPage.isLoginPageDisplayed(),
                    "Login page should remain visible for invalid credentials."
            );

            Assert.assertFalse(
                    searchResultPage.isHotelsTabDisplayed(),
                    "Search results page should not be displayed for invalid login."
            );

            return;
        }

        throw new IllegalArgumentException("Unsupported outcome: " + outcome);
    }

    private LoginData resolveDataSet(String dataSet) {

        if ("validUser".equalsIgnoreCase(dataSet)) {
            return loginTestData.getValidUser();
        }

        if ("invalidUser".equalsIgnoreCase(dataSet)) {
            return loginTestData.getInvalidUser();
        }

        throw new IllegalArgumentException("Unknown login data set: " + dataSet);
    }
}