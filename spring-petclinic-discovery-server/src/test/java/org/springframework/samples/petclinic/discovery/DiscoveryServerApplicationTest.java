package org.springframework.samples.petclinic.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

class DiscoveryServerApplicationTest {

    @Test
    void mainStartsApplication() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            DiscoveryServerApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(DiscoveryServerApplication.class, new String[]{}));
        }
    }
}
