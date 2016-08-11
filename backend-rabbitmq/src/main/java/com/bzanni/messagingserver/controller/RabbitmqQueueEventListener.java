package com.bzanni.messagingserver.controller;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

import com.bzanni.messagingserver.service.ActiveSessionServiceException;
import com.bzanni.messagingserver.service.IActiveSessionService;

/**
 * 
 * Listener on rabbitmq internal queue event (create/delete)
 * 
 * When a queue is created in rabbitmq, corresponding session is acked
 * 
 * When a queue is deleted in rabbitmq, corresponing session is deleted
 * 
 * @author bzanni
 *
 */
@Service
public class RabbitmqQueueEventListener implements MessageListener {

	private static final String QUEUE_DELETED = "queue.deleted";

	private static final String QUEUE_CREATED = "queue.created";

	private static final Logger LOGGER = LogManager
			.getLogger(RabbitmqQueueEventListener.class);

	@Resource
	private IActiveSessionService activeSessionService;

	@Override
	public void onMessage(Message message) {

		try {
			String routingKey = message.getMessageProperties()
					.getReceivedRoutingKey();
			String queueName = (String) message.getMessageProperties()
					.getHeaders().get("name");

			LOGGER.debug("@rabbit >>> " + routingKey + " - " + queueName);

			if (QUEUE_CREATED.equals(routingKey)) {

				activeSessionService.ack(queueName);

			} else if (QUEUE_DELETED.equals(routingKey)) {

				activeSessionService.delete(queueName);

			}
		} catch (ActiveSessionServiceException e) {

			LOGGER.error(e);

		}

	}
}
