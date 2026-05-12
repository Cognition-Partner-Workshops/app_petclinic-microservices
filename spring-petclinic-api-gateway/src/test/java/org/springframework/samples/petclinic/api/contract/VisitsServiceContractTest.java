package org.springframework.samples.petclinic.api.contract;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Consumer-side contract test verifying the API Gateway can consume
 * the Visits Service contract via auto-generated WireMock stubs.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = ContractTestConfig.class
)
@AutoConfigureStubRunner(
    ids = "org.springframework.samples.petclinic.visits:spring-petclinic-visits-service:+:stubs",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("contracttest")
class VisitsServiceContractTest {

    @Autowired
    private StubFinder stubFinder;

    @Test
    void shouldRetrieveVisitsForPets() {
        int port = stubFinder.findStubUrl("spring-petclinic-visits-service").getPort();

        WebClient webClient = WebClient.builder().build();

        String response = webClient.get()
            .uri("http://localhost:" + port + "/pets/visits?petId=1,2")
            .header("Accept", "application/json")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).isNotNull();
        assertThat(response).contains("rabies shot");
        assertThat(response).contains("neutered");
    }
}
