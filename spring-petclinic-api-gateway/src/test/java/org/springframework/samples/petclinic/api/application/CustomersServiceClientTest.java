package org.springframework.samples.petclinic.api.application;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.api.dto.OwnerDetails;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomersServiceClientTest {

    @SuppressWarnings("unchecked")
    @Test
    void getOwnerReturnsOwnerMono() {
        WebClient webClient = mock(WebClient.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(builder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn((WebClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(1).firstName("John").lastName("Doe").build();
        when(responseSpec.bodyToMono(OwnerDetails.class)).thenReturn(Mono.just(owner));

        CustomersServiceClient client = new CustomersServiceClient(builder);
        Mono<OwnerDetails> result = client.getOwner(1);

        StepVerifier.create(result)
            .assertNext(o -> {
                assertThat(o.id()).isEqualTo(1);
                assertThat(o.firstName()).isEqualTo("John");
            })
            .verifyComplete();
    }
}
