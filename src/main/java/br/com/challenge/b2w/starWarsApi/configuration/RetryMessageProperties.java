package br.com.challenge.b2w.starWarsApi.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "resttemplate.pool")
public class RetryMessageProperties {

    @NotNull
    private Integer connectionRequestTimeout;

    @NotNull
    private int connectTimeout;

    @NotNull
    private int socketTimeout;

    @NotNull
    private int maxConnections;

    @NotNull
    private int maxPerRoute;

    @NotNull
    private int validateAfterInactivity;

    @NotNull
    private int retryCount;

    @NotNull
    private int retrySleepTimeMS;


}
