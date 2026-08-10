package org.springframework.samples.petclinic.visits.config;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricConfigTest {

    private final MetricConfig config = new MetricConfig();

    @Test
    void shouldTagEveryMeterWithTheApplicationName() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        config.metricsCommonTags().customize(registry);
        registry.counter("test.counter").increment();

        assertThat(registry.get("test.counter").counter().getId().getTag("application"))
            .isEqualTo("petclinic");
    }

    @Test
    void shouldProvideTimedAspect() {
        assertThat(config.timedAspect(new SimpleMeterRegistry())).isNotNull();
    }
}
