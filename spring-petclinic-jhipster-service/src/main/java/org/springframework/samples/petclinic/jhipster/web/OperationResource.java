package org.springframework.samples.petclinic.jhipster.web;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.jhipster.model.Operation;
import org.springframework.samples.petclinic.jhipster.model.OperationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST controller for managing {@link Operation}.
 */
@RestController
@RequestMapping("/api/operations")
@Transactional
@Timed("petclinic.jhipster.operation")
class OperationResource {

    private static final Logger log = LoggerFactory.getLogger(OperationResource.class);

    private final OperationRepository operationRepository;

    OperationResource(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    /**
     * {@code POST  /operations} : Create a new operation.
     */
    @PostMapping("")
    public ResponseEntity<Operation> createOperation(@Valid @RequestBody Operation operation) throws URISyntaxException {
        log.debug("REST request to save Operation : {}", operation);
        if (operation.getId() != null) {
            throw new BadRequestException("A new operation cannot already have an ID", "operation", "idexists");
        }
        operation = operationRepository.save(operation);
        return ResponseEntity.created(new URI("/api/operations/" + operation.getId())).body(operation);
    }

    /**
     * {@code PUT  /operations/:id} : Updates an existing operation.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Operation> updateOperation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Operation operation
    ) {
        log.debug("REST request to update Operation : {}, {}", id, operation);
        if (operation.getId() == null) {
            throw new BadRequestException("Invalid id", "operation", "idnull");
        }
        if (!Objects.equals(id, operation.getId())) {
            throw new BadRequestException("Invalid ID", "operation", "idinvalid");
        }
        if (!operationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Operation " + id + " not found");
        }
        operation = operationRepository.save(operation);
        return ResponseEntity.ok().body(operation);
    }

    /**
     * {@code PATCH  /operations/:id} : Partial updates given fields of an existing operation.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Operation> partialUpdateOperation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Operation operation
    ) {
        log.debug("REST request to partial update Operation partially : {}, {}", id, operation);
        if (operation.getId() == null) {
            throw new BadRequestException("Invalid id", "operation", "idnull");
        }
        if (!Objects.equals(id, operation.getId())) {
            throw new BadRequestException("Invalid ID", "operation", "idinvalid");
        }
        if (!operationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Operation " + id + " not found");
        }

        Optional<Operation> result = operationRepository
            .findById(operation.getId())
            .map(existingOperation -> {
                updateIfPresent(existingOperation::setDate, operation.getDate());
                updateIfPresent(existingOperation::setDescription, operation.getDescription());
                updateIfPresent(existingOperation::setAmount, operation.getAmount());
                return existingOperation;
            })
            .map(operationRepository::save);

        return result.map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("Operation " + id + " not found"));
    }

    /**
     * {@code GET  /operations} : get all the operations with pagination.
     */
    @GetMapping("")
    public ResponseEntity<List<Operation>> getAllOperations(
        Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Operations");
        Page<Operation> page;
        if (eagerload) {
            page = operationRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = operationRepository.findAll(pageable);
        }
        HttpHeaders headers = generatePaginationHttpHeaders(page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /operations/:id} : get the "id" operation.
     */
    @GetMapping("/{id}")
    public Operation getOperation(@PathVariable("id") Long id) {
        log.debug("REST request to get Operation : {}", id);
        return operationRepository.findOneWithEagerRelationships(id)
            .orElseThrow(() -> new ResourceNotFoundException("Operation " + id + " not found"));
    }

    /**
     * {@code DELETE  /operations/:id} : delete the "id" operation.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOperation(@PathVariable("id") Long id) {
        log.debug("REST request to delete Operation : {}", id);
        operationRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Generate pagination headers for a Spring Data Page.
     * Replaces JHipster's PaginationUtil with inline implementation.
     */
    private HttpHeaders generatePaginationHttpHeaders(Page<?> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        int totalPages = page.getTotalPages();

        UriComponentsBuilder baseUri = ServletUriComponentsBuilder.fromCurrentRequest();
        StringBuilder linkHeader = new StringBuilder();

        if (pageNumber < totalPages - 1) {
            linkHeader.append(buildLinkHeader(baseUri, pageNumber + 1, pageSize, "next"));
        }
        if (pageNumber > 0) {
            if (linkHeader.length() > 0) linkHeader.append(", ");
            linkHeader.append(buildLinkHeader(baseUri, pageNumber - 1, pageSize, "prev"));
        }
        // Last and first
        if (linkHeader.length() > 0) linkHeader.append(", ");
        linkHeader.append(buildLinkHeader(baseUri, totalPages - 1, pageSize, "last"));
        linkHeader.append(", ");
        linkHeader.append(buildLinkHeader(baseUri, 0, pageSize, "first"));

        headers.add(HttpHeaders.LINK, linkHeader.toString());
        return headers;
    }

    private String buildLinkHeader(UriComponentsBuilder baseUri, int page, int size, String rel) {
        return "<" + baseUri.cloneBuilder()
            .replaceQueryParam("page", page)
            .replaceQueryParam("size", size)
            .build().toUriString() + ">; rel=\"" + rel + "\"";
    }
}
