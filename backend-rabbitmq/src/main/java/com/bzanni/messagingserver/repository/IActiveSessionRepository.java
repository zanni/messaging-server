package com.bzanni.messagingserver.repository;

import com.bzanni.messagingserver.domain.ActiveSession;

/**
 * CRUD interface on session repository
 * 
 * @author bzanni
 *
 */
public interface IActiveSessionRepository {
	/**
	 * @param sessionToCreate
	 * @throws ActiveSessionRepositoryException
	 */
	public void create(ActiveSession sessionToCreate) throws ActiveSessionRepositoryException;

	/**
	 * @param userId
	 * @return
	 * @throws ActiveSessionRepositoryException
	 */
	public ActiveSession read(String userId) throws ActiveSessionRepositoryException;

	/**
	 * @param session
	 * @throws ActiveSessionRepositoryException
	 */
	public void update(ActiveSession session) throws ActiveSessionRepositoryException;

	/**
	 * @param userId
	 * @throws ActiveSessionRepositoryException
	 */
	public void delete(String userId) throws ActiveSessionRepositoryException;
}