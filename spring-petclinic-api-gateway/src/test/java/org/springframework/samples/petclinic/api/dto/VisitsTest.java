package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VisitsTest {

    @Test
    void noArgConstructorCreatesEmptyList() {
        Visits visits = new Visits();
        assertThat(visits.items()).isNotNull().isEmpty();
    }

    @Test
    void constructorWithItemsStoresThem() {
        VisitDetails v1 = new VisitDetails(1, 10, "2024-01-01", "checkup");
        VisitDetails v2 = new VisitDetails(2, 20, "2024-02-01", "surgery");

        Visits visits = new Visits(new ArrayList<>(List.of(v1, v2)));

        assertThat(visits.items()).hasSize(2);
        assertThat(visits.items().get(0).description()).isEqualTo("checkup");
        assertThat(visits.items().get(1).description()).isEqualTo("surgery");
    }
}
