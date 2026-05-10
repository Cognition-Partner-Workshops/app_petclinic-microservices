package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisitDetailsTest {

    @Test
    void shouldHoldAllFields() {
        VisitDetails visit = new VisitDetails(1, 10, "2024-01-15", "vaccination");

        assertThat(visit.id()).isEqualTo(1);
        assertThat(visit.petId()).isEqualTo(10);
        assertThat(visit.date()).isEqualTo("2024-01-15");
        assertThat(visit.description()).isEqualTo("vaccination");
    }
}
