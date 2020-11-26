package br.com.challenge.b2w.starWarsApi.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Leonardo Rocha
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket starWarsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.challenge.b2w.starWarsApi.resources"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .apiInfo(this.metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Star Wars API")
                .description("API that contains planet data.")
                .version("1.0")
                .contact(new Contact("Leonardo Rocha", "https://github.com/lrochadev", "lrochadev@gmail.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }
}
