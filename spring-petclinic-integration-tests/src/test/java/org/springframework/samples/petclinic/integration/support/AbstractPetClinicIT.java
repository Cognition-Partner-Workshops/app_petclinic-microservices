package org.springframework.samples.petclinic.integration.support;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractPetClinicIT {

    protected static PetClinicEnvironment environment;

    @BeforeAll
    static void startEnvironment() {
        environment = PetClinicEnvironment.getInstance();
    }

    protected static RequestSpecification gateway() {
        return environment.gateway();
    }

    protected static RequestSpecification customersService() {
        return environment.customersService();
    }

    protected static RequestSpecification visitsService() {
        return environment.visitsService();
    }
}
