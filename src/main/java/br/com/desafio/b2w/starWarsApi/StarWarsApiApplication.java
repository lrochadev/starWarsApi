package br.com.desafio.b2w.starWarsApi;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * 
 * @author Leonardo Rocha
 *
 */
@SpringBootApplication
@EnableMongoRepositories(value = "br.com.desafio.b2w.starWarsApi.repository", considerNestedRepositories = true)
@EnableMongoAuditing
public class StarWarsApiApplication {

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		Properties properties = new Properties();
		properties.setProperty("fileEncodings", "UTF-8");
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFileEncodings(properties);

		return messageSource;
	}

	@Bean
	public LocalValidatorFactoryBean localValidatorFactoryBean() {
		LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
		validatorFactoryBean.setValidationMessageSource(messageSource());
		return validatorFactoryBean;
	}

	public static void main(String[] args) {
		SpringApplication.run(StarWarsApiApplication.class, args);
	}
}
