package org.springframework.samples.petclinic.vets;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

class VetsServiceApplicationTest {

    @Test
    void mainStartsApplication() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            VetsServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(VetsServiceApplication.class, new String[]{}));
        }
    }
}
