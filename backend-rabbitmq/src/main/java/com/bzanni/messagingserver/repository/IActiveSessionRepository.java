package com.bzanni.messagingserver.repository;

import com.bzanni.messagingserver.domain.ActiveSession;

public interface IActiveSessionRepository {
	public void create(ActiveSession sessionToCreate)
			throws ActiveSessionRepositoryException;

	public ActiveSession read(String userId)
			throws ActiveSessionRepositoryException;

	public void update(ActiveSession session)
			throws ActiveSessionRepositoryException;

	public void delete(String userId)
			throws ActiveSessionRepositoryException;
}