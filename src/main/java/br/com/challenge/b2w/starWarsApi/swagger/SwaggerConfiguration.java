package br.com.challenge.b2w.starWarsApi.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Leonardo Rocha
 */
@Configuration
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi starWarsApi() {
        return GroupedOpenApi.builder()
                .group("br.com.challenge.b2w.starWarsApi.resources")
                .pathsToMatch("/api/planets/**")
                .build();
    }

    @Bean
    public OpenAPI metaData() {
        return new OpenAPI()
                .info(new Info().title("Star Wars API")
                        .description("API that contains planet data.")
                        .version("v1.0")
                        .license(new License().name("Apache License Version 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
