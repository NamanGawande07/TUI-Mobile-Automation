@search
Feature: Search Results

  Background:
    Given the user is logged into the application

  @Regression @Hotels
  Scenario: Verify Hotels tab

    When the user taps on the Hotels tab
    Then hotel results should be displayed

  @Regression @Holidays
  Scenario: Verify Holidays tab

    When the user taps on the Holidays tab
    Then holiday results should be displayed

  @Regression @All
  Scenario: Verify All tab

    When the user taps on the All tab
    Then both hotel and holiday results should be displayed

  @Regression @LazyLoading
  Scenario: Verify hotel lazy loading

    When the user taps on the Hotels tab
    And the user scrolls down the hotel list
    Then a new hotel card should be displayed
    And the hotel name should be displayed
    And the hotel price should be displayed
    And the hotel price should be in valid format