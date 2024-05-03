package br.com.challenge.b2w.starWarsApi.configuration;

import br.com.challenge.b2w.starWarsApi.infrastructure.RetryHandlerConfiguration;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static org.apache.hc.core5.util.Timeout.ofSeconds;

@Configuration
@EnableConfigurationProperties(RetryMessageProperties.class)
public class ApplicationConfiguration {

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
    public JsonMapper jsonMapper() {

        final DefaultIndenter defaultIndenter = new DefaultIndenter("  ", "\n");

        final DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();
        defaultPrettyPrinter.indentArraysWith(defaultIndenter);
        defaultPrettyPrinter.indentObjectsWith(defaultIndenter);

        return JsonMapper.builder()
                .defaultPrettyPrinter(defaultPrettyPrinter)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
                .disable(SerializationFeature.INDENT_OUTPUT)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .visibility(PropertyAccessor.GETTER, NONE)
                .visibility(PropertyAccessor.SETTER, NONE)
                .visibility(PropertyAccessor.FIELD, ANY)
                .addModule(new JavaTimeModule())
                .addModule(new SimpleModule())
                .build();
    }

    @Bean
    public RestTemplate restTemplate(final RetryMessageProperties properties) {
        final RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory(properties));
        restTemplate.setMessageConverters(messageConverters());
        return restTemplate;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(jsonMapper());
    }

    private List<HttpMessageConverter<?>> messageConverters() {
        final List<HttpMessageConverter<?>> messageConverters = new LinkedList<>();
        messageConverters.add(mappingJackson2HttpMessageConverter());
        messageConverters.add(stringHttpMessageConverter());
        return messageConverters;
    }

    private StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(final RetryMessageProperties properties) {
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(ofSeconds(properties.getConnectionRequestTimeout()))
                .setConnectTimeout(ofSeconds(5000))
                .setResponseTimeout(ofSeconds(properties.getSocketTimeout()))
                .build();

        final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(properties.getMaxConnections());
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(properties.getMaxPerRoute());
        poolingHttpClientConnectionManager.setValidateAfterInactivity(TimeValue.ofSeconds(properties.getValidateAfterInactivity()));

        final CloseableHttpClient httpClientBuilder = HttpClientBuilder.create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setRetryStrategy(new RetryHandlerConfiguration(properties.getRetryCount(), properties.getRetrySleepTimeMS()))
                .setDefaultRequestConfig(requestConfig)
                .build();

        requestFactory.setHttpClient(httpClientBuilder);

        return new BufferingClientHttpRequestFactory(requestFactory);
    }

}
