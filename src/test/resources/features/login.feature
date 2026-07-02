@login
Feature: Login

  As a TUI user
  I want to login to the application
  So that I can view travel search results

  @Smoke @Login
  Scenario Outline: Login outcome by externalized credentials

    Given the user launches the application
    When the user logs in with data set "<dataSet>"
    Then the login outcome should be "<outcome>"

    Examples:
      | dataSet     | outcome |
      | validUser   | SUCCESS |
      | invalidUser | FAILURE |