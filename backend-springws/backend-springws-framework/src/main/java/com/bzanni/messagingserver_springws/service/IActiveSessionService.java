package com.bzanni.messagingserver_springws.service;

import com.bzanni.messagingserver_springws.domain.ActiveSession;

public interface IActiveSessionService {

	/**
	 * Create and return user's ActiveSession
	 * 
	 * @param userId
	 * @return
	 * @throws ActiveSessionServiceException
	 */
	public ActiveSession create(String userId)
			throws ActiveSessionServiceException;

	/**
	 * Return true if user is linked to an active session
	 * 
	 * @param userId
	 * @return
	 */
	public boolean exists(String userId);

	/**
	 * Return true if user is linked to an active session corresponding to the
	 * given listeningKey
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkValidity(String userId, String listeningKey);

	/**
	 * @param queueName
	 * @throws ActiveSessionServiceException
	 */
	public void ack(String queueName) throws ActiveSessionServiceException;

	/**
	 * @param userId
	 * @throws ActiveSessionServiceException
	 */
	public void delete(String queueName) throws ActiveSessionServiceException;

	/**
	 * @param userId
	 * @throws ActiveSessionServiceException
	 */
	public void exchange(String from, String to, String content)
			throws ActiveSessionServiceException;
}
