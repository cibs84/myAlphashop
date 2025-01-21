package com.alphashop.articles_web_service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import com.alphashop.articles_web_service.test.BaseSpringIT;

public class TestActiveProfileUnitTest extends BaseSpringIT {

	@Value("${profile.property.value}")
	private String propertyString;

	@Test
	void whenTestIsActive_thenValueShouldBeKeptFromApplicationTestProperties() {
		Assertions.assertEquals("This the application-test.properties file src/test", propertyString);
	}
}
