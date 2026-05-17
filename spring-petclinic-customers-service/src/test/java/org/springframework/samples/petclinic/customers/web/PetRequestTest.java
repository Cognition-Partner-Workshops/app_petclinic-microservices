package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class PetRequestTest {

    @Test
    void recordAccessors() {
        Date birthDate = new Date();
        PetRequest request = new PetRequest(5, birthDate, "Buddy", 2);

        assertThat(request.id()).isEqualTo(5);
        assertThat(request.birthDate()).isEqualTo(birthDate);
        assertThat(request.name()).isEqualTo("Buddy");
        assertThat(request.typeId()).isEqualTo(2);
    }
}
