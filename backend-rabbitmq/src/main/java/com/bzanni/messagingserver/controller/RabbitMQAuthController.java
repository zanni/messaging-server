package com.bzanni.messagingserver.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bzanni.messagingserver.repository.ActiveSessionRepositoryException;
import com.bzanni.messagingserver.service.IActiveSessionService;

/**
 * Rest HTTP Json controller acting as rabbitmq authentification/authorization
 * backend
 * 
 * RabbitMQ http auth module should make HTTP POST request
 * 
 * @author bzanni
 *
 */
@RestController
public class RabbitMQAuthController {

	@Resource
	private IActiveSessionService activeSessionService;

	/**
	 * authenticate user
	 * 
	 * GET - /auth/user
	 * 
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 * @throws ActiveSessionRepositoryException
	 */
	@RequestMapping(value = "/auth/user", method = RequestMethod.GET)
	public String user(@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password, HttpServletRequest request,
			HttpServletResponse response) {
		if (activeSessionService.exists(username)) {
			return "allow anonymous";
		} else {
			return "deny";
		}

	}

	/**
	 * authorization on vhost (not used in this application as one vhost is
	 * used)
	 * 
	 * GET - /auth/user
	 * 
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 * @throws ActiveSessionRepositoryException
	 */
	@RequestMapping(value = "/auth/vhost", method = RequestMethod.GET)
	public String vhost(HttpServletRequest request, HttpServletResponse response) {
		return "allow";
	}

	/**
	 * authorization on resource (queue/exchange) creation/configuration/usage
	 * 
	 * GET - /auth/resource
	 * 
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 * @throws ActiveSessionRepositoryException
	 */
	@RequestMapping(value = "/auth/resource", method = RequestMethod.GET)
	public String resource(@RequestParam(value = "username", required = false) String username,
			@RequestParam("vhost") String vhost, @RequestParam("resource") String resource,
			@RequestParam("name") String name, @RequestParam("permission") String permission,
			HttpServletRequest request, HttpServletResponse response) {
		if (activeSessionService.checkValidity(username, name)) {
			return "allow";
		} else {
			return "deny";
		}
	}
}
