package com.alphashop.articles_web_service.test;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

//TODO: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
public class AlphashopPostgreSqlContainer extends PostgreSQLContainer<AlphashopPostgreSqlContainer> {

	private static final String IMAGE_VERSION = "postgres:16";
    private static AlphashopPostgreSqlContainer container;

    private AlphashopPostgreSqlContainer() {
        super(IMAGE_VERSION);
        withUsername("postgres");
        withPassword("pass123");
        withDatabaseName("alphashop");
    }

    public static AlphashopPostgreSqlContainer getInstance() {
        if (container == null) {
            container = new AlphashopPostgreSqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
    
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + container.getJdbcUrl(),
                    "spring.datasource.username=" + container.getUsername(),
                    "spring.datasource.password=" + container.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
