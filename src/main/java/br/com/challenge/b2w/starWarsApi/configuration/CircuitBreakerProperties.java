package br.com.challenge.b2w.starWarsApi.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "swapi.circuit-breaker")
public class CircuitBreakerProperties {

    @NotNull
    private int failureRateThreshold = 30;

    @NotNull
    private long slowCallDurationThresholdMs = 800;

    @NotNull
    private int slowCallRateThreshold = 50;

    @NotNull
    private long waitDurationInOpenStateSeconds = 60;

    @NotNull
    private int slidingWindowSize = 10;

    @NotNull
    private int minimumNumberOfCalls = 3;

    @NotNull
    private int permittedNumberOfCallsInHalfOpenState = 3;
}
