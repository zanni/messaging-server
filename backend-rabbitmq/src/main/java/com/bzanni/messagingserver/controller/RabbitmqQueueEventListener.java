package com.bzanni.messagingserver.controller;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

import com.bzanni.messagingserver.service.ActiveSessionServiceException;
import com.bzanni.messagingserver.service.IActiveSessionService;

/**
 * 
 * Listener on rabbitmq internal queue event (create/delete)
 * 
 * @author bzanni
 *
 */
@Service
public class RabbitmqQueueEventListener implements MessageListener {

	private static final String QUEUE_DELETED = "queue.deleted";

	private static final String QUEUE_CREATED = "queue.created";

	private static final Logger LOGGER = Logger.getLogger(RabbitmqQueueEventListener.class);

	@Resource
	private IActiveSessionService activeSessionService;

	@Override
	public void onMessage(Message message) {

		try {
			String queueName = (String) message.getMessageProperties().getHeaders().get("name");

			if (QUEUE_CREATED.equals(message.getMessageProperties().getReceivedRoutingKey())) {

				activeSessionService.ack(queueName);

			} else if (QUEUE_DELETED.equals(message.getMessageProperties().getReceivedRoutingKey())) {

				activeSessionService.delete(queueName);

			}
		} catch (ActiveSessionServiceException e) {

			LOGGER.error(e);

		}

	}
}
