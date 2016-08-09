package com.bzanni.messagingserver.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.bzanni.messagingserver.domain.ActiveSession;

@Service
public class ActiveSessionRepository implements IActiveSessionRepository {

	private Map<String, ActiveSession> repository = new ConcurrentHashMap<String, ActiveSession>();

	@Override
	public void create(ActiveSession sessionToCreate) throws ActiveSessionRepositoryException {
		ActiveSession session = repository.get(sessionToCreate.getUserId());
		if (session != null) {
			throw new ActiveSessionRepositoryException("already taken userid");
		}
		repository.put(sessionToCreate.getUserId(), sessionToCreate);
	}

	public void ack(String userId) throws ActiveSessionRepositoryException {
		ActiveSession session = repository.get(userId);
		if (session == null) {
			throw new ActiveSessionRepositoryException("user not found");
		}
		session.setAcked(true);
		repository.put(userId, session);
	}

	@Override
	public ActiveSession read(String userId) throws ActiveSessionRepositoryException {
		ActiveSession session = repository.get(userId);
		if (session == null) {
			throw new ActiveSessionRepositoryException("user not found");
		}
		return session;
	}

	@Override
	public void update(ActiveSession sessionToUpdate) throws ActiveSessionRepositoryException {
		ActiveSession session = repository.get(sessionToUpdate.getUserId());
		if (session == null) {
			throw new ActiveSessionRepositoryException("user not found");
		}
		repository.put(sessionToUpdate.getUserId(), sessionToUpdate);
	}

	@Override
	public void delete(String userId) throws ActiveSessionRepositoryException {
		ActiveSession session = repository.get(userId);
		if (session == null) {
			throw new ActiveSessionRepositoryException("user not found");
		}
		repository.remove(session.getUserId());
	}

}
