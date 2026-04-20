package br.com.challenge.b2w.starWarsApi.configuration;

import br.com.challenge.b2w.starWarsApi.infrastructure.RetryHandlerConfiguration;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(RetryMessageProperties.class)
public class ApplicationConfiguration {

    @Bean
    public CircuitBreaker swapiCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(30)
                .slowCallDurationThreshold(Duration.ofMillis(800))
                .slowCallRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(3)
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();
        return CircuitBreakerRegistry.of(config).circuitBreaker("swapi");
    }

    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        final Properties properties = new Properties();
        properties.setProperty("fileEncodings", "UTF-8");
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFileEncodings(properties);

        return messageSource;
    }

    @Bean
    public RestClient restClient(final RetryMessageProperties properties) {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory(properties))
                .configureMessageConverters(configurer -> configurer
                        .withJsonConverter(new JacksonJsonHttpMessageConverter())
                        .withStringConverter(new StringHttpMessageConverter(StandardCharsets.UTF_8)))
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(final RetryMessageProperties properties) {
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        final ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(properties.getConnectTimeout()))
                .setSocketTimeout(Timeout.ofMilliseconds(properties.getSocketTimeout()))
                .setValidateAfterInactivity(TimeValue.ofSeconds(properties.getValidateAfterInactivity()))
                .build();

        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(properties.getConnectionRequestTimeout()))
                .setResponseTimeout(Timeout.ofMilliseconds(properties.getSocketTimeout()))
                .build();

        final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(properties.getMaxConnections())
                .setMaxConnPerRoute(properties.getMaxPerRoute())
                .setDefaultConnectionConfig(connectionConfig)
                .build();

        final CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setRetryStrategy(new RetryHandlerConfiguration(properties.getRetryCount(), properties.getRetrySleepTimeMS()))
                .setDefaultRequestConfig(requestConfig)
                .build();

        requestFactory.setHttpClient(httpClient);

        return new BufferingClientHttpRequestFactory(requestFactory);
    }

}
