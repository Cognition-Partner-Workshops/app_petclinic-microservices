package org.springframework.samples.petclinic.customers.web.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.OwnerRequest;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerEntityMapperTest {

    private final OwnerEntityMapper mapper = new OwnerEntityMapper();

    @Test
    void shouldMapAllFieldsFromRequestToOwner() {
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");

        Owner result = mapper.map(owner, request);

        assertThat(result).isSameAs(owner);
        assertThat(result.getFirstName()).isEqualTo("George");
        assertThat(result.getLastName()).isEqualTo("Franklin");
        assertThat(result.getAddress()).isEqualTo("110 W. Liberty St.");
        assertThat(result.getCity()).isEqualTo("Madison");
        assertThat(result.getTelephone()).isEqualTo("6085551023");
    }

    @Test
    void shouldOverwriteExistingOwnerFields() {
        Owner owner = new Owner();
        owner.setFirstName("Old");
        owner.setLastName("Name");
        owner.setAddress("Old Address");
        owner.setCity("Old City");
        owner.setTelephone("000");

        OwnerRequest request = new OwnerRequest("New", "Name", "New Address", "New City", "111");

        Owner result = mapper.map(owner, request);

        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getLastName()).isEqualTo("Name");
        assertThat(result.getAddress()).isEqualTo("New Address");
        assertThat(result.getCity()).isEqualTo("New City");
        assertThat(result.getTelephone()).isEqualTo("111");
    }
}
