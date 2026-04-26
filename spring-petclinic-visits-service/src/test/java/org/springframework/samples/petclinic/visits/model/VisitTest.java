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
        visit.setDescription("checkup");
        visit.setPetId(7);

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("checkup");
        assertThat(visit.getPetId()).isEqualTo(7);
    }

    @Test
    void shouldHaveDefaultDate() {
        Visit visit = new Visit();
        assertThat(visit.getDate()).isNotNull();
    }

    @Test
    void shouldBuildVisitWithBuilder() {
        Date date = new Date();
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(1)
            .date(date)
            .description("surgery")
            .petId(5)
            .build();

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("surgery");
        assertThat(visit.getPetId()).isEqualTo(5);
    }

    @Test
    void shouldBuildVisitWithNullDate() {
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(2)
            .description("checkup")
            .petId(3)
            .build();

        assertThat(visit.getId()).isEqualTo(2);
        assertThat(visit.getDate()).isNull();
    }
}
