package br.com.challenge.b2w.starWarsApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Leonardo Rocha
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableMongoRepositories(value = "br.com.challenge.b2w.starWarsApi.repository", considerNestedRepositories = true)
@EnableMongoAuditing
public class StarWarsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarWarsApiApplication.class, args);
    }
}
