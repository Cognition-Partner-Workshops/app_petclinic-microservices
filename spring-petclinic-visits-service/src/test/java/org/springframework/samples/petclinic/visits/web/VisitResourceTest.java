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

import java.util.List;


import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitResource.class)
@ActiveProfiles("test")
class VisitResourceTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    VisitRepository visitRepository;

    @Test
    void shouldFetchVisits() throws Exception {
        given(visitRepository.findByPetIdIn(asList(111, 222)))
            .willReturn(
                asList(
                    Visit.VisitBuilder.aVisit()
                        .id(1)
                        .petId(111)
                        .build(),
                    Visit.VisitBuilder.aVisit()
                        .id(2)
                        .petId(222)
                        .build(),
                    Visit.VisitBuilder.aVisit()
                        .id(3)
                        .petId(222)
                        .build()
                )
            );

        mvc.perform(get("/pets/visits?petId=111,222"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].id").value(1))
            .andExpect(jsonPath("$.items[1].id").value(2))
            .andExpect(jsonPath("$.items[2].id").value(3))
            .andExpect(jsonPath("$.items[0].petId").value(111))
            .andExpect(jsonPath("$.items[1].petId").value(222))
            .andExpect(jsonPath("$.items[2].petId").value(222));
    }

    @Test
    void shouldFetchVisitsForASinglePet() throws Exception {
        given(visitRepository.findByPetId(111)).willReturn(
            asList(Visit.VisitBuilder.aVisit().id(1).petId(111).description("rabies shot").build()));

        mvc.perform(get("/owners/1/pets/111/visits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].description").value("rabies shot"));
    }

    @Test
    void shouldReturnEmptyListWhenPetHasNoVisits() throws Exception {
        given(visitRepository.findByPetId(111)).willReturn(List.of());

        mvc.perform(get("/owners/1/pets/111/visits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldCreateVisitForPet() throws Exception {
        given(visitRepository.save(any(Visit.class))).willAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(post("/owners/1/pets/111/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2023-01-01\",\"description\":\"rabies shot\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.petId").value(111))
            .andExpect(jsonPath("$.description").value("rabies shot"));
    }

    @Test
    void shouldRejectVisitWithTooLongDescription() throws Exception {
        String tooLong = "x".repeat(8193);

        mvc.perform(post("/owners/1/pets/111/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2023-01-01\",\"description\":\"" + tooLong + "\"}"))
            .andExpect(status().isBadRequest());

        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void shouldRejectMalformedVisitPayload() throws Exception {
        mvc.perform(post("/owners/1/pets/111/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ not json"))
            .andExpect(status().isBadRequest());

        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void shouldRejectVisitsQueryWithoutPetIdParameter() throws Exception {
        mvc.perform(get("/pets/visits"))
            .andExpect(status().isBadRequest());
    }
}
