package org.springframework.samples.petclinic.visits.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricConfigTest {

    private final MetricConfig config = new MetricConfig();

    @Test
    void metricsCommonTagsReturnsCustomizer() {
        var customizer = config.metricsCommonTags();
        assertThat(customizer).isNotNull();

        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        customizer.customize(registry);

        registry.counter("test").increment();
        assertThat(registry.find("test").counter().getId().getTags())
            .anyMatch(tag -> tag.getKey().equals("application") && tag.getValue().equals("petclinic"));
    }

    @Test
    void timedAspectReturnsInstance() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        TimedAspect aspect = config.timedAspect(registry);
        assertThat(aspect).isNotNull();
    }
}
