package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PetDetailsTest {

    @Test
    void shouldBuildWithAllFields() {
        PetType type = new PetType("cat");
        List<VisitDetails> visits = new ArrayList<>();
        visits.add(new VisitDetails(1, 10, "2024-01-01", "checkup"));

        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(10)
            .name("Leo")
            .birthDate("2020-01-15")
            .type(type)
            .visits(visits)
            .build();

        assertThat(pet.id()).isEqualTo(10);
        assertThat(pet.name()).isEqualTo("Leo");
        assertThat(pet.birthDate()).isEqualTo("2020-01-15");
        assertThat(pet.type()).isEqualTo(type);
        assertThat(pet.visits()).hasSize(1);
    }

    @Test
    void shouldInitializeVisitsToEmptyListWhenNull() {
        PetDetails pet = new PetDetails(1, "Leo", "2020-01-01", null, null);
        assertThat(pet.visits()).isNotNull().isEmpty();
    }

    @Test
    void shouldKeepProvidedVisitsList() {
        List<VisitDetails> visits = new ArrayList<>();
        visits.add(new VisitDetails(1, 10, "2024-01-01", "checkup"));
        PetDetails pet = new PetDetails(1, "Leo", "2020-01-01", null, visits);
        assertThat(pet.visits()).hasSize(1);
    }
}
