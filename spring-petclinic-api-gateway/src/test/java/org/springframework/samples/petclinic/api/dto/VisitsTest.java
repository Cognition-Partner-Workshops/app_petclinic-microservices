package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VisitsTest {

    @Test
    void defaultConstructorCreatesEmptyList() {
        Visits visits = new Visits();
        assertThat(visits.items()).isEmpty();
    }

    @Test
    void parameterizedConstructorSetsItems() {
        VisitDetails v = new VisitDetails(1, 10, "2024-01-01", "checkup");
        Visits visits = new Visits(List.of(v));

        assertThat(visits.items()).hasSize(1);
        assertThat(visits.items().get(0).description()).isEqualTo("checkup");
    }
}
