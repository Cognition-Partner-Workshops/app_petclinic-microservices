package org.springframework.samples.petclinic.genai;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Spring configuration class providing infrastructure beans for the GenAI service.
 * <p>
 * Defines a {@link SimpleVectorStore} for storing and searching veterinarian embeddings
 * used in Retrieval-Augmented Generation (RAG), and a load-balanced {@link WebClient.Builder}
 * for making service-to-service HTTP calls resolved through Eureka discovery.
 *
 * @author Oded Shopen
 */
@Configuration
public class AIBeanConfiguration {

	/**
	 * Creates an in-memory {@link SimpleVectorStore} backed by the provided embedding model.
	 * Used to store and query veterinarian document embeddings for RAG functionality.
	 *
	 * @param embeddingModel the AI embedding model used to generate vector representations
	 * @return a configured {@link VectorStore} instance
	 */
	@Bean
	VectorStore vectorStore(EmbeddingModel embeddingModel) {
		return SimpleVectorStore.builder(embeddingModel).build();
	}

    /**
     * Creates a load-balanced {@link WebClient.Builder} for reactive HTTP calls
     * to other PetClinic microservices (e.g. vets-service) discovered via Eureka.
     *
     * @return a {@link WebClient.Builder} with client-side load balancing enabled
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
