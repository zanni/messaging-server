package com.bzanni.messagingserver_springws.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bzanni.messagingserver_springws.domain.ActiveSession;
import com.bzanni.messagingserver_springws.domain.Message;
import com.bzanni.messagingserver_springws.etcd.EtcdBinding;
import com.bzanni.messagingserver_springws.repository.ActiveSessionRepositoryException;
import com.bzanni.messagingserver_springws.repository.IActiveSessionRepository;

/**
 * Active session service:
 * - persistence using memcached
 * - 
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
	private IActiveSessionRepository memcachedActiveSessionRepository;

	@Resource
	private EtcdBinding etcdBinding;

	private AtomicInteger roundRobin = new AtomicInteger(0);

	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public ActiveSession create(String userId) throws ActiveSessionServiceException {
		String websocketService = this.getWebsocketService();
		if (websocketService == null) {
			throw new ActiveSessionServiceException("No available websocket node");
		}
		String listeningAddress = "http://" + websocketService + "/ws";
		UUID randomKey = UUID.randomUUID();
		String listeningKey = "queue" + "." + userId + "." + randomKey.toString();
		ActiveSession session = new ActiveSession(userId, listeningAddress, listeningKey);

		try {
			memcachedActiveSessionRepository.create(session);
			LOGGER.info("CREATE: " + userId + " on " + listeningAddress);
			return session;
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}
	}

	@Override
	public boolean exists(String userId) {
		try {
			memcachedActiveSessionRepository.read(userId);
			return true;
		} catch (ActiveSessionRepositoryException e) {
			return false;
		}
	}

	@Override
	public boolean checkValidity(String userId, String listeningKey) {
		try {
			ActiveSession read = memcachedActiveSessionRepository.read(userId);
			return read.getListeningKey().equals(listeningKey);
		} catch (ActiveSessionRepositoryException e) {
			return false;
		}
	}

	@Override
	public void ack(String queueName) throws ActiveSessionServiceException {
		try {
			String userId = getUserIdFromQueueName(queueName);
			ActiveSession session = memcachedActiveSessionRepository.read(userId);
			session.setAcked(true);
			memcachedActiveSessionRepository.update(session);
			LOGGER.info("ACK: " + userId);
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}
	}

	@Override
	public void delete(String queueName) throws ActiveSessionServiceException {
		try {
			String userId = getUserIdFromQueueName(queueName);
			memcachedActiveSessionRepository.delete(userId);
			LOGGER.info("DEL: " + userId);
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}

	}

	@Override
	public void exchange(String from, String to, String content) throws ActiveSessionServiceException {
		try {
			memcachedActiveSessionRepository.read(from);
			ActiveSession toSession = memcachedActiveSessionRepository.read(to);
			Message message = new Message(from, to, content);
			this.postToWebsocket(toSession, message);
			LOGGER.info("EX: " + from + " to " + to);
		} catch (ActiveSessionRepositoryException e) {
			throw new ActiveSessionServiceException(e.getMessage());
		}
	}

	private void postToWebsocket(ActiveSession to, Message message) {
		String addr = to.getListeningAddress().replace("/ws", "/exchange");

		ResponseEntity<String> postForEntity = restTemplate.postForEntity(addr, message, String.class);

		LOGGER.debug(postForEntity.getStatusCode());
	}

	private String getWebsocketService() {
		List<String> activeWebsocketNode = new ArrayList<String>(etcdBinding.getActiveWebsocketNode().values());

		if (activeWebsocketNode.isEmpty()) {
			return null;
		}
		int i = roundRobin.incrementAndGet();
		if (i > activeWebsocketNode.size() - 1) {
			i = 0;
			roundRobin.set(0);
		}
		String node = activeWebsocketNode.get(i);

		return node;
	}

	private String getUserIdFromQueueName(String queueName) {
		return queueName.split("\\.")[1];
	}

}
