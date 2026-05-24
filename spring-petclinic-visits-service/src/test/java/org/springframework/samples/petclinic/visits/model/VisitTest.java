package org.springframework.samples.petclinic.visits.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class VisitTest {

    @Test
    void gettersAndSetters() {
        Visit visit = new Visit();
        Date date = new Date();

        visit.setId(1);
        visit.setDate(date);
        visit.setDescription("rabies shot");
        visit.setPetId(7);

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("rabies shot");
        assertThat(visit.getPetId()).isEqualTo(7);
    }

    @Test
    void defaultDateIsNotNull() {
        Visit visit = new Visit();
        assertThat(visit.getDate()).isNotNull();
    }

    @Test
    void builderCreatesVisit() {
        Date date = new Date();
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(1)
            .date(date)
            .description("checkup")
            .petId(5)
            .build();

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("checkup");
        assertThat(visit.getPetId()).isEqualTo(5);
    }

    @Test
    void builderWithNullValues() {
        Visit visit = Visit.VisitBuilder.aVisit()
            .build();

        assertThat(visit.getId()).isNull();
        assertThat(visit.getDate()).isNull();
        assertThat(visit.getDescription()).isNull();
        assertThat(visit.getPetId()).isEqualTo(0);
    }
}
