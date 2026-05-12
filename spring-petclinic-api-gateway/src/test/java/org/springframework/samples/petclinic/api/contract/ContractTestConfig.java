package org.springframework.samples.petclinic.api.contract;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayClassPathWarningAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Minimal Spring configuration for consumer contract tests.
 * Excludes Gateway auto-configuration since these tests only need
 * the stub runner and a WebClient to verify contracts.
 */
@Configuration
@EnableAutoConfiguration(exclude = {
    GatewayAutoConfiguration.class,
    GatewayClassPathWarningAutoConfiguration.class,
    GatewayReactiveLoadBalancerClientAutoConfiguration.class
})
class ContractTestConfig {
}
