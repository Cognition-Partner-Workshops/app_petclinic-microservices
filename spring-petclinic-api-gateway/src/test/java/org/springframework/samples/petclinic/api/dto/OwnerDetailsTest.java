package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerDetailsTest {

    @Test
    void getPetIdsReturnsAllPetIds() {
        PetDetails pet1 = PetDetails.PetDetailsBuilder.aPetDetails().id(1).name("Buddy").build();
        PetDetails pet2 = PetDetails.PetDetailsBuilder.aPetDetails().id(2).name("Max").build();

        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(10)
            .firstName("John")
            .lastName("Doe")
            .address("123 Main")
            .city("Springfield")
            .telephone("555-1234")
            .pets(List.of(pet1, pet2))
            .build();

        assertThat(owner.getPetIds()).containsExactly(1, 2);
        assertThat(owner.id()).isEqualTo(10);
        assertThat(owner.firstName()).isEqualTo("John");
        assertThat(owner.lastName()).isEqualTo("Doe");
        assertThat(owner.address()).isEqualTo("123 Main");
        assertThat(owner.city()).isEqualTo("Springfield");
        assertThat(owner.telephone()).isEqualTo("555-1234");
    }

    @Test
    void getPetIdsReturnsEmptyForNoPets() {
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .pets(List.of())
            .build();

        assertThat(owner.getPetIds()).isEmpty();
    }

    @Test
    void builderCreatesFullOwner() {
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(5)
            .firstName("Jane")
            .lastName("Smith")
            .address("456 Oak")
            .city("Portland")
            .telephone("555-5678")
            .pets(new ArrayList<>())
            .build();

        assertThat(owner.id()).isEqualTo(5);
        assertThat(owner.firstName()).isEqualTo("Jane");
        assertThat(owner.lastName()).isEqualTo("Smith");
        assertThat(owner.pets()).isEmpty();
    }
}
