package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerDetailsTest {

    @Test
    void getPetIdsReturnsPetIds() {
        PetDetails pet1 = PetDetails.PetDetailsBuilder.aPetDetails().id(10).build();
        PetDetails pet2 = PetDetails.PetDetailsBuilder.aPetDetails().id(20).build();

        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .address("123 Main")
            .city("NYC")
            .telephone("1234567890")
            .pets(List.of(pet1, pet2))
            .build();

        assertThat(owner.getPetIds()).containsExactly(10, 20);
    }

    @Test
    void recordAccessors() {
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(1)
            .firstName("Jane")
            .lastName("Smith")
            .address("456 Oak")
            .city("LA")
            .telephone("5551234")
            .pets(List.of())
            .build();

        assertThat(owner.id()).isEqualTo(1);
        assertThat(owner.firstName()).isEqualTo("Jane");
        assertThat(owner.lastName()).isEqualTo("Smith");
        assertThat(owner.address()).isEqualTo("456 Oak");
        assertThat(owner.city()).isEqualTo("LA");
        assertThat(owner.telephone()).isEqualTo("5551234");
        assertThat(owner.pets()).isEmpty();
    }

    @Test
    void getPetIdsReturnsEmptyForNoPets() {
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .pets(List.of())
            .build();

        assertThat(owner.getPetIds()).isEmpty();
    }
}
