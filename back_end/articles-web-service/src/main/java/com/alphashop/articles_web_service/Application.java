package com.alphashop.articles_web_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = { FlywayAutoConfiguration.class })
public class Application {

//	@Autowired
//	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@Bean
//	Flyway flyway() {
//		Flyway flyway = Flyway.configure()
//				.dataSource(dataSource(env))
//				.locations("classpath:db/migration")
//				.load();
//		return flyway;
//	}
//
//	@Bean
//	DataSource dataSource(Environment env) {
//		// Configure your DataSource based on your database type
//		// Here's an example for PostgreSQL:
//		HikariDataSource dataSource = new HikariDataSource();
//		dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
//		dataSource.setUsername(env.getProperty("spring.datasource.username"));
//		dataSource.setPassword(env.getProperty("spring.datasource.password"));
//
//		// Configure other DataSource settings
//		return dataSource;
//	}
}
