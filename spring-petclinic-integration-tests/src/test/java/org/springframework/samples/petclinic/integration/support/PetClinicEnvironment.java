package org.springframework.samples.petclinic.integration.support;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Owns the Docker Compose environment shared by all integration tests: it brings the
 * services up once per JVM, exposes their endpoints and allows individual services to be
 * stopped and restarted so that resilience behaviour can be exercised.
 * <p>
 * Configuration is done through system properties:
 * <ul>
 *     <li>{@code petclinic.compose.enabled} - set to {@code false} to test an already running environment</li>
 *     <li>{@code petclinic.compose.file} - path of the compose file to use</li>
 *     <li>{@code petclinic.compose.project} - compose project name</li>
 *     <li>{@code petclinic.compose.keepRunning} - set to {@code true} to keep the containers after the tests</li>
 *     <li>{@code petclinic.gateway.url}, {@code petclinic.customers.url}, {@code petclinic.visits.url}</li>
 * </ul>
 */
public final class PetClinicEnvironment {

    public static final String CUSTOMERS_SERVICE = "customers-service";

    public static final String VISITS_SERVICE = "visits-service";

    private static final Logger log = LoggerFactory.getLogger(PetClinicEnvironment.class);

    private static final Duration STARTUP_TIMEOUT = Duration.ofMinutes(6);

    private static final Duration ROUTING_TIMEOUT = Duration.ofMinutes(3);

    private static PetClinicEnvironment instance;

    private final boolean composeEnabled = Boolean.parseBoolean(System.getProperty("petclinic.compose.enabled", "true"));

    private final boolean keepRunning = Boolean.parseBoolean(System.getProperty("petclinic.compose.keepRunning", "false"));

    private final String composeFile = System.getProperty("petclinic.compose.file", "docker-compose-integration-tests.yml");

    private final String projectName = System.getProperty("petclinic.compose.project", "petclinic-it");

    private final String gatewayUrl = System.getProperty("petclinic.gateway.url", "http://localhost:8080");

    private final String customersUrl = System.getProperty("petclinic.customers.url", "http://localhost:8081");

    private final String visitsUrl = System.getProperty("petclinic.visits.url", "http://localhost:8082");

    private PetClinicEnvironment() {
    }

    public static synchronized PetClinicEnvironment getInstance() {
        if (instance == null) {
            // Assigned before starting so that a failed startup is reported once instead of
            // being retried by every test class
            instance = new PetClinicEnvironment();
            instance.start();
        }
        return instance;
    }

    public RequestSpecification gateway() {
        return specFor(gatewayUrl);
    }

    public RequestSpecification customersService() {
        return specFor(customersUrl);
    }

    public RequestSpecification visitsService() {
        return specFor(visitsUrl);
    }

    /**
     * Stops a service container, leaving the rest of the environment untouched. The
     * container is not removed so that {@link #startService(String)} can bring it back.
     */
    public void stopService(String service) {
        log.info("Stopping service {}", service);
        requireCompose();
        compose("stop", "--timeout", "10", service);
    }

    public void startService(String service) {
        log.info("Starting service {}", service);
        requireCompose();
        compose("up", "--detach", "--wait", "--wait-timeout", "300", service);
    }

    private void start() {
        if (!composeEnabled) {
            log.info("Compose management disabled, using the already running environment at {}", gatewayUrl);
            awaitGatewayRoutes();
            return;
        }
        log.info("Starting the PetClinic environment from {} (project {})", composeFile, projectName);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        compose("up", "--detach", "--wait", "--wait-timeout", String.valueOf(STARTUP_TIMEOUT.toSeconds()));
        awaitGatewayRoutes();
    }

    private void stop() {
        if (keepRunning) {
            log.info("petclinic.compose.keepRunning is set, leaving the environment running");
            return;
        }
        log.info("Stopping the PetClinic environment");
        compose("down", "--volumes", "--remove-orphans");
    }

    /**
     * The gateway discovers the services through Eureka, so its routes only work once the
     * registrations have propagated.
     */
    private void awaitGatewayRoutes() {
        await("gateway routes to be available")
            .atMost(ROUTING_TIMEOUT)
            .pollInterval(3, TimeUnit.SECONDS)
            .ignoreExceptions()
            .until(() -> gateway().get("/api/customer/owners").statusCode() == 200
                && gateway().get("/api/visit/owners/1/pets/1/visits").statusCode() == 200
                && gateway().get("/api/gateway/owners/1").statusCode() == 200);
        log.info("Gateway routes are available");
    }

    private RequestSpecification specFor(String baseUri) {
        return RestAssured.given(new RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build());
    }

    private void requireCompose() {
        if (!composeEnabled) {
            throw new IllegalStateException(
                "Service lifecycle control requires the compose environment (petclinic.compose.enabled=true)");
        }
    }

    private void compose(String... arguments) {
        List<String> command = new ArrayList<>(List.of("docker", "compose", "--file", composeFile, "--project-name", projectName));
        command.addAll(List.of(arguments));
        log.info("Running {}", String.join(" ", command));
        try {
            Process process = new ProcessBuilder(command)
                .directory(new File(composeFile).getAbsoluteFile().getParentFile())
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("Command " + String.join(" ", command) + " failed with exit code " + exitCode);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to run " + String.join(" ", command), e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while running " + String.join(" ", command), e);
        }
    }
}
