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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.jhipster.model.Label;
import org.springframework.samples.petclinic.jhipster.model.LabelRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Label}.
 */
@RestController
@RequestMapping("/api/labels")
@Transactional
@Timed("petclinic.jhipster.label")
class LabelResource {

    private static final Logger log = LoggerFactory.getLogger(LabelResource.class);

    private final LabelRepository labelRepository;

    LabelResource(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    /**
     * {@code POST  /labels} : Create a new label.
     */
    @PostMapping("")
    public ResponseEntity<Label> createLabel(@Valid @RequestBody Label label) throws URISyntaxException {
        log.debug("REST request to save Label : {}", label);
        if (label.getId() != null) {
            throw new BadRequestException("A new label cannot already have an ID", "label", "idexists");
        }
        label = labelRepository.save(label);
        return ResponseEntity.created(new URI("/api/labels/" + label.getId())).body(label);
    }

    /**
     * {@code PUT  /labels/:id} : Updates an existing label.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Label> updateLabel(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Label label
    ) {
        log.debug("REST request to update Label : {}, {}", id, label);
        if (label.getId() == null) {
            throw new BadRequestException("Invalid id", "label", "idnull");
        }
        if (!Objects.equals(id, label.getId())) {
            throw new BadRequestException("Invalid ID", "label", "idinvalid");
        }
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Label " + id + " not found");
        }
        label = labelRepository.save(label);
        return ResponseEntity.ok().body(label);
    }

    /**
     * {@code PATCH  /labels/:id} : Partial updates given fields of an existing label.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Label> partialUpdateLabel(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Label label
    ) {
        log.debug("REST request to partial update Label partially : {}, {}", id, label);
        if (label.getId() == null) {
            throw new BadRequestException("Invalid id", "label", "idnull");
        }
        if (!Objects.equals(id, label.getId())) {
            throw new BadRequestException("Invalid ID", "label", "idinvalid");
        }
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Label " + id + " not found");
        }

        Optional<Label> result = labelRepository
            .findById(label.getId())
            .map(existingLabel -> {
                updateIfPresent(existingLabel::setLabel, label.getLabel());
                return existingLabel;
            })
            .map(labelRepository::save);

        return result.map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("Label " + id + " not found"));
    }

    /**
     * {@code GET  /labels} : get all the labels.
     */
    @GetMapping("")
    public List<Label> getAllLabels() {
        log.debug("REST request to get all Labels");
        return labelRepository.findAll();
    }

    /**
     * {@code GET  /labels/:id} : get the "id" label.
     */
    @GetMapping("/{id}")
    public Label getLabel(@PathVariable("id") Long id) {
        log.debug("REST request to get Label : {}", id);
        return labelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Label " + id + " not found"));
    }

    /**
     * {@code DELETE  /labels/:id} : delete the "id" label.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable("id") Long id) {
        log.debug("REST request to delete Label : {}", id);
        labelRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
