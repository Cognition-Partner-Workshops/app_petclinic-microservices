package org.springframework.samples.petclinic.jhipster.web;

import io.micrometer.core.annotation.Timed;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.jhipster.model.User;
import org.springframework.samples.petclinic.jhipster.model.UserRepository;
import org.springframework.samples.petclinic.jhipster.service.dto.UserDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST controller for managing users (public-facing, read-only).
 * Returns only public user information (id, login).
 */
@RestController
@RequestMapping("/api")
@Timed("petclinic.jhipster.public-user")
class PublicUserResource {

    private static final Logger log = LoggerFactory.getLogger(PublicUserResource.class);

    private final UserRepository userRepository;

    PublicUserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@code GET /users} : get all users with only public information - calling this method is allowed for anyone.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllPublicUsers(Pageable pageable) {
        log.debug("REST request to get all public User names");
        final Page<UserDTO> page = userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
        HttpHeaders headers = generatePaginationHttpHeaders(page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/authorities")
    public List<String> getAuthorities() {
        // Return the standard JHipster authorities
        return List.of("ROLE_ADMIN", "ROLE_USER");
    }

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
