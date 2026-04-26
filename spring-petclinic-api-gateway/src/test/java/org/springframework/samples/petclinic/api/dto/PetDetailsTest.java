package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PetDetailsTest {

    @Test
    void shouldBuildWithAllFields() {
        PetType type = new PetType("cat");
        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(1)
            .name("Garfield")
            .birthDate("2020-01-01")
            .type(type)
            .visits(new ArrayList<>())
            .build();

        assertThat(pet.id()).isEqualTo(1);
        assertThat(pet.name()).isEqualTo("Garfield");
        assertThat(pet.birthDate()).isEqualTo("2020-01-01");
        assertThat(pet.type().name()).isEqualTo("cat");
        assertThat(pet.visits()).isEmpty();
    }

    @Test
    void shouldInitializeVisitsToEmptyListWhenNull() {
        PetDetails pet = new PetDetails(1, "Garfield", "2020-01-01", null, null);
        assertThat(pet.visits()).isNotNull().isEmpty();
    }

    @Test
    void shouldKeepVisitsWhenProvided() {
        VisitDetails visit = new VisitDetails(1, 1, "2020-01-01", "checkup");
        PetDetails pet = new PetDetails(1, "Garfield", "2020-01-01", null, new ArrayList<>(List.of(visit)));
        assertThat(pet.visits()).hasSize(1);
    }
}
