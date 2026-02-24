package org.springframework.samples.petclinic.jhipster.web;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.jhipster.model.User;
import org.springframework.samples.petclinic.jhipster.model.UserRepository;
import org.springframework.samples.petclinic.jhipster.security.SecurityUtils;
import org.springframework.samples.petclinic.jhipster.service.dto.AdminUserDTO;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@Timed("petclinic.jhipster.account")
class AccountResource {

    private static final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    AccountResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@code GET /account} : get the current user.
     *
     * @return the current user.
     * @throws ResourceNotFoundException if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new ResourceNotFoundException("User could not be found"));
    }

    /**
     * {@code POST /account} : update the current user information.
     *
     * @param userDTO the current user information.
     */
    @PostMapping("/account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new ResourceNotFoundException("Current user login not found"));

        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User could not be found");
        }

        User existingUser = user.get();
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        existingUser.setLangKey(userDTO.getLangKey());
        existingUser.setImageUrl(userDTO.getImageUrl());
        userRepository.save(existingUser);
        log.debug("Changed Information for User: {}", existingUser);
    }
}
