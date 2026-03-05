package org.springframework.samples.petclinic.genai;

import java.net.URI;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.samples.petclinic.genai.dto.OwnerDetails;
import org.springframework.samples.petclinic.genai.dto.PetDetails;
import org.springframework.samples.petclinic.genai.dto.PetRequest;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Functions that are invoked by the LLM will use this bean to query the system of record
 * for information such as listing owners and vets, or adding pets to an owner.
 *
 * @author Oded Shopen
 */
@Service
public class AIDataProvider {

	private final VectorStore vectorStore;

    private final RestClient restClient;

    private final DiscoveryClient discoveryClient;

	/**
	 * Constructs the data provider with vector store and discovery client dependencies.
	 *
	 * @param vectorStore     the vector store for veterinarian similarity searches
	 * @param discoveryClient the Eureka discovery client for resolving service URIs
	 */
	public AIDataProvider(VectorStore vectorStore, DiscoveryClient discoveryClient) {
        this.restClient = RestClient.builder().build();
        this.vectorStore = vectorStore;
        this.discoveryClient = discoveryClient;
    }

	/**
	 * Retrieves all pet owners from the Customers microservice.
	 *
	 * @return a list of all {@link OwnerDetails} records
	 */
	public List<OwnerDetails> getAllOwners() {
        return restClient
            .get()
            .uri(getCustomerServiceUri() + "/owners")
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });
	}

    /**
     * Searches for veterinarians using vector similarity against the stored embeddings.
     * <p>
     * Serializes the vet request to JSON and performs a similarity search in the vector
     * store. Returns up to 20 results when criteria are provided, or up to 50 when no
     * criteria are specified (i.e. {@code vetRequest} is {@code null}).
     *
     * @param vetRequest optional vet criteria for the similarity search; may be {@code null}
     * @return a list of formatted vet document strings matching the query
     * @throws JacksonException if the vet request cannot be serialized to JSON
     */
    public List<String> getVets(Vet vetRequest) throws JacksonException {
		ObjectMapper objectMapper = new ObjectMapper();
		String vetAsJson = objectMapper.writeValueAsString(vetRequest);

        int topK = 20;
        if (vetRequest == null) {
            // Provide a limit of 50 results when zero parameters are sent
            topK = 50;
        }
        SearchRequest sr = SearchRequest.builder()
            .query(vetAsJson)
            .topK(topK)
            .build();


		List<Document> topMatches = this.vectorStore.similaritySearch(sr);
		return topMatches.stream().map(Document::getFormattedContent).toList();
	}

	/**
	 * Adds a new pet to the specified owner via the Customers microservice.
	 *
	 * @param ownerId    the ID of the owner to add the pet to
	 * @param petRequest the pet data to create
	 * @return the created {@link PetDetails} as returned by the Customers service
	 */
	public PetDetails addPetToOwner(int ownerId, PetRequest petRequest) {
        return restClient
            .post()
            .uri(getCustomerServiceUri()  + "/owners/" + ownerId + "/pets")
            .body(petRequest)
            .retrieve()
            .body(PetDetails.class);
	}

	/**
	 * Creates a new pet owner via the Customers microservice.
	 *
	 * @param ownerRequest the owner data to create
	 * @return the created {@link OwnerDetails} as returned by the Customers service
	 */
	public OwnerDetails addOwnerToPetclinic(OwnerRequest ownerRequest) {
       return restClient
            .post()
            .uri(getCustomerServiceUri() + "/owners")
            .body(ownerRequest)
            .retrieve()
            .body(OwnerDetails.class);
	}

    /**
     * Resolves the base URI of the Customers microservice using Eureka service discovery.
     *
     * @return the URI of the first available customers-service instance
     */
    @NotNull
    private URI getCustomerServiceUri() {
        return discoveryClient.getInstances("customers-service").get(0).getUri();
    }

}
