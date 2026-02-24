package org.springframework.samples.petclinic.jhipster.web;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.jhipster.model.Authority;
import org.springframework.samples.petclinic.jhipster.model.AuthorityRepository;
import org.springframework.samples.petclinic.jhipster.model.User;
import org.springframework.samples.petclinic.jhipster.model.UserRepository;
import org.springframework.samples.petclinic.jhipster.security.AuthoritiesConstants;
import org.springframework.samples.petclinic.jhipster.service.dto.AdminUserDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST controller for managing users (admin-only).
 *
 * <p>This class accesses the {@link User} entity, and needs to fetch its collection of authorities.</p>
 */
@RestController
@RequestMapping("/api/admin")
@Timed("petclinic.jhipster.admin-user")
class UserResource {

    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    UserResource(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@code POST /admin/users} : Creates a new user.
     */
    @PostMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdminUserDTO> createUser(@Valid @RequestBody AdminUserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestException("A new user cannot already have an ID", "userManagement", "idexists");
        }

        // Check for existing login
        if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new BadRequestException("Login name already used", "userManagement", "userexists");
        }
        // Check for existing email
        if (userDTO.getEmail() != null && userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email is already in use", "userManagement", "emailexists");
        }

        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        user.setLangKey(userDTO.getLangKey() != null ? userDTO.getLangKey() : "en");
        // Set a temporary password
        String encryptedPassword = passwordEncoder.encode("changeit");
        user.setPassword(encryptedPassword);
        user.setActivated(true);

        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }

        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        AdminUserDTO result = new AdminUserDTO(user);
        return ResponseEntity.created(new URI("/api/admin/users/" + user.getLogin())).body(result);
    }

    /**
     * {@code PUT /admin/users} : Updates an existing User.
     */
    @PutMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AdminUserDTO> updateUser(@Valid @RequestBody AdminUserDTO userDTO) {
        log.debug("REST request to update User : {}", userDTO);

        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userDTO.getId())) {
            throw new BadRequestException("Email is already in use", "userManagement", "emailexists");
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userDTO.getId())) {
            throw new BadRequestException("Login name already used", "userManagement", "userexists");
        }

        Optional<AdminUserDTO> updatedUser = userRepository
            .findById(userDTO.getId())
            .map(user -> {
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                if (userDTO.getAuthorities() != null) {
                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    userDTO.getAuthorities().stream()
                        .map(authorityRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(managedAuthorities::add);
                }
                userRepository.save(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(AdminUserDTO::new);

        return updatedUser.map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * {@code GET /admin/users} : get all users with all the details.
     */
    @GetMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<AdminUserDTO>> getAllUsers(Pageable pageable) {
        log.debug("REST request to get all User for an admin");
        final Page<AdminUserDTO> page = userRepository.findAll(pageable).map(AdminUserDTO::new);
        HttpHeaders headers = generatePaginationHttpHeaders(page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET /admin/users/:login} : get the "login" user.
     */
    @GetMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public AdminUserDTO getUser(@PathVariable("login") String login) {
        log.debug("REST request to get User : {}", login);
        return userRepository.findOneWithAuthoritiesByLogin(login)
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("User " + login + " not found"));
    }

    /**
     * {@code DELETE /admin/users/:login} : delete the "login" User.
     */
    @DeleteMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("login") String login) {
        log.debug("REST request to delete User: {}", login);
        userRepository.findOneByLogin(login).ifPresent(user -> {
            userRepository.delete(user);
            log.debug("Deleted User: {}", user);
        });
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
