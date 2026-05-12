# Spring Cloud Contract Tests

This document describes the contract tests that verify the service boundaries between the API Gateway (consumer) and the Customers Service / Visits Service (providers).

## Service Boundaries Tested

| Consumer (Gateway)             | Provider              | Endpoint                         |
|-------------------------------|-----------------------|----------------------------------|
| `CustomersServiceClient`       | Customers Service     | `GET /owners/{ownerId}`          |
| `VisitsServiceClient`          | Visits Service        | `GET /pets/visits?petId=1,2`     |

## Project Structure

```
spring-petclinic-customers-service/
  src/test/resources/contracts/owner/
    shouldReturnOwnerById.groovy          # Contract definition (Groovy DSL)
  src/test/java/.../ContractVerifierBaseTest.java  # Provider verification test

spring-petclinic-visits-service/
  src/test/resources/contracts/visits/
    shouldReturnVisitsForPets.groovy      # Contract definition (Groovy DSL)
  src/test/java/.../ContractVerifierBaseTest.java  # Provider verification test

spring-petclinic-api-gateway/
  src/test/java/.../contract/
    ContractTestConfig.java               # Minimal Spring config for consumer tests
    CustomersServiceContractTest.java     # Consumer test (uses WireMock stubs)
    VisitsServiceContractTest.java        # Consumer test (uses WireMock stubs)
  src/test/resources/
    application-contracttest.yml          # Profile for consumer contract tests
```

## How to Run

### 1. Build and install provider stubs to local Maven repository

```bash
./mvnw clean install -pl spring-petclinic-customers-service,spring-petclinic-visits-service -DskipTests
```

This compiles the Groovy contract definitions into WireMock stub JARs and installs them in `~/.m2/repository`.

### 2. Run provider-side contract verification tests

```bash
./mvnw test -pl spring-petclinic-customers-service,spring-petclinic-visits-service
```

These tests use `@WebMvcTest` with MockMvc to verify that each provider service responds according to its contract definition.

### 3. Run consumer-side contract tests (API Gateway)

```bash
./mvnw test -pl spring-petclinic-api-gateway -Dtest="*Contract*"
```

These tests use `@AutoConfigureStubRunner` to start WireMock servers loaded with the provider stubs, then verify the API Gateway's WebClient calls return the expected data.

### Run everything in one command

```bash
./mvnw clean install -pl spring-petclinic-customers-service,spring-petclinic-visits-service -DskipTests && \
./mvnw test -pl spring-petclinic-customers-service,spring-petclinic-visits-service,spring-petclinic-api-gateway
```

## How It Works

1. **Contract Definitions** (Groovy DSL in `src/test/resources/contracts/`): Define the expected request/response for each service boundary.

2. **Provider Side**: The `spring-cloud-contract-maven-plugin` converts contracts into WireMock stub mappings and packages them as `-stubs.jar` artifacts. Manual `@WebMvcTest`-based verification tests ensure the provider endpoints match the contracts.

3. **Consumer Side**: `@AutoConfigureStubRunner` loads the stubs from the local Maven repository into embedded WireMock servers. Consumer tests then make real HTTP calls against these stubs using `WebClient`, exactly as the API Gateway does in production.

## Notes

- The provider stubs must be installed to the local Maven repo (`mvn install`) before running consumer tests.
- Provider tests use `@ActiveProfiles("test")` which disables Config Server and Eureka.
- Consumer tests use `@ActiveProfiles("contracttest")` with a minimal Spring context that excludes Gateway auto-configuration.
- The contract plugin's auto-generated test classes are disabled (`<phase>none</phase>`) due to RestAssured incompatibility with Spring Boot 4.x / Spring Framework 7.x. Provider verification is done via hand-written MockMvc tests instead.
