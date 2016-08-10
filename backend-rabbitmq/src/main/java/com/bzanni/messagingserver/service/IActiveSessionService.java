package com.bzanni.messagingserver.service;

import com.bzanni.messagingserver.domain.ActiveSession;

public interface IActiveSessionService {

	/**
	 * @param userId
	 * @return
	 * @throws ActiveSessionServiceException
	 */
	public ActiveSession create(String userId) throws ActiveSessionServiceException;
	
	/**
	 * @param userId
	 * @return
	 */
	public boolean exists(String userId);
	
	/**
	 * @param userId
	 * @return
	 */
	public boolean checkValidity(String userId, String queueName);

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
	public void exchange(String from, String to, String content) throws ActiveSessionServiceException;
}
