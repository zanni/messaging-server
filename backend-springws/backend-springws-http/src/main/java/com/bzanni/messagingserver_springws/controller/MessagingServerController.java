package com.bzanni.messagingserver_springws.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bzanni.messagingserver_springws.domain.ActiveSession;
import com.bzanni.messagingserver_springws.repository.ActiveSessionRepositoryException;
import com.bzanni.messagingserver_springws.service.ActiveSessionServiceException;
import com.bzanni.messagingserver_springws.service.IActiveSessionService;

/**
 * Rest HTTP Json controller dealing with user session creation
 * 
 * @author bzanni
 *
 */
@RestController
public class MessagingServerController {

	private static final Logger LOGGER = LogManager.getLogger(MessagingServerController.class);

	@Resource
	private IActiveSessionService activeSessionService;

	/**
	 * Handle exception from mappings
	 * 
	 * @param e
	 * @param request
	 * @param response
	 */
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ActiveSessionServiceException.class)
	public void error(ActiveSessionServiceException e, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.warn(e.getMessage());
		try {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (IOException e1) {
			LOGGER.error(e1);
		}
	}

	/**
	 * Post - /connect
	 * 
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 * @throws ActiveSessionRepositoryException
	 */
	@RequestMapping(value = "/connect", method = RequestMethod.POST)
	public ActiveSession connect(@RequestParam(value = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) throws ActiveSessionServiceException {

		ActiveSession session = activeSessionService.create(userId);

		return session;
	}

	/**
	 * Post - /disconnect
	 * 
	 * @param userId
	 * @param request
	 * @param response
	 * @throws ActiveSessionServiceException
	 */
	@RequestMapping(value = "/disconnect")
	public void disconnect(@RequestParam(value = "queueName", required = true) String queueName,
			HttpServletRequest request, HttpServletResponse response) throws ActiveSessionServiceException {

		activeSessionService.delete(queueName);

	}

	/**
	 * Post - /exchange
	 * 
	 * @param fromUserId
	 * @param toUserId
	 * @param content
	 * @param request
	 * @param response
	 * @throws ActiveSessionServiceException
	 */
	@RequestMapping(value = "/exchange")
	public void exchange(@RequestParam(value = "fromUserId", required = true) String fromUserId,
			@RequestParam(value = "toUserId", required = true) String toUserId,
			@RequestParam(value = "content", required = true) String content, HttpServletRequest request,
			HttpServletResponse response) throws ActiveSessionServiceException {

		activeSessionService.exchange(fromUserId, toUserId, content);

	}

}