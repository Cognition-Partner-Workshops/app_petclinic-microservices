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
 * Entry point for the API Gateway microservice.
 * <p>
 * Acts as the single entry point for all client requests to the PetClinic system.
 * Routes incoming requests to the appropriate downstream microservices (customers,
 * visits, vets) and serves the Angular front-end as static content. Integrates with
 * Eureka for service discovery, provides load-balanced {@link WebClient} and
 * {@link RestTemplate} instances, and configures a Resilience4j circuit breaker
 * with a 10-second timeout to protect against downstream service failures.
 *
 * @author Maciej Szarlinski
 * @see org.springframework.cloud.client.discovery.EnableDiscoveryClient
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * Launches the API Gateway application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    /**
     * Creates a load-balanced {@link RestTemplate} that resolves service names
     * (e.g. {@code http://customers-service}) to actual host addresses via Eureka.
     *
     * @return a {@link RestTemplate} with client-side load balancing enabled
     */
    @Bean
    @LoadBalanced
    RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Creates a load-balanced {@link WebClient.Builder} for reactive, non-blocking
     * HTTP calls to downstream services discovered through Eureka.
     *
     * @return a {@link WebClient.Builder} with client-side load balancing enabled
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Value("classpath:/static/index.html")
    private Resource indexHtml;

    /**
     * Configures router functions to serve the Angular front-end static resources
     * and forward root requests ({@code /}) to {@code index.html}.
     * <p>
     * This is a workaround for Spring Boot not automatically forwarding to
     * {@code index.html} in reactive applications.
     *
     * @return a {@link RouterFunction} that serves static content and the SPA entry point
     * @see <a href="https://github.com/spring-projects/spring-boot/issues/9785">#9785</a>
     */
    @Bean
    RouterFunction<?> routerFunction() {
        RouterFunction router = RouterFunctions.resources("/**", new ClassPathResource("static/"))
            .andRoute(RequestPredicates.GET("/"),
                request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
        return router;
    }

    /**
     * Provides the default Resilience4j circuit breaker configuration for all reactive
     * circuit breakers in the gateway. Uses default circuit breaker settings and a
     * custom time limiter with a 10-second timeout to prevent long-running downstream
     * calls from blocking the gateway.
     *
     * @return a {@link Customizer} that applies default circuit breaker and time limiter settings
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
            .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10)).build())
            .build());
    }
}
