package com.bzanni.messagingserver.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Configure RabbitMQ connection from application.properties
 * 
 * @author bzanni
 *
 */
@Component
@Configuration
public class RabbitMQConfig {

	@Value("${rabbitmq_host}")
	private String rabbitmqHost;

	@Value("${rabbitmq_port}")
	private Integer rabbitmqPort;

	@Value("${rabbitmq_username}")
	private String rabbitmqUsername;

	@Value("${rabbitmq_password}")
	private String rabbitmqPassword;

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
				this.getRabbitmqHost(), this.getRabbitmqPort());
		connectionFactory.setUsername(this.getRabbitmqUsername());
		connectionFactory.setPassword(this.getRabbitmqPassword());
		return connectionFactory;
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
		return jsonMessageConverter;
	}

	public String getRabbitmqHost() {
		return rabbitmqHost;
	}

	public void setRabbitmqHost(String rabbitmqHost) {
		this.rabbitmqHost = rabbitmqHost;
	}

	public Integer getRabbitmqPort() {
		return rabbitmqPort;
	}

	public void setRabbitmqPort(Integer rabbitmqPort) {
		this.rabbitmqPort = rabbitmqPort;
	}

	public String getRabbitmqUsername() {
		return rabbitmqUsername;
	}

	public void setRabbitmqUsername(String rabbitmqUsername) {
		this.rabbitmqUsername = rabbitmqUsername;
	}

	public String getRabbitmqPassword() {
		return rabbitmqPassword;
	}

	public void setRabbitmqPassword(String rabbitmqPassword) {
		this.rabbitmqPassword = rabbitmqPassword;
	}
}