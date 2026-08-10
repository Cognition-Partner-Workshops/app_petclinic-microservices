package org.springframework.samples.petclinic.api.dto;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void ownerDetailsShouldExposePetIds() {
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .id(1)
            .firstName("George")
            .lastName("Franklin")
            .address("110 W. Liberty St.")
            .city("Madison")
            .telephone("6085551023")
            .pets(List.of(pet(20, "Leo"), pet(21, "Basil")))
            .build();

        assertThat(owner.getPetIds()).containsExactly(20, 21);
        assertThat(owner.city()).isEqualTo("Madison");
        assertThat(owner.telephone()).isEqualTo("6085551023");
        assertThat(owner.address()).isEqualTo("110 W. Liberty St.");
        assertThat(owner.lastName()).isEqualTo("Franklin");
        assertThat(owner.id()).isEqualTo(1);
    }

    @Test
    void ownerWithoutPetsShouldHaveNoPetIds() {
        OwnerDetails owner = OwnerDetails.OwnerDetailsBuilder.anOwnerDetails()
            .pets(List.of())
            .build();

        assertThat(owner.getPetIds()).isEmpty();
    }

    @Test
    void petDetailsShouldDefaultToMutableEmptyVisitList() {
        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(20)
            .name("Leo")
            .birthDate("2010-09-07")
            .type(new PetType("cat"))
            .build();

        assertThat(pet.visits()).isEmpty();
        pet.visits().add(new VisitDetails(1, 20, "2013-01-01", "rabies shot"));
        assertThat(pet.visits()).hasSize(1);
        assertThat(pet.type().name()).isEqualTo("cat");
        assertThat(pet.birthDate()).isEqualTo("2010-09-07");
    }

    @Test
    void visitsShouldDefaultToEmptyItems() {
        assertThat(new Visits().items()).isEmpty();
    }

    private PetDetails pet(int id, String name) {
        return PetDetails.PetDetailsBuilder.aPetDetails().id(id).name(name).build();
    }
}
