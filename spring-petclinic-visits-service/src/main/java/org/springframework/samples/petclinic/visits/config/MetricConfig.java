package org.springframework.samples.petclinic.visits.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer metrics configuration for the Visits microservice.
 * <p>
 * Applies a common {@code application=petclinic} tag to all emitted metrics so
 * they can be easily filtered in monitoring dashboards. Also enables the
 * {@link TimedAspect} to honour {@code @Timed} annotations on Spring beans.
 */
@Configuration
public class MetricConfig {

  /**
   * Registers a common tag ({@code application=petclinic}) on every meter
   * published by this service.
   *
   * @return a {@link MeterRegistryCustomizer} that applies the common tag
   */
  @Bean
  MeterRegistryCustomizer<@NonNull MeterRegistry> metricsCommonTags() {
    return registry -> registry.config().commonTags("application", "petclinic");
  }

  /**
   * Enables AOP-based method timing via Micrometer's {@link TimedAspect},
   * allowing {@code @Timed} annotations to record method execution durations.
   *
   * @param registry the meter registry to bind the aspect to
   * @return a configured {@link TimedAspect} instance
   */
  @Bean
  TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

}
