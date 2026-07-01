# TUI Mobile Automation

Android UI automation framework using Appium + Java + TestNG + Cucumber + Allure.

Goal: let a new engineer clone and run tests in 10-15 minutes (assuming Android SDK/emulator are already installed).

## Tech Stack

- Java 17
- Maven
- Appium Server 2.x
- Android Emulator / Android Device
- Cucumber + TestNG
- Allure Reports

## Project Structure

- src/main/java: framework core (driver, pages, utils, config)
- src/test/java: hooks, runners, step definitions, tests
- src/test/resources/features: Cucumber feature files
- src/test/resources/testdata: test data JSON
- apps/app.apk: AUT APK

## Prerequisites

Install and verify the following:

1. Java 17
2. Maven 3.8+
3. Node.js 18+ (for Appium)
4. Appium 2.x
5. Android Studio + Android SDK + at least one emulator image
6. adb available in PATH

Quick verification commands:

```bash
java -version
mvn -version
node -v
appium -v
adb version
adb devices
```

## One-Time Setup (New Machine)

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd TUI-Mobile-Automation
```

### 2. Install Appium Driver

```bash
appium driver install uiautomator2
appium driver list --installed
```

### 3. Start an Android Emulator

If emulator is already created, start it from Android Studio Device Manager.

Or via CLI:

```bash
emulator -list-avds
emulator -avd <your_avd_name>
```

Wait until device is visible:

```bash
adb devices
```

### 4. Confirm Runtime Config

Update values in src/main/resources/config.properties if your machine/device differs:

- deviceName
- udid
- platformVersion
- app (absolute path to APK)
- appiumServerURL

Current app path is absolute and machine-specific by default. Update app to your local cloned path if needed.

## Daily Run (10-15 Minutes)

### 1. Start Appium Server

Run in a separate terminal:

```bash
appium --port 4723
```

### 2. Ensure Emulator/Device is Online

```bash
adb devices
```

### 3. Run Full Test Suite

```bash
mvn clean test
```

Optional runtime overrides (no file edits required):

```bash
mvn clean test \
	-DdeviceName=emulator-5554 \
	-Dudid=emulator-5554 \
	-DplatformVersion=14
```

The same keys can also be provided through environment variables:

```bash
export DEVICE_NAME=emulator-5554
export UDID=emulator-5554
export PLATFORM_VERSION=14
mvn clean test
```

### 4. Generate Allure HTML Report

```bash
mvn allure:report
```

Open report:

- target/site/allure-maven-plugin/index.html

## Fast Local Runs

Run only one scenario by feature line:

```bash
mvn -Dtest=TestRunner -Dcucumber.features=src/test/resources/features/search.feature:14 test
```

Run one feature file:

```bash
mvn -Dtest=TestRunner -Dcucumber.features=src/test/resources/features/login.feature test
```

## Reports and Artifacts

- Cucumber HTML: reports/cucumber/cucumber-report.html
- Cucumber JSON: reports/cucumber/cucumber.json
- Allure raw results: target/allure-results
- Allure HTML: target/site/allure-maven-plugin
- Surefire reports: target/surefire-reports
- Failure screenshots: screenshots

To share Allure output, share the full folder below (not only index.html):

- target/site/allure-maven-plugin

## Common Troubleshooting

### Appium session not created / endpoint issues

- Keep Appium running on port 4723.
- Verify config.properties appiumServerURL.
- Check adb devices returns your emulator/device.

### APK not found

- Verify apps/app.apk exists.
- Update config.properties app with correct absolute path.

### Emulator found but tests flaky on date picker

- Keep emulator unlocked and stable.
- Re-run scenario once:

```bash
mvn -Dtest=TestRunner -Dcucumber.features=src/test/resources/features/search.feature:14 test
```

### Build/report command fails

- Clean and retry:

```bash
mvn clean test
mvn allure:report
```

## Recommended Execution Flow

1. Start emulator.
2. Start Appium server.
3. Run mvn clean test.
4. Run mvn allure:report.
5. Share target/site/allure-maven-plugin.
