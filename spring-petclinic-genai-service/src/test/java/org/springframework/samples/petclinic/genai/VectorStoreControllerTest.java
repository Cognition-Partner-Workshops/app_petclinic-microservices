package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.samples.petclinic.genai.dto.Specialty;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VectorStoreControllerTest {

    @Test
    void convertListToJsonResourceReturnsJsonResource() {
        VectorStore vectorStore = mock(VectorStore.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        when(builder.build()).thenReturn(webClient);

        VectorStoreController controller = new VectorStoreController(vectorStore, builder);

        Vet vet = new Vet(1, "James", "Carter", Set.of(new Specialty(1, "radiology")));
        Resource resource = controller.convertListToJsonResource(List.of(vet));

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
    }

    @Test
    void convertListToJsonResourceHandlesEmptyList() {
        VectorStore vectorStore = mock(VectorStore.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        when(builder.build()).thenReturn(webClient);

        VectorStoreController controller = new VectorStoreController(vectorStore, builder);

        Resource resource = controller.convertListToJsonResource(List.of());

        assertThat(resource).isNotNull();
    }

    @Test
    void loadVetDataToVectorStoreOnStartupLoadsFromExistingFile() throws IOException {
        SimpleVectorStore vectorStore = mock(SimpleVectorStore.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        when(builder.build()).thenReturn(webClient);

        VectorStoreController controller = new VectorStoreController(vectorStore, builder);

        ApplicationStartedEvent event = mock(ApplicationStartedEvent.class);
        controller.loadVetDataToVectorStoreOnStartup(event);

        verify(vectorStore).load(any(File.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void loadVetDataToVectorStoreOnStartupFetchesFromWebWhenNoResource() throws IOException {
        SimpleVectorStore vectorStore = mock(SimpleVectorStore.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        when(builder.build()).thenReturn(webClient);

        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        Vet vet = new Vet(1, "James", "Carter", Set.of(new Specialty(1, "radiology")));

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString())).thenReturn((WebClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(List.of(vet)));

        VectorStoreController controller = new VectorStoreController(vectorStore, builder);

        ApplicationStartedEvent event = mock(ApplicationStartedEvent.class);

        try (MockedConstruction<ClassPathResource> mocked = mockConstruction(ClassPathResource.class,
                (mock, context) -> when(mock.exists()).thenReturn(false))) {
            controller.loadVetDataToVectorStoreOnStartup(event);
        }

        verify(vectorStore).add(any(List.class));
        verify(vectorStore).save(any(File.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void loadVetDataToVectorStoreOnStartupWithNonSimpleVectorStore() throws IOException {
        VectorStore vectorStore = mock(VectorStore.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        when(builder.build()).thenReturn(webClient);

        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        Vet vet = new Vet(1, "James", "Carter", Set.of(new Specialty(1, "radiology")));

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString())).thenReturn((WebClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(List.of(vet)));

        VectorStoreController controller = new VectorStoreController(vectorStore, builder);

        ApplicationStartedEvent event = mock(ApplicationStartedEvent.class);

        try (MockedConstruction<ClassPathResource> mocked = mockConstruction(ClassPathResource.class,
                (mock, context) -> when(mock.exists()).thenReturn(false))) {
            controller.loadVetDataToVectorStoreOnStartup(event);
        }

        verify(vectorStore).add(any(List.class));
    }
}
