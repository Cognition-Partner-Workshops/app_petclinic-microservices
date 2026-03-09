/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.api;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Duration;


/**
 * Main entry point for the Spring PetClinic API Gateway microservice.
 *
 * <p>This application serves as the single external entry point for the entire PetClinic
 * microservices architecture. It is responsible for:
 * <ul>
 *   <li>Routing incoming HTTP requests to the appropriate backend services
 *       (customers-service, visits-service, vets-service, genai-service) via
 *       Spring Cloud Gateway routes defined in {@code application.yml}.</li>
 *   <li>Service discovery integration through Eureka ({@code @EnableDiscoveryClient}),
 *       enabling load-balanced routing to dynamically registered service instances
 *       using {@code lb://service-name} URIs.</li>
 *   <li>Circuit breaking with Resilience4j to gracefully handle downstream service
 *       failures and prevent cascading outages.</li>
 *   <li>Serving the Angular single-page application (SPA) frontend from static
 *       resources bundled within this module.</li>
 * </ul>
 *
 * <p>The gateway listens on port <strong>8080</strong> by default and expects the
 * Config Server (port 8888) and Discovery Server (port 8761) to be available at startup.
 *
 * @author Maciej Szarlinski
 * @see org.springframework.samples.petclinic.api.boundary.web.ApiGatewayController
 * @see org.springframework.samples.petclinic.api.application.CustomersServiceClient
 * @see org.springframework.samples.petclinic.api.application.VisitsServiceClient
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * Application entry point. Bootstraps the Spring Boot application context,
     * which triggers auto-configuration, component scanning, and registration
     * with the Eureka discovery server.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    /**
     * Creates a {@link RestTemplate} bean with client-side load balancing enabled.
     *
     * <p>The {@code @LoadBalanced} annotation integrates with Spring Cloud's
     * service discovery so that requests using logical service names
     * (e.g., {@code http://customers-service/owners}) are automatically resolved
     * to actual host:port endpoints registered in Eureka.
     *
     * @return a load-balanced {@link RestTemplate} instance
     */
    @Bean
    @LoadBalanced
    RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Creates a reactive {@link WebClient.Builder} bean with client-side load balancing.
     *
     * <p>This builder is injected into reactive service clients such as
     * {@link org.springframework.samples.petclinic.api.application.CustomersServiceClient}
     * and {@link org.springframework.samples.petclinic.api.application.VisitsServiceClient}
     * to make non-blocking HTTP calls to backend services. The {@code @LoadBalanced}
     * annotation ensures that logical service names in URIs are resolved via Eureka.
     *
     * @return a load-balanced {@link WebClient.Builder} instance
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    /** Reference to the Angular SPA's {@code index.html}, loaded from the classpath. */
    @Value("classpath:/static/index.html")
    private Resource indexHtml;

    /**
     * Configures functional routing for serving the Angular single-page application.
     *
     * <p>This bean defines two routing rules:
     * <ol>
     *   <li>Serves all static resources (JS, CSS, images) from the {@code static/}
     *       classpath directory under the {@code /**} path pattern.</li>
     *   <li>Maps the root path ({@code GET /}) to return {@code index.html} with
     *       a {@code text/html} content type, enabling the SPA to handle
     *       client-side routing.</li>
     * </ol>
     *
     * <p>This is a workaround for a known Spring Boot issue where WebFlux-based
     * applications do not automatically forward root requests to {@code index.html}.
     *
     * @return a {@link RouterFunction} that serves static resources and the SPA entry page
     * @see <a href="https://github.com/spring-projects/spring-boot/issues/9785">spring-boot#9785</a>
     */
    @Bean
    RouterFunction<?> routerFunction() {
        RouterFunction router = RouterFunctions.resources("/**", new ClassPathResource("static/"))
            .andRoute(RequestPredicates.GET("/"),
                request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
        return router;
    }

    /**
     * Configures the default Resilience4j circuit breaker settings for all reactive
     * circuit breaker instances created by the gateway.
     *
     * <p>The configuration applies to every circuit breaker unless overridden on a
     * per-instance basis. It uses:
     * <ul>
     *   <li>{@link CircuitBreakerConfig#ofDefaults()} &mdash; default thresholds for
     *       failure rate, slow call rate, and sliding window size.</li>
     *   <li>A custom {@link TimeLimiterConfig} with a <strong>10-second</strong> timeout,
     *       after which the call is considered timed out and the circuit breaker may
     *       transition to an open state.</li>
     * </ul>
     *
     * <p>This configuration is used by the {@code ApiGatewayController} when invoking
     * downstream services through the {@code ReactiveCircuitBreakerFactory}.
     *
     * @return a {@link Customizer} that applies default circuit breaker configuration
     * @see org.springframework.samples.petclinic.api.boundary.web.ApiGatewayController
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
            .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10)).build())
            .build());
    }
}
