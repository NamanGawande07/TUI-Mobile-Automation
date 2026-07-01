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

### AI-assisted areas

- Framework scaffolding and code acceleration:
	- Generating page object methods, utility classes, and initial test hooks.
	- Producing first-pass boilerplate for runners, steps, and helper methods.
- Test robustness improvements:
	- Suggesting wait strategies, retry boundaries, and synchronization patterns.
	- Proposing locator fallback strategies for dynamic mobile UIs.
- Reporting and observability setup:
	- Drafting report integration patterns, artifact paths, and execution commands.
	- Helping structure failure evidence capture (logs/screenshots/report attachments).
- Documentation and developer experience:
	- Producing quick-start setup steps, troubleshooting guides, and execution playbooks.
	- Assisting with decision logs and implementation rationale documentation.

### Where we corrected AI output

- Environment-specific mismatches:
	- AI-generated defaults may not match local/CI tool versions, plugin compatibility, or device capabilities.
	- We validate dependencies and execution commands against the actual runtime environment.
- Flaky-interaction risk in first-pass automation:
	- AI often proposes a single interaction path; mobile apps frequently need fallback selectors and state-aware flows.
	- We refine generated logic to handle timing variation, UI transitions, and alternate element representations.
- Over-generalized locator and wait patterns:
	- Generated selectors may be syntactically valid but not stable enough in real devices.
	- We harden locators and waits based on repeated execution evidence.
- Reporting and pipeline assumptions:
	- AI-generated reporting setup can assume artifact versions/paths that differ by organization.
	- We align report generation with repository standards and CI constraints.

Decision: AI is used as an accelerator, not as final authority. Any non-trivial generated output must be validated through local reruns, evidence review, and environment-specific hardening before acceptance.

## 3. What We Would Add With More Time (Description Only)

- Cross-platform abstraction layer to support Android and iOS page locators from one page contract.
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
