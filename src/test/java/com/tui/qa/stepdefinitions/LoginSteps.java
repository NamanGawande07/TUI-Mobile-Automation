package com.tui.qa.stepdefinitions;

import com.tui.qa.data.JsonReader;
import com.tui.qa.models.LoginData;
import com.tui.qa.pages.LoginPage;
import com.tui.qa.pages.SearchResultPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LoginSteps {

    private final LoginPage loginPage = new LoginPage();
    private final SearchResultPage searchResultPage = new SearchResultPage();

    private final LoginData loginData =
            JsonReader.read("loginData.json", LoginData.class);

    @Given("the user launches the application")
    public void theUserLaunchesTheApplication() {

        Assert.assertTrue(
                loginPage.isLoginPageDisplayed(),
                "Login page should be displayed."
        );
    }

    @Given("the user is logged into the application")
    public void theUserIsLoggedIntoTheApplication() {

        loginPage.login(
                loginData.getUsername(),
                loginData.getPassword(),
                loginData.getDateOfBirth()
        );

        Assert.assertTrue(
                searchResultPage.isHotelsTabDisplayed(),
                "User is not navigated to Search Results page."
        );
    }

    @When("the user enters a valid username")
    public void theUserEntersAValidUsername() {

        loginPage.enterUsername(loginData.getUsername());
    }

    @When("the user enters a valid password")
    public void theUserEntersAValidPassword() {

        loginPage.enterPassword(loginData.getPassword());
    }

    @When("the user enters a valid date of birth")
    public void theUserEntersAValidDateOfBirth() {

        loginPage.selectDateOfBirth(loginData.getDateOfBirth());
    }

    @When("the user taps the Login button")
    public void theUserTapsTheLoginButton() {

        loginPage.tapSubmit();
    }

    @Then("the search results page should be displayed")
    public void theSearchResultsPageShouldBeDisplayed() {

        Assert.assertTrue(
                searchResultPage.isHotelsTabDisplayed(),
                "Search Results page should be displayed."
        );
    }
}