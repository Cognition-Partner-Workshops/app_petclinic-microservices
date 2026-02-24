# Cucumber BDD Test Framework for PetClinic Microservices

This document describes the reusable Cucumber BDD test framework and how to replicate the pattern for any additional microservice.

## Architecture Overview

```
spring-petclinic-microservices/
  spring-petclinic-test-common/           # Shared test library (not a Spring Boot app)
    src/main/java/.../test/
      steps/
        HttpStepDefinitions.java          # Reusable HTTP GET/POST/PUT/DELETE steps
        JsonValidationStepDefinitions.java # JSONPath-based response assertions
        DatabaseStepDefinitions.java      # SQL execute, row count verification
        ScenarioContext.java              # Shared state between steps
        CucumberHooks.java               # Before/After hooks (context reset)
      data/
        TestDataLoader.java              # CSV/YAML file loader for parameterized data
        TestDataStepDefinitions.java     # Steps for loading test data files

  spring-petclinic-customers-service/     # Example service with Cucumber tests
    src/test/
      java/.../cucumber/
        CucumberSpringConfiguration.java  # @CucumberContextConfiguration + @SpringBootTest
        RunCucumberTest.java             # JUnit Platform Suite entry point
        CustomerStepDefinitions.java     # Service-specific steps
      resources/
        features/                        # .feature files (Gherkin)
          owners.feature
          pets.feature
        testdata/                        # CSV/YAML test data
          owners.csv
```

## Running Tests

### Run all tests for all services (default build)
```bash
./mvnw test
```

### Run tests for a specific service using Maven profiles
```bash
# Customers service only
./mvnw test -Ptest-customers

# Vets service only
./mvnw test -Ptest-vets

# Visits service only
./mvnw test -Ptest-visits

# Data validation service only
./mvnw test -Ptest-data-validation

# All four testable services
./mvnw test -Ptest-all
```

### Run a specific feature file
```bash
cd spring-petclinic-customers-service
../mvnw test -Dcucumber.features=src/test/resources/features/owners.feature
```

## Shared Step Definitions (test-common)

### HTTP Steps
| Step | Description |
|------|-------------|
| `Given the base URL is "{url}"` | Sets the base URL for subsequent requests |
| `When I send a GET request to "{path}"` | Sends an HTTP GET |
| `When I send a POST request to "{path}" with body:` | Sends an HTTP POST with JSON body |
| `When I send a PUT request to "{path}" with body:` | Sends an HTTP PUT with JSON body |
| `When I send a DELETE request to "{path}"` | Sends an HTTP DELETE |
| `Then the response status should be {int}` | Asserts HTTP status code |
| `Then the response content type should be "{type}"` | Asserts content type |

### JSON Validation Steps
| Step | Description |
|------|-------------|
| `Then the JSON response should have field "{jsonPath}"` | Asserts field exists |
| `Then the JSON field "{jsonPath}" should be "{value}"` | Asserts string value |
| `Then the JSON field "{jsonPath}" should be the integer {int}` | Asserts integer value |
| `Then the JSON field "{jsonPath}" should be true/false` | Asserts boolean |
| `Then the JSON field "{jsonPath}" should be null` | Asserts null |
| `Then the JSON field "{jsonPath}" should not be null` | Asserts non-null |
| `Then the JSON array "{jsonPath}" should have size {int}` | Asserts array size |
| `Then the JSON array "{jsonPath}" should not be empty` | Asserts non-empty array |
| `Then the JSON response should contain "{substring}"` | Substring match |

### Database Steps
| Step | Description |
|------|-------------|
| `Given the database table "{name}" has been cleared` | Deletes all rows |
| `Given I execute SQL "{sql}"` | Runs a single SQL statement |
| `Given I execute SQL:` (docstring) | Runs multiple semicolon-separated SQL statements |
| `Then the database table "{name}" should have {int} rows` | Asserts exact row count |
| `Then the database table "{name}" should have at least {int} rows` | Asserts minimum row count |

### Test Data Steps
| Step | Description |
|------|-------------|
| `Given test data is loaded from CSV "{path}"` | Loads CSV from classpath |
| `Given test data is loaded from YAML "{path}"` | Loads YAML from classpath |

## Parameterized Test Data Framework

### CSV Format
Place CSV files under `src/test/resources/testdata/`. The first row contains headers:

```csv
firstName,lastName,address,city,telephone
Alice,Wonderland,42 Rabbit Hole Ln,Wonderville,5551112222
Bob,Builder,100 Construction Ave,Buildtown,5553334444
```

### YAML Format
Place YAML files under `src/test/resources/testdata/`. Top-level must be a list:

```yaml
- description: "Test scenario 1"
  sourceQuery: "SELECT id FROM table1"
  destinationQuery: "SELECT id FROM table2"
  compareFields:
    - id
    - name
```

### Using TestDataLoader in Step Definitions
```java
List<Map<String, String>> rows = TestDataLoader.loadCsv("testdata/owners.csv");
for (Map<String, String> row : rows) {
    String firstName = TestDataLoader.requireString(row, "firstName");
    // ... use data in test
}
```

## Adding Tests for a New Microservice

Follow these steps to add Cucumber tests for any new microservice:

### Step 1: Add Dependencies to the Service POM

Add these to the service's `pom.xml` under `<dependencies>`:

```xml
<!-- Cucumber BDD Testing -->
<dependency>
    <groupId>org.springframework.samples.petclinic.test</groupId>
    <artifactId>spring-petclinic-test-common</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <scope>test</scope>
</dependency>
```

### Step 2: Create the Spring Configuration Class

Create `src/test/java/.../<service>/cucumber/CucumberSpringConfiguration.java`:

```java
package org.springframework.samples.petclinic.<service>.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}
```

### Step 3: Create the Test Runner

Create `src/test/java/.../<service>/cucumber/RunCucumberTest.java`:

```java
package org.springframework.samples.petclinic.<service>.cucumber;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "org.springframework.samples.petclinic.test.steps,"
        + "org.springframework.samples.petclinic.test.data,"
        + "org.springframework.samples.petclinic.<service>.cucumber")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
public class RunCucumberTest {
}
```

### Step 4: Create Service-Specific Step Definitions

Create `src/test/java/.../<service>/cucumber/<Service>StepDefinitions.java` with steps specific to the service's API.

### Step 5: Write Feature Files

Create `.feature` files under `src/test/resources/features/`. Use the shared steps from `test-common` and your service-specific steps.

### Step 6: Add Test Data Files

Place CSV or YAML files under `src/test/resources/testdata/` for data-driven testing.

### Step 7: Add Maven Profile (Optional)

In the parent `pom.xml`, add a profile to run just this service's tests:

```xml
<profile>
    <id>test-<service-name></id>
    <modules>
        <module>spring-petclinic-test-common</module>
        <module>spring-petclinic-<service-name></module>
    </modules>
</profile>
```

## Cucumber Reports

After running tests, HTML and JSON reports are generated at:
- `target/cucumber-reports/cucumber.html` (human-readable)
- `target/cucumber-reports/cucumber.json` (machine-readable, for CI dashboards)
