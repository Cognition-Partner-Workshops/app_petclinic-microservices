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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitResource.class)
@ActiveProfiles("test")
class VisitResourceAdditionalTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    VisitRepository visitRepository;

    @Test
    void shouldCreateVisit() throws Exception {
        Visit savedVisit = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(7)
            .description("rabies shot")
            .build();

        given(visitRepository.save(any(Visit.class))).willReturn(savedVisit);

        mvc.perform(post("/owners/1/pets/7/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"date":"2024-01-15","description":"rabies shot"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.petId").value(7))
            .andExpect(jsonPath("$.description").value("rabies shot"));

        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void shouldReadVisitsByPetId() throws Exception {
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(7)
            .description("checkup")
            .build();

        given(visitRepository.findByPetId(7)).willReturn(List.of(visit));

        mvc.perform(get("/owners/1/pets/7/visits").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].petId").value(7))
            .andExpect(jsonPath("$[0].description").value("checkup"));
    }

    @Test
    void shouldReadVisitsByMultiplePetIds() throws Exception {
        Visit visit1 = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(111)
            .build();
        Visit visit2 = Visit.VisitBuilder.aVisit()
            .id(2)
            .petId(222)
            .build();

        given(visitRepository.findByPetIdIn(List.of(111, 222)))
            .willReturn(List.of(visit1, visit2));

        mvc.perform(get("/pets/visits?petId=111,222"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].petId").value(111))
            .andExpect(jsonPath("$.items[1].petId").value(222));
    }
}
