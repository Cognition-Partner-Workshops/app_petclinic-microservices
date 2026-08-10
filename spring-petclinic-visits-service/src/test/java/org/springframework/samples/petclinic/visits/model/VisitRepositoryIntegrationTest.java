package org.springframework.samples.petclinic.visits.model;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VisitRepositoryIntegrationTest {

    @Autowired
    VisitRepository visitRepository;

    @Test
    void shouldFindVisitsOfASinglePet() {
        List<Visit> visits = visitRepository.findByPetId(7);

        assertThat(visits).hasSize(2)
            .extracting(Visit::getDescription)
            .containsExactlyInAnyOrder("rabies shot", "spayed");
    }

    @Test
    void shouldReturnNoVisitsForUnknownPet() {
        assertThat(visitRepository.findByPetId(9999)).isEmpty();
    }

    @Test
    void shouldFindVisitsOfSeveralPets() {
        assertThat(visitRepository.findByPetIdIn(List.of(7, 8))).hasSize(4);
    }

    @Test
    void shouldReturnNoVisitsForEmptyPetIdList() {
        assertThat(visitRepository.findByPetIdIn(List.of())).isEmpty();
    }

    @Test
    void shouldPersistNewVisit() {
        Visit visit = Visit.VisitBuilder.aVisit()
            .petId(7)
            .date(new Date())
            .description("check-up")
            .build();

        Visit saved = visitRepository.save(visit);

        assertThat(saved.getId()).isNotNull();
        assertThat(visitRepository.findByPetId(7)).hasSize(3);
    }
}
