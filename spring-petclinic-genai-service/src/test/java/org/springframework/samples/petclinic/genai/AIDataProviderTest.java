package org.springframework.samples.petclinic.genai;

import java.net.URI;
import java.util.Date;
import java.util.List;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.samples.petclinic.genai.dto.OwnerDetails;
import org.springframework.samples.petclinic.genai.dto.PetDetails;
import org.springframework.samples.petclinic.genai.dto.PetRequest;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AIDataProviderTest {

    private MockWebServer server;

    private VectorStore vectorStore;

    private AIDataProvider provider;

    @BeforeEach
    void setUp() {
        server = new MockWebServer();
        vectorStore = mock(VectorStore.class);

        DiscoveryClient discoveryClient = mock(DiscoveryClient.class);
        URI uri = URI.create("http://" + server.getHostName() + ":" + server.getPort());
        given(discoveryClient.getInstances("customers-service")).willReturn(
            List.of(new DefaultServiceInstance("customers-1", "customers-service",
                uri.getHost(), uri.getPort(), false)));

        provider = new AIDataProvider(vectorStore, discoveryClient);
    }

    @AfterEach
    void shutdown() throws Exception {
        server.close();
    }

    @Test
    void shouldFetchAllOwners() {
        enqueueJson("""
            [{"id":1,"firstName":"George","lastName":"Franklin","address":"110 W. Liberty St.",
              "city":"Madison","telephone":"6085551023","pets":[]}]
            """);

        List<OwnerDetails> owners = provider.getAllOwners();

        assertThat(owners).hasSize(1);
        assertThat(owners.get(0).lastName()).isEqualTo("Franklin");
    }

    @Test
    void shouldPropagateErrorWhenCustomersServiceFails() {
        server.enqueue(new MockResponse.Builder().code(500).build());

        assertThatThrownBy(() -> provider.getAllOwners())
            .isInstanceOf(HttpServerErrorException.class);
    }

    @Test
    void shouldAddPetToOwner() {
        enqueueJson("""
            {"id":20,"name":"Leo","birthDate":"2010-09-07","type":{"name":"cat"},"visits":[]}
            """);

        PetDetails pet = provider.addPetToOwner(1, new PetRequest(0, new Date(0), "Leo", 1));

        assertThat(pet.name()).isEqualTo("Leo");
    }

    @Test
    void shouldReportNotFoundWhenAddingPetToUnknownOwner() {
        server.enqueue(new MockResponse.Builder().code(404).build());

        assertThatThrownBy(() -> provider.addPetToOwner(999, new PetRequest(0, new Date(0), "Leo", 1)))
            .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void shouldAddOwner() {
        enqueueJson("""
            {"id":2,"firstName":"Ada","lastName":"Lovelace","address":"1 Analytical Way",
             "city":"London","telephone":"0123456789","pets":[]}
            """);

        OwnerDetails owner = provider.addOwnerToPetclinic(
            new OwnerRequest("Ada", "Lovelace", "1 Analytical Way", "London", "0123456789"));

        assertThat(owner.firstName()).isEqualTo("Ada");
    }

    @Test
    void shouldRejectInvalidOwner() {
        server.enqueue(new MockResponse.Builder().code(400).build());

        assertThatThrownBy(() -> provider.addOwnerToPetclinic(
            new OwnerRequest("", "", "", "", "")))
            .isInstanceOf(HttpClientErrorException.BadRequest.class);
    }

    @Test
    void shouldSearchVetsInVectorStore() throws Exception {
        given(vectorStore.similaritySearch(any(SearchRequest.class)))
            .willReturn(List.of(Document.builder().text("James Carter").build()));

        List<String> vets = provider.getVets(new Vet(1, "James", "Carter", null));

        assertThat(vets).hasSize(1);
        assertThat(vets.get(0)).contains("James Carter");
    }

    @Test
    void shouldReturnEmptyResultWhenNoVetMatches() throws Exception {
        given(vectorStore.similaritySearch(any(SearchRequest.class))).willReturn(List.of());

        assertThat(provider.getVets(null)).isEmpty();
    }

    private void enqueueJson(String body) {
        server.enqueue(new MockResponse.Builder()
            .addHeader("Content-Type", "application/json")
            .body(body)
            .build());
    }
}
