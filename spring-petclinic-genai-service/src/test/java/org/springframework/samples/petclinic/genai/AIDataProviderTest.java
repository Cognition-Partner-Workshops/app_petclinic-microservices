package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.samples.petclinic.genai.dto.*;
import org.springframework.web.client.RestClient;
import tools.jackson.core.JacksonException;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIDataProviderTest {

    @Mock
    VectorStore vectorStore;

    @Mock
    DiscoveryClient discoveryClient;

    @Mock
    RestClient restClient;

    AIDataProvider dataProvider;

    @BeforeEach
    void setUp() throws Exception {
        dataProvider = new AIDataProvider(vectorStore, discoveryClient);
        Field field = AIDataProvider.class.getDeclaredField("restClient");
        field.setAccessible(true);
        field.set(dataProvider, restClient);
    }

    private void mockDiscoveryClient() {
        ServiceInstance instance = new DefaultServiceInstance("id", "customers-service", "localhost", 8080, false);
        when(discoveryClient.getInstances("customers-service")).thenReturn(List.of(instance));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllOwnersCallsRestClient() {
        mockDiscoveryClient();
        RestClient.RequestHeadersUriSpec<?> uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        OwnerDetails owner = new OwnerDetails(1, "George", "Franklin", "addr", "city", "phone", List.of());

        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString())).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of(owner));

        List<OwnerDetails> result = dataProvider.getAllOwners();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("George");
    }

    @SuppressWarnings("unchecked")
    @Test
    void addPetToOwnerCallsRestClient() {
        mockDiscoveryClient();
        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        PetRequest petRequest = new PetRequest(0, new Date(), "Buddy", 1);
        PetDetails petDetails = new PetDetails(1, "Buddy", "2020-01-01", new PetType("dog"), List.of());

        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(PetRequest.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(PetDetails.class)).thenReturn(petDetails);

        PetDetails result = dataProvider.addPetToOwner(1, petRequest);

        assertThat(result.name()).isEqualTo("Buddy");
    }

    @SuppressWarnings("unchecked")
    @Test
    void addOwnerToPetclinicCallsRestClient() {
        mockDiscoveryClient();
        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        OwnerRequest ownerRequest = new OwnerRequest("George", "Franklin", "addr", "city", "6085551023");
        OwnerDetails ownerDetails = new OwnerDetails(1, "George", "Franklin", "addr", "city", "6085551023", List.of());

        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(OwnerRequest.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(OwnerDetails.class)).thenReturn(ownerDetails);

        OwnerDetails result = dataProvider.addOwnerToPetclinic(ownerRequest);

        assertThat(result.firstName()).isEqualTo("George");
    }

    @Test
    void getVetsWithNonNullRequestUsesTopK20() throws JacksonException {
        Vet vetRequest = new Vet(1, "James", "Carter", Set.of());
        Document doc = new Document("vet data");
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));

        List<String> result = dataProvider.getVets(vetRequest);

        assertThat(result).hasSize(1);
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }

    @Test
    void getVetsWithNullRequestUsesTopK50() throws JacksonException {
        Document doc = new Document("vet data");
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));

        List<String> result = dataProvider.getVets(null);

        assertThat(result).hasSize(1);
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }
}
