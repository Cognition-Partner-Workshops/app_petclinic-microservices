package org.springframework.samples.petclinic.vets.system;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CacheConfigTest {

    @Test
    void canInstantiate() {
        CacheConfig config = new CacheConfig();
        assertThat(config).isNotNull();
    }
}
