package org.springframework.samples.petclinic.visits.model;

import java.util.Date;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisitTest {

    @Test
    void builderShouldPopulateEveryAttribute() {
        Date date = new Date(0);

        Visit visit = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(111)
            .date(date)
            .description("rabies shot")
            .build();

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getPetId()).isEqualTo(111);
        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("rabies shot");
    }

    @Test
    void newVisitShouldDefaultToCurrentDate() {
        assertThat(new Visit().getDate()).isNotNull();
    }

    @Test
    void settersShouldOverrideBuilderValues() {
        Visit visit = Visit.VisitBuilder.aVisit().id(1).petId(111).build();

        visit.setId(2);
        visit.setPetId(222);
        visit.setDescription("neutered");
        visit.setDate(new Date(1000));

        assertThat(visit.getId()).isEqualTo(2);
        assertThat(visit.getPetId()).isEqualTo(222);
        assertThat(visit.getDescription()).isEqualTo("neutered");
        assertThat(visit.getDate()).isEqualTo(new Date(1000));
    }
}
