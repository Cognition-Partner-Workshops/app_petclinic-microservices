package org.springframework.samples.petclinic.customers.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer metrics configuration for the Customers microservice.
 * <p>
 * Applies a common {@code application=petclinic} tag to all metrics emitted by this
 * service, making it easy to filter and aggregate metrics across the PetClinic system.
 * Also enables {@link Timed @Timed} annotation support via a {@link TimedAspect} bean.
 */
@Configuration
public class MetricConfig {

  /**
   * Registers a {@link MeterRegistryCustomizer} that adds a common
   * {@code application=petclinic} tag to every metric in the registry.
   *
   * @return a customizer that applies the common tag to the {@link MeterRegistry}
   */
  @Bean
  MeterRegistryCustomizer<@NonNull MeterRegistry> metricsCommonTags() {
      return registry -> registry.config().commonTags("application", "petclinic");
  }

  /**
   * Creates a {@link TimedAspect} bean that enables Micrometer's
   * {@link io.micrometer.core.annotation.Timed @Timed} annotation on Spring beans.
   *
   * @param registry the application's {@link MeterRegistry}
   * @return a configured {@link TimedAspect} instance
   */
  @Bean
  TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

}
