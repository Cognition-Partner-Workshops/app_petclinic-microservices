package org.springframework.samples.petclinic.customers.web;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body record for creating or updating a pet owner.
 * <p>
 * All fields are validated: names, address, and city must be non-blank,
 * and the telephone number must contain only digits (up to 12).
 *
 * @param firstName the owner's first name (required)
 * @param lastName  the owner's last name (required)
 * @param address   the owner's street address (required)
 * @param city      the owner's city (required)
 * @param telephone the owner's telephone number — digits only, up to 12 characters (required)
 */
public record OwnerRequest(@NotBlank String firstName,
                           @NotBlank String lastName,
                           @NotBlank String address,
                           @NotBlank String city,
                           @NotBlank
                           @Digits(fraction = 0, integer = 12)
                           String telephone
) {
}
