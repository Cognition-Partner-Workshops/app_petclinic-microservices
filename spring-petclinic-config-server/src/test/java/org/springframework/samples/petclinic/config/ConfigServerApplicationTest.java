package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

class ConfigServerApplicationTest {

    @Test
    void mainStartsApplication() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            ConfigServerApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(ConfigServerApplication.class, new String[]{}));
        }
    }
}
