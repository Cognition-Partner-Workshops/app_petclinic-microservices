package org.springframework.samples.petclinic.customers.web.mapper;

import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.OwnerRequest;
import org.springframework.stereotype.Component;

/**
 * Maps {@link OwnerRequest} DTO fields onto an {@link Owner} JPA entity.
 * <p>
 * This manual mapping implementation is used for simplicity. In a production
 * application, a framework such as MapStruct could generate this code automatically.
 */
@Component
public class OwnerEntityMapper implements Mapper<OwnerRequest, Owner> {

    /**
     * Applies the owner request data to the given entity, updating all mutable fields
     * (address, city, telephone, first name, last name).
     *
     * @param owner   the target entity to populate
     * @param request the source request containing updated owner data
     * @return the updated {@link Owner} entity (same reference as {@code owner})
     */
    @Override
    public Owner map(final Owner owner, final OwnerRequest request) {
        owner.setAddress(request.address());
        owner.setCity(request.city());
        owner.setTelephone(request.telephone());
        owner.setFirstName(request.firstName());
        owner.setLastName(request.lastName());
        return owner;
    }
}
