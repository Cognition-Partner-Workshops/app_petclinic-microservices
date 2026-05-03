package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PetDetailsTest {

    @Test
    void builderCreatesValidPetDetails() {
        PetType type = new PetType("Dog");
        VisitDetails visit = new VisitDetails(1, 10, "2024-01-01", "checkup");

        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(10)
            .name("Rex")
            .birthDate("2020-01-01")
            .type(type)
            .visits(new ArrayList<>(List.of(visit)))
            .build();

        assertThat(pet.id()).isEqualTo(10);
        assertThat(pet.name()).isEqualTo("Rex");
        assertThat(pet.birthDate()).isEqualTo("2020-01-01");
        assertThat(pet.type().name()).isEqualTo("Dog");
        assertThat(pet.visits()).hasSize(1);
    }

    @Test
    void nullVisitsDefaultsToEmptyList() {
        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(1)
            .name("Test")
            .visits(null)
            .build();

        assertThat(pet.visits()).isNotNull().isEmpty();
    }

    @Test
    void visitsAreMutable() {
        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(1)
            .visits(new ArrayList<>())
            .build();

        pet.visits().add(new VisitDetails(1, 1, "2024-01-01", "test"));
        assertThat(pet.visits()).hasSize(1);
    }
}
