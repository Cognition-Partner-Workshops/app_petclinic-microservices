package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PetDetailsTest {

    @Test
    void compactConstructorInitializesNullVisitsToEmptyList() {
        PetDetails pet = new PetDetails(1, "Garfield", "2020-01-01",
            new PetType("cat"), null);

        assertThat(pet.visits()).isNotNull();
        assertThat(pet.visits()).isEmpty();
    }

    @Test
    void compactConstructorPreservesNonNullVisits() {
        VisitDetails visit = new VisitDetails(1, 10, "2024-01-01", "checkup");
        PetDetails pet = new PetDetails(1, "Garfield", "2020-01-01",
            new PetType("cat"), new ArrayList<>(List.of(visit)));

        assertThat(pet.visits()).hasSize(1);
        assertThat(pet.visits().get(0).description()).isEqualTo("checkup");
    }

    @Test
    void builderCreatesPetDetails() {
        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(1)
            .name("Garfield")
            .birthDate("2020-01-01")
            .type(new PetType("cat"))
            .visits(new ArrayList<>())
            .build();

        assertThat(pet.id()).isEqualTo(1);
        assertThat(pet.name()).isEqualTo("Garfield");
        assertThat(pet.birthDate()).isEqualTo("2020-01-01");
        assertThat(pet.type().name()).isEqualTo("cat");
        assertThat(pet.visits()).isEmpty();
    }

    @Test
    void builderWithNullVisitsInitializesEmptyList() {
        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(1)
            .name("Garfield")
            .visits(null)
            .build();

        assertThat(pet.visits()).isNotNull();
        assertThat(pet.visits()).isEmpty();
    }
}
