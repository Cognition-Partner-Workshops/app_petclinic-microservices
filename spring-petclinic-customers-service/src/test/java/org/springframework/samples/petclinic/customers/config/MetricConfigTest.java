package org.springframework.samples.petclinic.customers.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;

import static org.assertj.core.api.Assertions.assertThat;

class MetricConfigTest {

    private final MetricConfig config = new MetricConfig();

    @Test
    void metricsCommonTagsReturnsCustomizer() {
        MeterRegistryCustomizer<MeterRegistry> customizer = config.metricsCommonTags();
        assertThat(customizer).isNotNull();

        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        customizer.customize(registry);
        assertThat(registry.config().toString()).isNotNull();
    }

    @Test
    void timedAspectReturnsBeanInstance() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        TimedAspect aspect = config.timedAspect(registry);
        assertThat(aspect).isNotNull();
    }
}
