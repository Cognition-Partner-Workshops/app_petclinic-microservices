package org.springframework.samples.petclinic.customers.web.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.OwnerRequest;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerEntityMapperTest {

    private final OwnerEntityMapper mapper = new OwnerEntityMapper();

    @Test
    void shouldCopyAllRequestFieldsOntoOwner() {
        OwnerRequest request = new OwnerRequest("George", "Bush", "Rue de la Paix", "Paris", "0123456789");

        Owner owner = mapper.map(new Owner(), request);

        assertThat(owner.getFirstName()).isEqualTo("George");
        assertThat(owner.getLastName()).isEqualTo("Bush");
        assertThat(owner.getAddress()).isEqualTo("Rue de la Paix");
        assertThat(owner.getCity()).isEqualTo("Paris");
        assertThat(owner.getTelephone()).isEqualTo("0123456789");
    }

    @Test
    void shouldOverwriteExistingValuesWithNulls() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setCity("Paris");

        mapper.map(owner, new OwnerRequest(null, null, null, null, null));

        assertThat(owner.getFirstName()).isNull();
        assertThat(owner.getCity()).isNull();
    }
}
