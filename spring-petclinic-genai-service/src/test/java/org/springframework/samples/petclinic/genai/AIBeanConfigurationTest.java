package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AIBeanConfigurationTest {

    @Test
    void loadBalancedWebClientBuilderReturnsBuilder() {
        AIBeanConfiguration config = new AIBeanConfiguration();
        WebClient.Builder builder = config.loadBalancedWebClientBuilder();
        assertThat(builder).isNotNull();
    }

    @Test
    void vectorStoreReturnsSimpleVectorStore() {
        AIBeanConfiguration config = new AIBeanConfiguration();
        EmbeddingModel embeddingModel = mock(EmbeddingModel.class);
        VectorStore store = config.vectorStore(embeddingModel);
        assertThat(store).isNotNull();
    }
}
