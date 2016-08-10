package com.bzanni.messagingserver.service;

import java.util.UUID;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.messagingserver.domain.ActiveSession;
import com.bzanni.messagingserver.repository.ActiveSessionRepositoryException;
import com.bzanni.messagingserver.repository.IActiveSessionRepository;

@Service
public class ActiveSessionService implements IActiveSessionService {

	private static final Logger LOGGER = LogManager
			.getLogger(ActiveSessionService.class);

	@Resource
	private IActiveSessionRepository activeSessionRepository;

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq_host}")
	private String rabbitmqHost;

	@Override
	public ActiveSession create(String userId)
			throws ActiveSessionServiceException {

		// create activesession domain object
		String listeningAddress = "http://" + rabbitmqHost + ":15674/stomp";
		UUID randomKey = UUID.randomUUID();
		String queueName = "queue" + "." + userId + "." + randomKey.toString();
		ActiveSession session = new ActiveSession(userId, listeningAddress,
				queueName);

		// TODO bertrand: find a way to declare auto-delete queue here that then
		// will be consumed by STOMP clients
		// Queue queue = new Queue(queueName, false, true, false);
		// rabbitMQConfig.getAdmin().setAutoStartup(false);
		// rabbitMQConfig.getAdmin().declareQueue(queue);

		try {
			activeSessionRepository.create(session);
			LOGGER.info("CREATE: " + userId);
			return session;
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}

	}

	public boolean exists(String userId) {
		try {
			activeSessionRepository.read(userId);
			return true;
		} catch (ActiveSessionRepositoryException e) {
			return false;
		}
	}

	public boolean checkValidity(String userId, String queueName) {
		try {
			ActiveSession read = activeSessionRepository.read(userId);

			return read.getListeningKey().equals(queueName);
		} catch (ActiveSessionRepositoryException e) {
			return false;
		}
	}

	@Override
	public void ack(String queueName) throws ActiveSessionServiceException {
		try {
			String userId = getUserIdFromQueueName(queueName);
			ActiveSession session = activeSessionRepository.read(userId);
			session.setAcked(true);
			activeSessionRepository.update(session);
			LOGGER.info("ACK: " + userId);
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}
	}

	@Override
	public void delete(String queueName) throws ActiveSessionServiceException {
		try {
			String userId = getUserIdFromQueueName(queueName);
			activeSessionRepository.delete(userId);
			LOGGER.info("DEL: " + userId);
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}

	}

	@Override
	public void exchange(String from, String to, String content)
			throws ActiveSessionServiceException {
		try {
			activeSessionRepository.read(from);
			ActiveSession toUserSession = activeSessionRepository.read(to);
			rabbitTemplate.convertAndSend(toUserSession.getListeningKey(),
					content);
			LOGGER.info("EX: " + from + " to " + to);
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}

	}

	private String getUserIdFromQueueName(String queueName) {
		return queueName.split("\\.")[1];
	}

}
