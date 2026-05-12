package org.springframework.samples.petclinic.visits.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Provider-side contract verification test for the Visits Service.
 * Verifies the contract: GET /pets/visits?petId=1,2 returns visits for given pets.
 * This contract is consumed by the API Gateway's VisitsServiceClient.
 */
@WebMvcTest(VisitResource.class)
@ActiveProfiles("test")
class ContractVerifierBaseTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private VisitRepository visitRepository;

    @Test
    void contractShouldReturnVisitsForPets() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Visit visit1 = new Visit();
        visit1.setId(1);
        visit1.setPetId(1);
        visit1.setDate(sdf.parse("2023-01-01"));
        visit1.setDescription("rabies shot");

        Visit visit2 = new Visit();
        visit2.setId(2);
        visit2.setPetId(2);
        visit2.setDate(sdf.parse("2023-03-15"));
        visit2.setDescription("neutered");

        given(visitRepository.findByPetIdIn(Arrays.asList(1, 2)))
            .willReturn(Arrays.asList(visit1, visit2));

        mvc.perform(get("/pets/visits")
                .param("petId", "1,2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.items[0].id").value(1))
            .andExpect(jsonPath("$.items[0].petId").value(1))
            .andExpect(jsonPath("$.items[0].description").value("rabies shot"))
            .andExpect(jsonPath("$.items[1].id").value(2))
            .andExpect(jsonPath("$.items[1].petId").value(2))
            .andExpect(jsonPath("$.items[1].description").value("neutered"));
    }
}
