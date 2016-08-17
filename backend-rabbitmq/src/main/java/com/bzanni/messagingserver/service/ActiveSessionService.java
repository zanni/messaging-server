package com.bzanni.messagingserver.service;

import java.util.UUID;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.messagingserver.domain.ActiveSession;
import com.bzanni.messagingserver.domain.Message;
import com.bzanni.messagingserver.repository.ActiveSessionRepositoryException;
import com.bzanni.messagingserver.repository.IActiveSessionRepository;

/**
 * @author bzanni
 *
 */
@Service
public class ActiveSessionService implements IActiveSessionService {

	private static final Logger LOGGER = LogManager.getLogger(ActiveSessionService.class);

	/**
	 * active session persistence repository
	 */
	@Resource
	private IActiveSessionRepository activeSessionRepository;

	/**
	 * rabbitmq connection
	 */
	@Resource
	private RabbitTemplate rabbitTemplate;

	/**
	 * rabbitmq host ip
	 */
	@Value("${rabbitmq_host}")
	private String rabbitmqHost;

	@Override
	public ActiveSession create(String userId) throws ActiveSessionServiceException {

		// create activesession domain object
		// dummy find available rabbit node
		String listeningAddress = "ws://" + rabbitmqHost + ":15674/ws";
		// gen random based listeningKey
		UUID randomKey = UUID.randomUUID();
		String listeningKey = "queue" + "." + userId + "." + randomKey.toString();
		ActiveSession session = new ActiveSession(userId, listeningAddress, listeningKey);

		try {
			activeSessionRepository.create(session);
			LOGGER.info("CREATE: " + userId);
			return session;
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}
	}

	@Override
	public boolean exists(String userId) {
		try {
			activeSessionRepository.read(userId);
			return true;
		} catch (ActiveSessionRepositoryException e) {
			return false;
		}
	}

	@Override
	public boolean checkValidity(String userId, String listeningKey) {
		try {
			ActiveSession read = activeSessionRepository.read(userId);
			return read.getListeningKey().equals(listeningKey);
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
	public void exchange(String from, String to, String content) throws ActiveSessionServiceException {
		try {
			activeSessionRepository.read(from);
			ActiveSession toUserSession = activeSessionRepository.read(to);
			rabbitTemplate.convertAndSend(toUserSession.getListeningKey(), new Message(from, content));
			LOGGER.info("EX: " + from + " to " + to);
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}

	}

	/**
	 * find userId from queue name
	 * 
	 * @param queueName
	 * @return
	 * @throws ActiveSessionServiceException
	 */
	private String getUserIdFromQueueName(String queueName) throws ActiveSessionServiceException {
		try {
			return queueName.split("\\.")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ActiveSessionServiceException("invalid queue name");
		}
	}

}
