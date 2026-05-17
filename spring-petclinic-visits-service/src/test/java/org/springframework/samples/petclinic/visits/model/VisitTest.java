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
        visit.setDescription("annual checkup");
        visit.setPetId(5);

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("annual checkup");
        assertThat(visit.getPetId()).isEqualTo(5);
    }

    @Test
    void defaultDateIsNotNull() {
        Visit visit = new Visit();
        assertThat(visit.getDate()).isNotNull();
    }

    @Test
    void defaultIdIsNull() {
        Visit visit = new Visit();
        assertThat(visit.getId()).isNull();
    }

    @Test
    void builderCreatesVisit() {
        Date date = new Date();
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(10)
            .date(date)
            .description("surgery")
            .petId(3)
            .build();

        assertThat(visit.getId()).isEqualTo(10);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("surgery");
        assertThat(visit.getPetId()).isEqualTo(3);
    }

    @Test
    void builderWithNullValues() {
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(null)
            .date(null)
            .description(null)
            .petId(0)
            .build();

        assertThat(visit.getId()).isNull();
        assertThat(visit.getDate()).isNull();
        assertThat(visit.getDescription()).isNull();
        assertThat(visit.getPetId()).isZero();
    }
}
