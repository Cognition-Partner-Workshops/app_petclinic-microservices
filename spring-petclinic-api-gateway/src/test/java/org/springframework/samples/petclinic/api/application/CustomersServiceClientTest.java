package org.springframework.samples.petclinic.api.application;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.api.dto.OwnerDetails;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomersServiceClientTest {

    @SuppressWarnings("unchecked")
    @Test
    void getOwnerCallsCustomersService() {
        WebClient webClient = mock(WebClient.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        OwnerDetails owner = new OwnerDetails(1, "George", "Franklin",
            "addr", "city", "phone", List.of());

        when(builder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString(), any(Object.class))).thenReturn((WebClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OwnerDetails.class)).thenReturn(Mono.just(owner));

        CustomersServiceClient client = new CustomersServiceClient(builder);
        Mono<OwnerDetails> result = client.getOwner(1);

        StepVerifier.create(result)
            .expectNextMatches(o -> o.firstName().equals("George") && o.id() == 1)
            .verifyComplete();
    }
}
