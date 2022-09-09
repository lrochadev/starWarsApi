package br.com.challenge.b2w.starWarsApi;

import br.com.challenge.b2w.starWarsApi.swagger.RetryHandlerConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

/**
 * @author Leonardo Rocha
 */
@SpringBootApplication
@EnableMongoRepositories(value = "br.com.challenge.b2w.starWarsApi.repository", considerNestedRepositories = true)
@EnableMongoAuditing
public class StarWarsApiApplication {

    @Value("${restTemplate.pool.connectionRequestTimeout}")
    private int connectionRequestTimeout;

    @Value("${restTemplate.pool.connectionTimeout}")
    private int connectTimeout;

    @Value("${restTemplate.pool.socketTimeout}")
    private int socketTimeout;

    @Value("${restTemplate.pool.maxConnections}")
    private int maxConnections;

    @Value("${restTemplate.pool.maxPerRoute}")
    private int maxPerRoute;

    @Value("${restTemplate.pool.validateAfterInactivity}")
    private int validateAfterInactivity;

    @Value("${restTemplate.pool.retryCount}")
    private int retryCount;

    @Value("${restTemplate.pool.retrySleepTimeMS}")
    private int retrySleepTimeMS;

    public static void main(String[] args) {
        SpringApplication.run(StarWarsApiApplication.class, args);
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
    public ObjectMapper objectMapper() {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true).dateFormat(StdDateFormat.getDateInstance()).failOnEmptyBeans(true).failOnUnknownProperties(true);
        return builder.build().registerModule(new JavaTimeModule()).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public RestTemplate restTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        final RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.getMessageConverters().add(0, mappingJackson2HttpMessageConverter);
        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout).build();

        final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(maxConnections);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
        poolingHttpClientConnectionManager.setValidateAfterInactivity(validateAfterInactivity);

        final CloseableHttpClient httpClientBuilder = HttpClientBuilder.create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setRetryHandler(new RetryHandlerConfiguration(retryCount, retrySleepTimeMS))
                .setDefaultRequestConfig(requestConfig)
                .build();

        requestFactory.setHttpClient(httpClientBuilder);

        return new BufferingClientHttpRequestFactory(requestFactory);
    }

}
