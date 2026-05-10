package org.springframework.samples.petclinic.visits.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;

import static org.assertj.core.api.Assertions.assertThat;

class MetricConfigTest {

    private final MetricConfig config = new MetricConfig();

    @Test
    void shouldCreateMeterRegistryCustomizer() {
        MeterRegistryCustomizer<MeterRegistry> customizer = config.metricsCommonTags();
        assertThat(customizer).isNotNull();

        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        customizer.customize(registry);
    }

    @Test
    void shouldCreateTimedAspect() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        TimedAspect timedAspect = config.timedAspect(registry);
        assertThat(timedAspect).isNotNull();
    }
}
