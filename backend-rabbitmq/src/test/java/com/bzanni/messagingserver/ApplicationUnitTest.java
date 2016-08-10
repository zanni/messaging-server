package com.bzanni.messagingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

import com.bzanni.messagingserver.config.RabbitMQConfig;
import com.bzanni.messagingserver.config.RabbitmqQueueEventConfig;
import com.bzanni.messagingserver.controller.RabbitmqQueueEventListener;

@SpringBootApplication
@ComponentScan(basePackages = { "com.bzanni.messagingserver" }, excludeFilters = {
		@ComponentScan.Filter(value = RabbitMQConfig.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = RabbitmqQueueEventConfig.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = RabbitmqQueueEventListener.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = Application.class, type = FilterType.ASSIGNABLE_TYPE) })
@PropertySource({ "file:${messagingserver.home}/application.properties" })
public class ApplicationUnitTest {

	public ApplicationUnitTest() {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
