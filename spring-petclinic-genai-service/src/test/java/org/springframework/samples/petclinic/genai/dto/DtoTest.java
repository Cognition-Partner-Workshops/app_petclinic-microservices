package org.springframework.samples.petclinic.genai.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void ownerDetailsShouldExposeItsPets() {
        PetDetails pet = new PetDetails(20, "Leo", "2010-09-07", new PetType("cat"),
            List.of(new VisitDetails(1, 20, "2013-01-01", "rabies shot")));
        OwnerDetails owner = new OwnerDetails(1, "George", "Franklin", "110 W. Liberty St.",
            "Madison", "6085551023", List.of(pet));

        assertThat(owner.pets()).containsExactly(pet);
        assertThat(owner.id()).isEqualTo(1);
        assertThat(owner.firstName()).isEqualTo("George");
        assertThat(owner.lastName()).isEqualTo("Franklin");
        assertThat(owner.address()).isEqualTo("110 W. Liberty St.");
        assertThat(owner.city()).isEqualTo("Madison");
        assertThat(owner.telephone()).isEqualTo("6085551023");
        assertThat(pet.visits().get(0).description()).isEqualTo("rabies shot");
        assertThat(pet.visits().get(0).petId()).isEqualTo(20);
        assertThat(pet.visits().get(0).id()).isEqualTo(1);
        assertThat(pet.visits().get(0).date()).isEqualTo("2013-01-01");
        assertThat(pet.type().name()).isEqualTo("cat");
        assertThat(pet.birthDate()).isEqualTo("2010-09-07");
        assertThat(pet.name()).isEqualTo("Leo");
        assertThat(pet.id()).isEqualTo(20);
    }

    @Test
    void petRequestShouldExposeItsAttributes() {
        Date birthDate = new Date(0);
        PetRequest request = new PetRequest(5, birthDate, "Leo", 1);

        assertThat(request.id()).isEqualTo(5);
        assertThat(request.birthDate()).isEqualTo(birthDate);
        assertThat(request.name()).isEqualTo("Leo");
        assertThat(request.typeId()).isEqualTo(1);
    }

    @Test
    void vetShouldExposeItsSpecialties() {
        Specialty radiology = new Specialty(1, "radiology");
        Vet vet = new Vet(2, "Helen", "Leary", Set.of(radiology));

        assertThat(vet.id()).isEqualTo(2);
        assertThat(vet.firstName()).isEqualTo("Helen");
        assertThat(vet.lastName()).isEqualTo("Leary");
        assertThat(vet.specialties()).containsExactly(radiology);
        assertThat(radiology.id()).isEqualTo(1);
        assertThat(radiology.name()).isEqualTo("radiology");
    }
}
