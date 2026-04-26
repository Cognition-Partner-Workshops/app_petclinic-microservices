package org.springframework.samples.petclinic.api;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class ApiGatewayApplicationBeanTest {

    private final ApiGatewayApplication app = new ApiGatewayApplication();

    @Test
    void shouldCreateLoadBalancedRestTemplate() {
        RestTemplate template = app.loadBalancedRestTemplate();
        assertThat(template).isNotNull();
    }

    @Test
    void shouldCreateLoadBalancedWebClientBuilder() {
        WebClient.Builder builder = app.loadBalancedWebClientBuilder();
        assertThat(builder).isNotNull();
    }

    @Test
    void shouldCreateDefaultCustomizer() {
        Customizer<ReactiveResilience4JCircuitBreakerFactory> customizer = app.defaultCustomizer();
        assertThat(customizer).isNotNull();
    }
}
