package org.springframework.samples.petclinic.customers.web.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.OwnerRequest;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerEntityMapperTest {

    private final OwnerEntityMapper mapper = new OwnerEntityMapper();

    @Test
    void shouldMapAllFields() {
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Springfield", "1234567890");

        Owner result = mapper.map(owner, request);

        assertThat(result).isSameAs(owner);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getAddress()).isEqualTo("123 Main St");
        assertThat(result.getCity()).isEqualTo("Springfield");
        assertThat(result.getTelephone()).isEqualTo("1234567890");
    }

    @Test
    void shouldOverwriteExistingValues() {
        Owner owner = new Owner();
        owner.setFirstName("Old");
        owner.setLastName("Name");
        owner.setAddress("Old Addr");
        owner.setCity("Old City");
        owner.setTelephone("0000000000");

        OwnerRequest request = new OwnerRequest("New", "Person", "New Addr", "New City", "9999999999");

        Owner result = mapper.map(owner, request);

        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getLastName()).isEqualTo("Person");
        assertThat(result.getAddress()).isEqualTo("New Addr");
        assertThat(result.getCity()).isEqualTo("New City");
        assertThat(result.getTelephone()).isEqualTo("9999999999");
    }
}
