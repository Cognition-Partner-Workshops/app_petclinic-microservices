package org.springframework.samples.petclinic.jhipster.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.jhipster.security.SecurityUtils;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * JWT encoder/decoder configuration.
 * Uses a shared secret (HMAC-SHA512) for signing and verifying tokens.
 */
@Configuration
public class SecurityJwtConfiguration {

    @Value("${jhipster.security.authentication.jwt.base64-secret:}")
    private String jwtBase64Secret;

    @Value("${jhipster.security.authentication.jwt.secret:}")
    private String jwtSecret;

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
            .macAlgorithm(SecurityUtils.JWT_ALGORITHM)
            .build();
        return jwtDecoder;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes;
        if (!jwtBase64Secret.isEmpty()) {
            keyBytes = Base64.from(jwtBase64Secret).decode();
        } else {
            keyBytes = jwtSecret.getBytes();
        }
        return new SecretKeySpec(keyBytes, SecurityUtils.JWT_ALGORITHM.getName());
    }
}
