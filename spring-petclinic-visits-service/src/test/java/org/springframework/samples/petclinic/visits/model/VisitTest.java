package org.springframework.samples.petclinic.visits.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class VisitTest {

    @Test
    void shouldSetAndGetFields() {
        Visit visit = new Visit();
        Date date = new Date();

        visit.setId(1);
        visit.setDate(date);
        visit.setDescription("annual checkup");
        visit.setPetId(7);

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("annual checkup");
        assertThat(visit.getPetId()).isEqualTo(7);
    }

    @Test
    void shouldBuildWithBuilder() {
        Date date = new Date();
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(5)
            .date(date)
            .description("surgery")
            .petId(3)
            .build();

        assertThat(visit.getId()).isEqualTo(5);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("surgery");
        assertThat(visit.getPetId()).isEqualTo(3);
    }

    @Test
    void shouldHaveDefaultDate() {
        Visit visit = new Visit();
        assertThat(visit.getDate()).isNotNull();
    }
}
