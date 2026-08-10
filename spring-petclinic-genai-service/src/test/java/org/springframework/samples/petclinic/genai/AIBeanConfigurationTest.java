package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AIBeanConfigurationTest {

    private final AIBeanConfiguration configuration = new AIBeanConfiguration();

    @Test
    void shouldProvideSimpleVectorStore() {
        assertThat(configuration.vectorStore(mock(EmbeddingModel.class)))
            .isInstanceOf(SimpleVectorStore.class);
    }

    @Test
    void shouldProvideWebClientBuilder() {
        assertThat(configuration.loadBalancedWebClientBuilder()).isNotNull();
    }
}
