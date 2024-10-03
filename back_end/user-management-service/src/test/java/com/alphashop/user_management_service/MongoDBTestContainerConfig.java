package com.alphashop.user_management_service;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

@ActiveProfiles("test")
@Configuration
@EnableMongoRepositories
public class MongoDBTestContainerConfig {
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7")
            .withExposedPorts(27017);

    static {
        mongoDBContainer.start();
        var mappedPort = mongoDBContainer.getMappedPort(27017);
        System.setProperty("mongodb.container.port", String.valueOf(mappedPort));
    }
}