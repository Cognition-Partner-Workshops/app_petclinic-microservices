package org.springframework.samples.petclinic.api.application;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.api.dto.OwnerDetails;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class CustomersServiceClientTest {

    private final AtomicReference<URI> requestedUri = new AtomicReference<>();

    @Test
    void shouldReturnOwnerWithPets() {
        CustomersServiceClient client = clientRespondingWith(HttpStatus.OK, """
            {"id":1,"firstName":"George","lastName":"Franklin","address":"110 W. Liberty St.",
             "city":"Madison","telephone":"6085551023",
             "pets":[{"id":20,"name":"Leo","birthDate":"2010-09-07","type":{"name":"cat"}}]}
            """);

        OwnerDetails owner = client.getOwner(1).block();

        assertThat(owner).isNotNull();
        assertThat(owner.firstName()).isEqualTo("George");
        assertThat(owner.getPetIds()).containsExactly(20);
        assertThat(owner.pets().get(0).visits()).isEmpty();
        assertThat(requestedUri.get()).hasToString("http://customers-service/owners/1");
    }

    @Test
    void shouldPropagateNotFoundFromCustomersService() {
        CustomersServiceClient client = clientRespondingWith(HttpStatus.NOT_FOUND, "");

        StepVerifier.create(client.getOwner(999))
            .expectErrorSatisfies(error -> assertThat(error)
                .isInstanceOf(WebClientResponseException.NotFound.class))
            .verify();
    }

    @Test
    void shouldPropagateServerErrorFromCustomersService() {
        CustomersServiceClient client = clientRespondingWith(HttpStatus.INTERNAL_SERVER_ERROR, "");

        StepVerifier.create(client.getOwner(1))
            .expectError(WebClientResponseException.InternalServerError.class)
            .verify();
    }

    private CustomersServiceClient clientRespondingWith(HttpStatus status, String body) {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(request -> {
            requestedUri.set(request.url());
            return Mono.just(ClientResponse.create(status)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build());
        });
        return new CustomersServiceClient(builder);
    }
}
