package br.com.challenge.b2w.starWarsApi;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class ContainerBase {

    @Container
    @ServiceConnection
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:8.0")
            .withReuse(true);
}
