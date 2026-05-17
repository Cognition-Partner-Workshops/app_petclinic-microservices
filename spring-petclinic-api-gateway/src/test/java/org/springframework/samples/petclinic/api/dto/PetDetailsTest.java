package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PetDetailsTest {

    @Test
    void builderCreatesFullPetDetails() {
        PetType type = new PetType("Cat");
        VisitDetails visit = new VisitDetails(1, 10, "2024-01-01", "checkup");

        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(10)
            .name("Garfield")
            .birthDate("2020-06-15")
            .type(type)
            .visits(new ArrayList<>(List.of(visit)))
            .build();

        assertThat(pet.id()).isEqualTo(10);
        assertThat(pet.name()).isEqualTo("Garfield");
        assertThat(pet.birthDate()).isEqualTo("2020-06-15");
        assertThat(pet.type().name()).isEqualTo("Cat");
        assertThat(pet.visits()).hasSize(1);
        assertThat(pet.visits().get(0).description()).isEqualTo("checkup");
    }

    @Test
    void compactConstructorDefaultsNullVisitsToEmptyList() {
        PetDetails pet = new PetDetails(1, "Buddy", "2021-01-01", null, null);
        assertThat(pet.visits()).isNotNull().isEmpty();
    }

    @Test
    void compactConstructorKeepsProvidedVisits() {
        VisitDetails v = new VisitDetails(1, 1, "2024-01-01", "test");
        PetDetails pet = new PetDetails(1, "Buddy", "2021-01-01", null, new ArrayList<>(List.of(v)));
        assertThat(pet.visits()).hasSize(1);
    }
}
