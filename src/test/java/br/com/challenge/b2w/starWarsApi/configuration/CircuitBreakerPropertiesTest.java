package br.com.challenge.b2w.starWarsApi.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class CircuitBreakerPropertiesTest {

    @Autowired
    private CircuitBreakerProperties props;

    @Test
    void allPropertiesAreBound() {
        assertThat(props.getFailureRateThreshold()).isEqualTo(30);
        assertThat(props.getSlowCallDurationThresholdMs()).isEqualTo(800);
        assertThat(props.getSlowCallRateThreshold()).isEqualTo(50);
        assertThat(props.getWaitDurationInOpenStateSeconds()).isEqualTo(60);
        assertThat(props.getSlidingWindowSize()).isEqualTo(10);
        assertThat(props.getMinimumNumberOfCalls()).isEqualTo(3);
        assertThat(props.getPermittedNumberOfCallsInHalfOpenState()).isEqualTo(3);
    }
}
