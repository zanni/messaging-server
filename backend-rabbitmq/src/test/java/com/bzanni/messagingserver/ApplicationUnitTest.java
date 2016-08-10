package com.bzanni.messagingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.bzanni.messagingserver.config.RabbitMQConfig;
import com.bzanni.messagingserver.config.RabbitmqQueueEventConfig;
import com.bzanni.messagingserver.controller.MessagingServerController;
import com.bzanni.messagingserver.controller.RabbitMQAuthController;
import com.bzanni.messagingserver.controller.RabbitmqQueueEventListener;
import com.bzanni.messagingserver.service.ActiveSessionService;

@SpringBootApplication
@ComponentScan(basePackages = { "com.bzanni.messagingserver" }, excludeFilters = {
		@ComponentScan.Filter(value = RabbitMQConfig.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = RabbitmqQueueEventConfig.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = RabbitmqQueueEventListener.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = RabbitMQAuthController.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = ActiveSessionService.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = MessagingServerController.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = Application.class, type = FilterType.ASSIGNABLE_TYPE) })
public class ApplicationUnitTest {

	public ApplicationUnitTest() {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
