package org.springframework.samples.petclinic.visits.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class VisitTest {

    @Test
    void shouldGetAndSetFields() {
        Visit visit = new Visit();
        Date date = new Date();
        visit.setId(1);
        visit.setDate(date);
        visit.setDescription("checkup");
        visit.setPetId(10);

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("checkup");
        assertThat(visit.getPetId()).isEqualTo(10);
    }

    @Test
    void shouldHaveDefaultDate() {
        Visit visit = new Visit();
        assertThat(visit.getDate()).isNotNull();
    }

    @Test
    void shouldBuildWithBuilder() {
        Date date = new Date();
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(5)
            .date(date)
            .description("vaccination")
            .petId(20)
            .build();

        assertThat(visit.getId()).isEqualTo(5);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("vaccination");
        assertThat(visit.getPetId()).isEqualTo(20);
    }

    @Test
    void shouldBuildWithNullFields() {
        Visit visit = Visit.VisitBuilder.aVisit()
            .build();

        assertThat(visit.getId()).isNull();
        assertThat(visit.getDate()).isNull();
        assertThat(visit.getDescription()).isNull();
        assertThat(visit.getPetId()).isZero();
    }
}
