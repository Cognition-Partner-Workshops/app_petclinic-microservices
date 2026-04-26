package org.springframework.samples.petclinic.vets.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("production")
class CacheConfigTest {

    @Autowired
    CacheManager cacheManager;

    @Test
    void shouldLoadCacheConfig() {
        assertThat(cacheManager).isNotNull();
    }
}
