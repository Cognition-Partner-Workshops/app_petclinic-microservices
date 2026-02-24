package org.springframework.samples.petclinic.jhipster.web;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.jhipster.model.Authority;
import org.springframework.samples.petclinic.jhipster.model.AuthorityRepository;
import org.springframework.samples.petclinic.jhipster.security.AuthoritiesConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Authority}.
 */
@RestController
@RequestMapping("/api/authorities")
@Transactional
@Timed("petclinic.jhipster.authority")
class AuthorityResource {

    private static final Logger log = LoggerFactory.getLogger(AuthorityResource.class);

    private final AuthorityRepository authorityRepository;

    AuthorityResource(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    /**
     * {@code POST  /authorities} : Create a new authority.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Authority> createAuthority(@Valid @RequestBody Authority authority) throws URISyntaxException {
        log.debug("REST request to save Authority : {}", authority);
        if (authorityRepository.existsById(authority.getName())) {
            throw new BadRequestException("Authority already exists", "authority", "idexists");
        }
        authority = authorityRepository.save(authority);
        return ResponseEntity.created(new URI("/api/authorities/" + authority.getName())).body(authority);
    }

    /**
     * {@code GET  /authorities} : get all the authorities.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<Authority> getAllAuthorities() {
        log.debug("REST request to get all Authorities");
        return authorityRepository.findAll();
    }

    /**
     * {@code GET  /authorities/:id} : get the "id" authority.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public Authority getAuthority(@PathVariable("id") String id) {
        log.debug("REST request to get Authority : {}", id);
        return authorityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Authority " + id + " not found"));
    }

    /**
     * {@code DELETE  /authorities/:id} : delete the "id" authority.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthority(@PathVariable("id") String id) {
        log.debug("REST request to delete Authority : {}", id);
        authorityRepository.deleteById(id);
    }
}
