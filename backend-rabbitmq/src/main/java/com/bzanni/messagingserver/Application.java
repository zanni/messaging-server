package com.bzanni.messagingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Spring boot main class
 * 
 * @author bzanni
 *
 */
@SpringBootApplication
@PropertySource({ "file:${messagingserver.home}/application.properties" })
public class Application {

	public Application() {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
