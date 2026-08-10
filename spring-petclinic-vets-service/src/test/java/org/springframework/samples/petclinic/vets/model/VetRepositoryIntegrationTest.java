package org.springframework.samples.petclinic.vets.model;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryIntegrationTest {

    @Autowired
    VetRepository vetRepository;

    @Test
    void shouldLoadVetsWithTheirSpecialties() {
        List<Vet> vets = vetRepository.findAll();

        assertThat(vets).hasSize(6);
        Vet leary = vets.stream().filter(v -> "Leary".equals(v.getLastName())).findFirst().orElseThrow();
        assertThat(leary.getSpecialties()).extracting(Specialty::getName).containsExactly("radiology");
    }

    @Test
    void shouldFindVetById() {
        assertThat(vetRepository.findById(1)).get().extracting(Vet::getFirstName).isEqualTo("James");
    }

    @Test
    void shouldReturnEmptyForUnknownVet() {
        assertThat(vetRepository.findById(9999)).isEmpty();
    }

    @Test
    void shouldPersistNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("Ada");
        vet.setLastName("Lovelace");

        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(vetRepository.findById(saved.getId())).isPresent();
    }
}
