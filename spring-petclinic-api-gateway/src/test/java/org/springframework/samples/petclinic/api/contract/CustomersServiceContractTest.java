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
 * the Customers Service contract via auto-generated WireMock stubs.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = ContractTestConfig.class
)
@AutoConfigureStubRunner(
    ids = "org.springframework.samples.petclinic.client:spring-petclinic-customers-service:+:stubs",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("contracttest")
class CustomersServiceContractTest {

    @Autowired
    private StubFinder stubFinder;

    @Test
    void shouldRetrieveOwnerById() {
        int port = stubFinder.findStubUrl("spring-petclinic-customers-service").getPort();

        WebClient webClient = WebClient.builder().build();

        String response = webClient.get()
            .uri("http://localhost:" + port + "/owners/1")
            .header("Accept", "application/json")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).isNotNull();
        assertThat(response).contains("George");
        assertThat(response).contains("Franklin");
        assertThat(response).contains("Leo");
        assertThat(response).contains("110 W. Liberty St.");
        assertThat(response).contains("Madison");
        assertThat(response).contains("6085551023");
    }
}
