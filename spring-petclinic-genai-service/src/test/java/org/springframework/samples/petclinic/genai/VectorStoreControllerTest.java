package org.springframework.samples.petclinic.genai;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.genai.dto.Specialty;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class VectorStoreControllerTest {

    private final SimpleVectorStore vectorStore = mock(SimpleVectorStore.class);

    private final VectorStoreController controller =
        new VectorStoreController(vectorStore, WebClient.builder());

    @Test
    void shouldLoadPreEmbeddedVectorStoreFromClasspath() throws Exception {
        controller.loadVetDataToVectorStoreOnStartup(null);

        verify(vectorStore).load(any(File.class));
    }

    @Test
    void shouldEmbedVetsFetchedFromVetsServiceWhenNoPreEmbeddedStoreExists() throws Exception {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(request -> Mono.just(
            ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    [{"id":1,"firstName":"James","lastName":"Carter","specialties":[]}]
                    """)
                .build()));
        VectorStoreController controller = new VectorStoreController(vectorStore, builder);

        withoutPreEmbeddedVectorStore(() -> controller.loadVetDataToVectorStoreOnStartup(null));

        verify(vectorStore).add(anyList());
        verify(vectorStore).save(any(File.class));
    }

    @Test
    void shouldSerializeVetsToJsonResource() throws Exception {
        Vet vet = new Vet(1, "James", "Carter", Set.of(new Specialty(1, "radiology")));

        Resource resource = controller.convertListToJsonResource(List.of(vet));

        assertThat(resource).isNotNull();
        assertThat(resource.getContentAsString(java.nio.charset.StandardCharsets.UTF_8))
            .contains("James")
            .contains("radiology");
    }

    @Test
    void shouldSerializeEmptyVetListToEmptyJsonArray() throws Exception {
        Resource resource = controller.convertListToJsonResource(List.of());

        assertThat(resource.getContentAsString(java.nio.charset.StandardCharsets.UTF_8))
            .isEqualTo("[]");
    }

    /**
     * Runs the given action with a class loader that hides the pre-embedded {@code vectorstore.json}.
     */
    private void withoutPreEmbeddedVectorStore(ThrowingRunnable action) throws Exception {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(new ClassLoader(original) {
            @Override
            public URL getResource(String name) {
                return "vectorstore.json".equals(name) ? null : super.getResource(name);
            }
        });
        try {
            action.run();
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
