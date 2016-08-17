package com.bzanni.messagingserver_springws.repository;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bzanni.messagingserver_springws.domain.ActiveSession;

import net.spy.memcached.MemcachedClient;

/**
 * Memcached based implementation of IActiveSessionRepository
 * 
 * @author bzanni
 *
 */
@Service
public class MemcachedActiveSessionRepository implements IActiveSessionRepository {

	private static final int ACTIVE_SESSION_TTL_SECOND = 10 * 60;

	@Value("${messagingserver.memcached.host}")
	private String memcachedHost;

	@Value("${messagingserver.memcached.port}")
	private Integer memcachedPort;

	private MemcachedClient client;

	@PostConstruct
	public void init() {
		try {
			client = new MemcachedClient(new InetSocketAddress(memcachedHost, memcachedPort));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ActiveSession getKey(String key) {
		if (client == null) {
			return null;
		}
		return (ActiveSession) client.get(key);
	}

	private void setKey(String key, ActiveSession obj, int ttl) {
		if (client != null && obj != null) {
			client.set(key, ttl, obj);
		}
	}

	private void removeKey(String key) {
		if (client != null) {
			client.delete(key);
		}
	}

	@Override
	public void create(ActiveSession sessionToCreate) throws ActiveSessionRepositoryException {
		ActiveSession session = this.getKey(sessionToCreate.getUserId());
		if (session != null) {
			throw new ActiveSessionRepositoryException("already taken userid");
		}
		this.setKey(sessionToCreate.getUserId(), sessionToCreate,
				MemcachedActiveSessionRepository.ACTIVE_SESSION_TTL_SECOND * 1000);
	}

	@Override
	public ActiveSession read(String userId) throws ActiveSessionRepositoryException {
		ActiveSession session = this.getKey(userId);
		if (session == null) {
			throw new ActiveSessionRepositoryException("user not found");
		}
		return session;
	}

	@Override
	public void update(ActiveSession sessionToUpdate) throws ActiveSessionRepositoryException {
		ActiveSession session = this.getKey(sessionToUpdate.getUserId());
		if (session == null) {
			throw new ActiveSessionRepositoryException("user not found");
		}
		this.setKey(sessionToUpdate.getUserId(), sessionToUpdate, ACTIVE_SESSION_TTL_SECOND * 1000);
	}

	@Override
	public void delete(String userId) throws ActiveSessionRepositoryException {
//		ActiveSession session = this.getKey(userId);
		// if (session == null) {
		// throw new ActiveSessionRepositoryException("user not found");
		// }
		this.removeKey(userId);
	}
}
