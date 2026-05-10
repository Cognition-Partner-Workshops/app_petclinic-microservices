package org.springframework.samples.petclinic.vets.system;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetsPropertiesTest {

    @Test
    void shouldCreateWithCacheProperties() {
        VetsProperties.Cache cache = new VetsProperties.Cache(300, 100);
        VetsProperties props = new VetsProperties(cache);

        assertThat(props.cache()).isNotNull();
        assertThat(props.cache().ttl()).isEqualTo(300);
        assertThat(props.cache().heapSize()).isEqualTo(100);
    }
}
