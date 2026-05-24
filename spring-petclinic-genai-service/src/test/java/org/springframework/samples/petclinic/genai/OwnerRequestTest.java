package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerRequestTest {

    @Test
    void recordAccessors() {
        OwnerRequest request = new OwnerRequest("George", "Franklin", "addr", "city", "6085551023");

        assertThat(request.firstName()).isEqualTo("George");
        assertThat(request.lastName()).isEqualTo("Franklin");
        assertThat(request.address()).isEqualTo("addr");
        assertThat(request.city()).isEqualTo("city");
        assertThat(request.telephone()).isEqualTo("6085551023");
    }
}
