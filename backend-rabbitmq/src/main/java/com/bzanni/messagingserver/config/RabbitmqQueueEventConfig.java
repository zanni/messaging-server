package com.bzanni.messagingserver.config;

import javax.annotation.Resource;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bzanni.messagingserver.controller.RabbitmqQueueEventListener;

/**
 * Configure stuff in RabbitMQ:
 * 
 * - declare: queue "queue_events"
 * 
 * - bind: exchange "amq.rabbitmq.event" to queue "queue_events" using key
 * "queue_events"
 * 
 * - init: listener to "queue_events"
 * 
 * @author bzanni
 *
 */
@Configuration
public class RabbitmqQueueEventConfig {

	private static final String AMQ_RABBITMQ_EVENT = "amq.rabbitmq.event";
	private static final String queueName = "queue_events";
	private static final String routingKey = "queue.*";

	@Resource
	private RabbitmqQueueEventListener rabbitmqQueueEventListener;

	@Bean
	Queue queue() {
		return new Queue(queueName, true, true, true);
	}

	@Bean
	TopicExchange exchange() {
		TopicExchange topicExchange = new TopicExchange(AMQ_RABBITMQ_EVENT);
		topicExchange.setInternal(true);
		return topicExchange;
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(rabbitmqQueueEventListener);
		return container;
	}

}
