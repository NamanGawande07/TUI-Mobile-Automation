# Decisions

## 1. Why We Chose This Project Structure

We selected a layered, automation-focused structure to keep test intent readable and framework code maintainable.

- `src/main/java` contains reusable framework code (driver setup, page objects, utilities, config readers).
- `src/test/java` contains test-facing logic (Cucumber step definitions, hooks, runners, test classes).
- `src/test/resources/features` keeps Gherkin scenarios close to business behavior.
- `src/test/resources/testdata` externalizes test data from code to reduce hardcoding.
- `apps` stores the APK artifact in a predictable location for local and CI execution.
- `reports`, `screenshots`, and `target` separate execution artifacts from source code.

This structure follows Page Object Model + BDD separation of concerns. It allows framework updates (driver, waits, locators) without rewriting scenarios and enables non-developer stakeholders to review behavior in feature files.

## 2. Where AI Tools Were Used and Where We Corrected Output

Concrete AI usage and corrections in this repository:

- AI-generated first pass for DriverInitializer and BasePage patterns.
  Correction applied: switched from single-path endpoint assumptions to runtime Appium base-path detection (root vs /wd/hub).
- AI-generated wait and locator proposals for login/search pages.
  Correction applied: added fallback selectors/content-desc scanning and guarded retries for unstable calendar interactions.
- AI-generated CI workflow scaffold.
  Correction applied: workflow initially stopped after Appium/APK checks; updated to run mvn clean test and mvn allure:report and then upload artifacts.
- AI-generated retry-listener pattern.
  Correction applied: reduced scope so retry assignment applies to Cucumber runner flow, avoiding unintended blanket behavior for unrelated TestNG classes.
- AI-generated documentation draft.
  Correction applied: replaced generic statements with specific implementation details (dataset-driven login, testng.xml suite control, iOS initializer stub status).

Decision: AI is a productivity accelerator, but every generated change is reviewed with runnable validation and evidence before acceptance.

## 3. What We Would Add With More Time (Description Only)

- Complete iOS implementation behind the added IOSDriverInitializer stub and route page-level locators through a shared platform contract.
- Stronger flakiness controls:
	- deterministic wait utilities,
	- calendar helper component,
	- retry only for known transient failures.
- Test tagging strategy expansion (`@smoke`, `@regression`, `@critical`, `@ios-only`, `@android-only`) with selective pipelines.
- Better observability:
	- Appium server logs attached per test,
	- device logs (`logcat`) attached in Allure,
	- richer step-level screenshots.
- Environment management:
	- profile-based config (`local`, `ci`, `staging`),
	- secrets via CI vault,
	- device farm integration.
- Quality gates:
	- static checks + test retries policy + trend-based failure threshold.

## 4. How We Will Run This in CI With Parallel Execution Across iOS and Android

Proposed CI model:

1. Use a matrix strategy with `platform = [android, ios]`.
2. For each platform, fan out test shards in parallel by tags or feature files.
3. Run independent jobs per shard with isolated device/simulator allocation.
4. Publish per-shard artifacts (`allure-results`, screenshots, logs).
5. Merge all `allure-results` into one consolidated report job.

Execution design:

- Android jobs:
	- Start emulator/device session per shard.
	- Start Appium service bound to job-local port.
	- Execute shard command (example: feature subset or tag subset).
- iOS jobs:
	- Start iOS simulator per shard.
	- Run Appium with XCUITest driver.
	- Execute same scenario contracts mapped through iOS page layer.

Parallelization options:

- Feature-based sharding (simple and deterministic).
- Tag-based sharding (`@smoke`, `@regression-a`, `@regression-b`).
- Duration-balanced shard splitting using historical timing data.

Report strategy:

- Store one `allure-results` folder per shard.
- Merge in a downstream report job.
- Publish final Allure HTML as pipeline artifact and optional hosted page.

## 5. Why We Selected the Current Implemented Scenarios

We prioritized scenarios that provide high confidence in the core user journey and expose the most automation risk areas early.

- `Login with valid user details`:
	- Validates app launch, credential input, DOB selection, and transition to results.
	- Serves as prerequisite flow reused by search scenarios.
- Search tabs (`Hotels`, `Holidays`, `All`):
	- Verify primary business navigation and result rendering states.
	- Cover category-specific behavior and combined-result behavior.
- Lazy loading scenario:
	- Validates dynamic content loading on scroll.
	- Covers a common mobile instability point (list pagination/render timing).

Why these before other candidates:

- They maximize business coverage with a small suite.
- They exercise the most failure-prone technical areas (session setup, locator stability, asynchronous UI loading, calendar interaction).
- They are strong smoke/regression foundations that can be expanded later to edge cases (invalid login, no results, network interruption, locale/date format variance).
