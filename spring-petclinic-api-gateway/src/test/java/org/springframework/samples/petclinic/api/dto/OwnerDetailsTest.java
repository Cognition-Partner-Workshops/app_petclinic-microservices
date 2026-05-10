package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerDetailsTest {

    @Test
    void shouldGetPetIds() {
        PetDetails pet1 = PetDetails.PetDetailsBuilder.aPetDetails().id(10).name("Leo").build();
        PetDetails pet2 = PetDetails.PetDetailsBuilder.aPetDetails().id(20).name("Milo").build();
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(1)
            .firstName("George")
            .lastName("Franklin")
            .address("110 W. Liberty St.")
            .city("Madison")
            .telephone("6085551023")
            .pets(List.of(pet1, pet2))
            .build();

        assertThat(owner.getPetIds()).containsExactly(10, 20);
        assertThat(owner.id()).isEqualTo(1);
        assertThat(owner.firstName()).isEqualTo("George");
        assertThat(owner.lastName()).isEqualTo("Franklin");
        assertThat(owner.address()).isEqualTo("110 W. Liberty St.");
        assertThat(owner.city()).isEqualTo("Madison");
        assertThat(owner.telephone()).isEqualTo("6085551023");
    }

    @Test
    void shouldReturnEmptyPetIdsWhenNoPets() {
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .pets(List.of())
            .build();

        assertThat(owner.getPetIds()).isEmpty();
    }
}
