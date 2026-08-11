package org.springframework.samples.petclinic.integration.support;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class PetClinicFixtures {

    private PetClinicFixtures() {
    }

    public static Map<String, Object> ownerRequest(String lastName) {
        Map<String, Object> owner = new LinkedHashMap<>();
        owner.put("firstName", "Integration");
        owner.put("lastName", lastName);
        owner.put("address", "1 Compose Street");
        owner.put("city", "Testville");
        owner.put("telephone", randomTelephone());
        return owner;
    }

    public static Map<String, Object> petRequest(String name, String birthDate, int typeId) {
        Map<String, Object> pet = new LinkedHashMap<>();
        // PetRequest.id is a primitive, the service rejects the payload without it
        pet.put("id", 0);
        pet.put("name", name);
        pet.put("birthDate", birthDate);
        pet.put("typeId", typeId);
        return pet;
    }

    public static Map<String, Object> visitRequest(String date, String description) {
        Map<String, Object> visit = new LinkedHashMap<>();
        visit.put("date", date);
        visit.put("description", description);
        return visit;
    }

    private static String randomTelephone() {
        return String.valueOf(ThreadLocalRandom.current().nextLong(6_000_000_000L, 6_999_999_999L));
    }
}
