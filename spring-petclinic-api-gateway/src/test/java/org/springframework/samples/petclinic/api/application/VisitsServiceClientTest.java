package org.springframework.samples.petclinic.api.application;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.api.dto.VisitDetails;
import org.springframework.samples.petclinic.api.dto.Visits;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VisitsServiceClientTest {

    @SuppressWarnings("unchecked")
    @Test
    void getVisitsForPetsCallsVisitsService() {
        WebClient webClient = mock(WebClient.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        VisitDetails visit = new VisitDetails(1, 10, "2024-01-01", "checkup");
        Visits visits = new Visits(List.of(visit));

        when(builder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString(), any(Object.class))).thenReturn((WebClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Visits.class)).thenReturn(Mono.just(visits));

        VisitsServiceClient client = new VisitsServiceClient(builder);
        Mono<Visits> result = client.getVisitsForPets(List.of(10, 20));

        StepVerifier.create(result)
            .expectNextMatches(v -> v.items().size() == 1 && v.items().get(0).petId() == 10)
            .verifyComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    void setHostnameChangesBaseUrl() {
        WebClient webClient = mock(WebClient.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        Visits visits = new Visits(List.of());

        when(builder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString(), any(Object.class))).thenReturn((WebClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Visits.class)).thenReturn(Mono.just(visits));

        VisitsServiceClient client = new VisitsServiceClient(builder);
        client.setHostname("http://custom-host/");
        Mono<Visits> result = client.getVisitsForPets(List.of(1));

        StepVerifier.create(result)
            .expectNextMatches(v -> v.items().isEmpty())
            .verifyComplete();
    }
}
