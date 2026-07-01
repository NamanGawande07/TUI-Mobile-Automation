package com.tui.qa.runner;
import org.testng.annotations.Listeners;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@Listeners(com.tui.qa.listeners.RetryListener.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.tui.qa",
        plugin = {
                "pretty",
                "summary",
                "html:reports/cucumber/cucumber-report.html",
            "json:reports/cucumber/cucumber.json",
            "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true,
        publish = false,
        tags = "not @ignore"
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}