# Cross-service integration tests

RestAssured tests that exercise the customers-service, the visits-service and the API gateway
together, running as containers through Docker Compose.

| Test | What it covers |
| --- | --- |
| `CrossServiceWorkflowIT` | Registering an owner, adding a pet, scheduling a visit, gateway aggregation of `/api/gateway/owners/{id}` and gateway routing of `/api/customer/**` and `/api/visit/**` |
| `VisitsServiceOutageIT` | Gateway behaviour while the visits-service is down: `getOwnerDetails` circuit breaker fallback (owner and pets without visits), failing `/api/visit/**` route, unaffected `/api/customer/**` route and recovery after restart |

## Running

Build the service images once (they are the images referenced by the compose file):

```bash
./mvnw clean install -P buildDocker -DskipTests
```

Then run the suite:

```bash
./mvnw verify -P integration-tests -pl spring-petclinic-integration-tests
```

The tests are skipped by a normal `./mvnw verify` because they need a Docker environment.
Compose brings up config-server, discovery-server, customers-service, visits-service and
api-gateway from [docker-compose-integration-tests.yml](docker-compose-integration-tests.yml),
waits for their health checks and is torn down afterwards.

## Options

| System property | Default | Description |
| --- | --- | --- |
| `petclinic.compose.enabled` | `true` | Set to `false` to test an environment that is already running |
| `petclinic.compose.project` | `petclinic-it` | Compose project name |
| `petclinic.compose.keepRunning` | `false` | Keep the containers after the tests, useful when debugging |
| `petclinic.gateway.url` | `http://localhost:8080` | API gateway base URL |
| `petclinic.customers.url` | `http://localhost:8081` | customers-service base URL |
| `petclinic.visits.url` | `http://localhost:8082` | visits-service base URL |

Host ports can be changed with the `PETCLINIC_IT_GATEWAY_PORT`, `PETCLINIC_IT_CUSTOMERS_PORT`,
`PETCLINIC_IT_VISITS_PORT`, `PETCLINIC_IT_DISCOVERY_PORT` and `PETCLINIC_IT_CONFIG_PORT`
environment variables; the matching `petclinic.*.url` properties have to be set as well.
