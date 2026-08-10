package org.springframework.samples.petclinic.vets.system;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetsPropertiesTest {

    @Test
    void shouldExposeCacheSettings() {
        VetsProperties properties = new VetsProperties(new VetsProperties.Cache(10, 100));

        assertThat(properties.cache().ttl()).isEqualTo(10);
        assertThat(properties.cache().heapSize()).isEqualTo(100);
    }

    @Test
    void shouldAllowAbsentCacheConfiguration() {
        assertThat(new VetsProperties(null).cache()).isNull();
    }
}
