@login
Feature: Login

  As a TUI user
  I want to login to the application
  So that I can view travel search results

  @Smoke @Login
  Scenario: Login with valid user details

    Given the user launches the application
    When the user enters a valid username
    And the user enters a valid password
    And the user enters a valid date of birth
    And the user taps the Login button
    Then the search results page should be displayed