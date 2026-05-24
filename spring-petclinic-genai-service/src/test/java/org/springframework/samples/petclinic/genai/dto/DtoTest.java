package org.springframework.samples.petclinic.genai.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void ownerDetailsRecordAccessors() {
        PetDetails pet = new PetDetails(1, "Buddy", "2020-01-01", new PetType("dog"), List.of());
        OwnerDetails owner = new OwnerDetails(1, "George", "Franklin", "addr", "city", "phone", List.of(pet));

        assertThat(owner.id()).isEqualTo(1);
        assertThat(owner.firstName()).isEqualTo("George");
        assertThat(owner.lastName()).isEqualTo("Franklin");
        assertThat(owner.address()).isEqualTo("addr");
        assertThat(owner.city()).isEqualTo("city");
        assertThat(owner.telephone()).isEqualTo("phone");
        assertThat(owner.pets()).hasSize(1);
    }

    @Test
    void petDetailsRecordAccessors() {
        VisitDetails visit = new VisitDetails(1, 10, "2024-01-01", "checkup");
        PetDetails pet = new PetDetails(1, "Buddy", "2020-01-01", new PetType("dog"), List.of(visit));

        assertThat(pet.id()).isEqualTo(1);
        assertThat(pet.name()).isEqualTo("Buddy");
        assertThat(pet.birthDate()).isEqualTo("2020-01-01");
        assertThat(pet.type().name()).isEqualTo("dog");
        assertThat(pet.visits()).hasSize(1);
    }

    @Test
    void petRequestRecordAccessors() {
        Date date = new Date();
        PetRequest request = new PetRequest(0, date, "Buddy", 1);

        assertThat(request.id()).isEqualTo(0);
        assertThat(request.birthDate()).isEqualTo(date);
        assertThat(request.name()).isEqualTo("Buddy");
        assertThat(request.typeId()).isEqualTo(1);
    }

    @Test
    void vetRecordAccessors() {
        Specialty s = new Specialty(1, "radiology");
        Vet vet = new Vet(1, "James", "Carter", Set.of(s));

        assertThat(vet.id()).isEqualTo(1);
        assertThat(vet.firstName()).isEqualTo("James");
        assertThat(vet.lastName()).isEqualTo("Carter");
        assertThat(vet.specialties()).hasSize(1);
    }

    @Test
    void specialtyRecordAccessors() {
        Specialty s = new Specialty(1, "radiology");
        assertThat(s.id()).isEqualTo(1);
        assertThat(s.name()).isEqualTo("radiology");
    }

    @Test
    void visitDetailsRecordAccessors() {
        VisitDetails visit = new VisitDetails(1, 10, "2024-01-01", "checkup");
        assertThat(visit.id()).isEqualTo(1);
        assertThat(visit.petId()).isEqualTo(10);
        assertThat(visit.date()).isEqualTo("2024-01-01");
        assertThat(visit.description()).isEqualTo("checkup");
    }

    @Test
    void petTypeRecordAccessor() {
        PetType petType = new PetType("cat");
        assertThat(petType.name()).isEqualTo("cat");
    }
}
