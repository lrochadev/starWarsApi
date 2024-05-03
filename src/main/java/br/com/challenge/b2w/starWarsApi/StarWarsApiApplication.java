package br.com.challenge.b2w.starWarsApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author Leonardo Rocha
 */
@SpringBootApplication
@EnableMongoRepositories(value = "br.com.challenge.b2w.starWarsApi.repository", considerNestedRepositories = true)
@EnableMongoAuditing
public class StarWarsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarWarsApiApplication.class, args);
    }
}
