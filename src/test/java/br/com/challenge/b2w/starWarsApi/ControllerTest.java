package br.com.challenge.b2w.starWarsApi;

import br.com.challenge.b2w.starWarsApi.exception.StarWarsApiExceptionHandler;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.function.Consumer;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

public abstract class ControllerTest {

    private MockMvc mockMvc;

    private final ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver;

    protected final JsonMapper jsonMapper;

    protected ControllerTest() {

        final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);

        exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver() {
            @Override
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                final Method method = new ExceptionHandlerMethodResolver(StarWarsApiExceptionHandler.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new StarWarsApiExceptionHandler(), method);
            }
        };

        exceptionHandlerExceptionResolver.setMessageConverters(Collections.singletonList(mappingJackson2HttpMessageConverter));
        exceptionHandlerExceptionResolver.afterPropertiesSet();

        final DefaultIndenter defaultIndenter = new DefaultIndenter("  ", "\n");

        final DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();
        defaultPrettyPrinter.indentArraysWith(defaultIndenter);
        defaultPrettyPrinter.indentObjectsWith(defaultIndenter);

        jsonMapper = JsonMapper.builder()
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

    protected void configure(final Object... controllers) {

        // TODO: Add setCustomArgumentResolvers and addInterceptors
        mockMvc = MockMvcBuilders
                .standaloneSetup(controllers)
                .setCustomArgumentResolvers()
                .addInterceptors()
                .setHandlerExceptionResolvers(exceptionHandlerExceptionResolver)
                .build();

    }

    protected void doMvc(final Consumer<MockMvc> consumer) {
        consumer.accept(mockMvc);
    }

}