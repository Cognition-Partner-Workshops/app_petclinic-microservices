package org.springframework.samples.petclinic.customers.web.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.OwnerRequest;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerEntityMapperTest {

    private final OwnerEntityMapper mapper = new OwnerEntityMapper();

    @Test
    void shouldMapRequestToOwner() {
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Springfield", "5551234567");

        Owner result = mapper.map(owner, request);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getAddress()).isEqualTo("123 Main St");
        assertThat(result.getCity()).isEqualTo("Springfield");
        assertThat(result.getTelephone()).isEqualTo("5551234567");
    }

    @Test
    void shouldReturnSameOwnerInstance() {
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("Jane", "Smith", "456 Oak Ave", "Portland", "5559876543");

        Owner result = mapper.map(owner, request);

        assertThat(result).isSameAs(owner);
    }
}
