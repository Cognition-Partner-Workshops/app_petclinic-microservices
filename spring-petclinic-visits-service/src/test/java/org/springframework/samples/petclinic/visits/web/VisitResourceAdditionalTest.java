package org.springframework.samples.petclinic.visits.web;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitResource.class)
@ActiveProfiles("test")
class VisitResourceAdditionalTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    VisitRepository visitRepository;

    @Test
    void shouldCreateVisit() throws Exception {
        Visit saved = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(7)
            .description("check up")
            .build();
        given(visitRepository.save(any(Visit.class))).willReturn(saved);

        mvc.perform(post("/owners/1/pets/7/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"check up\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.petId").value(7))
            .andExpect(jsonPath("$.description").value("check up"));
    }

    @Test
    void shouldReadVisitsByPetId() throws Exception {
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(7)
            .description("routine")
            .build();
        given(visitRepository.findByPetId(7)).willReturn(List.of(visit));

        mvc.perform(get("/owners/1/pets/7/visits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].petId").value(7));
    }

    @Test
    void shouldReadVisitsByMultiplePetIds() throws Exception {
        Visit visit1 = Visit.VisitBuilder.aVisit().id(1).petId(1).build();
        Visit visit2 = Visit.VisitBuilder.aVisit().id(2).petId(2).build();
        given(visitRepository.findByPetIdIn(List.of(1, 2))).willReturn(List.of(visit1, visit2));

        mvc.perform(get("/pets/visits?petId=1,2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(2));
    }
}
