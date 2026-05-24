package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisitDetailsTest {

    @Test
    void recordAccessors() {
        VisitDetails visit = new VisitDetails(1, 10, "2024-01-01", "checkup");

        assertThat(visit.id()).isEqualTo(1);
        assertThat(visit.petId()).isEqualTo(10);
        assertThat(visit.date()).isEqualTo("2024-01-01");
        assertThat(visit.description()).isEqualTo("checkup");
    }
}
