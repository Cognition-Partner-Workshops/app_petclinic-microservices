# Cucumber BDD Test Suite — PetClinic Customers Service

This directory contains a **Cucumber BDD test suite** that validates the Customers Service REST API using business-readable Gherkin scenarios.

## Directory Layout

```
src/test/
├── java/.../customers/
│   ├── cucumber/
│   │   ├── CucumberRunnerTest.java        # JUnit Platform Suite runner
│   │   ├── CucumberSpringConfiguration.java  # Spring Boot + Cucumber bridge
│   │   ├── OwnerStepDefinitions.java      # Step defs for owner scenarios
│   │   ├── PetStepDefinitions.java        # Step defs for pet scenarios
│   │   └── ReportGeneratorHook.java       # Generates rich HTML report after run
│   └── web/
│       └── PetResourceTest.java           # Existing unit test
├── resources/
│   ├── features/
│   │   ├── owner_management.feature       # Owner CRUD scenarios
│   │   └── pet_management.feature         # Pet management scenarios
│   ├── application-test.yml               # Test profile configuration
│   └── cucumber.properties                # Cucumber configuration
└── README.md                              # This file
```

## How to Run the Tests

### Run Unit Tests Only (Surefire)

```bash
./mvnw test -pl spring-petclinic-customers-service
```

This runs standard JUnit tests but **excludes** Cucumber scenarios.

### Run Cucumber Integration Tests Only (Failsafe)

```bash
./mvnw verify -pl spring-petclinic-customers-service
```

This first runs unit tests via Surefire, then runs Cucumber scenarios via Failsafe during the `integration-test` phase.

### Run Everything from the Project Root

```bash
./mvnw verify
```

## Report Outputs

After a `mvn verify` run, reports are generated in:

```
spring-petclinic-customers-service/target/cucumber-reports/
├── cucumber-html-report.html          # Cucumber built-in HTML report
├── cucumber-report.json               # JSON report (CI-consumable)
├── cucumber-junit-report.xml          # JUnit XML report (CI-consumable)
└── advanced-reports/                  # Rich visual report (cucumber-reporting library)
    └── overview-features.html         # Start here for the best visual experience
```

### Report Descriptions

| Report | Format | Best For |
|--------|--------|----------|
| `cucumber-html-report.html` | HTML | Quick overview of pass/fail per scenario |
| `cucumber-report.json` | JSON | CI tool ingestion (Jenkins, GitHub Actions, etc.) |
| `cucumber-junit-report.xml` | JUnit XML | CI dashboards that consume JUnit XML |
| `advanced-reports/` | Rich HTML | **Business stakeholder presentations** — includes charts, timelines, tag-based filtering |

### Interpreting the Reports

1. **Open `advanced-reports/overview-features.html`** in a browser for the richest experience.
2. The **Features Overview** page shows pass/fail status for each `.feature` file.
3. Click into a feature to see individual **Scenario** results with step-by-step details.
4. The **Tags Overview** page lets you filter by tags (`@owners`, `@pets`).
5. **Green** = passed, **Red** = failed, **Yellow** = skipped/pending.

## Configuration Details

### Maven Plugins

- **Surefire** — runs standard unit tests; Cucumber runner is **excluded** to avoid double execution.
- **Failsafe** — runs `CucumberRunnerTest.java` during `integration-test` phase; generates reports in `target/cucumber-reports/`.

### Cucumber Options (in `CucumberRunnerTest.java`)

| Option | Value |
|--------|-------|
| Features | `classpath:features` |
| Glue | `org.springframework.samples.petclinic.customers.cucumber` |
| Plugins | `pretty`, `html`, `json`, `junit` |

### Spring Integration

The `CucumberSpringConfiguration` class uses `@CucumberContextConfiguration` with `@SpringBootTest` and `@AutoConfigureMockMvc` to launch the full application context with:

- **HSQLDB** in-memory database (via `application-test.yml`)
- **MockMvc** for HTTP request testing without a running server
- **Eureka & Config Server disabled** for isolated testing

## Writing New Scenarios

1. Create a `.feature` file under `src/test/resources/features/`.
2. Write scenarios using Gherkin syntax (`Given` / `When` / `Then`).
3. Add step definitions in a Java class under the `cucumber` package.
4. Run `./mvnw verify -pl spring-petclinic-customers-service` to execute.

### Example

```gherkin
@owners
Feature: Owner Search
  Scenario: Find owner by last name
    Given the PetClinic system is running
    And the following owner exists:
      | firstName | lastName | address     | city    | telephone  |
      | Jean      | Coleman  | 105 N. Lake | Monona  | 6085552654 |
    When I search for owners with last name "Coleman"
    Then the response status should be 200
```
