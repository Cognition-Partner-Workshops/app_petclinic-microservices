package org.springframework.samples.petclinic.customers.web.mapper;

/**
 * Generic mapping interface for converting request objects into entity objects.
 * <p>
 * Implementations apply the fields from a request DTO ({@code R}) onto an existing
 * entity ({@code E}), supporting both creation (mapping onto a new entity) and
 * update (mapping onto an existing entity) scenarios.
 *
 * @param <R> the request/DTO type to map from
 * @param <E> the entity type to map onto
 */
public interface Mapper<R, E> {

    /**
     * Maps the fields from the given request onto the provided entity.
     *
     * @param entity  the target entity to populate
     * @param request the source request containing the data to apply
     * @return the populated entity (same reference as {@code entity})
     */
    E map(E entity, R request);
}
