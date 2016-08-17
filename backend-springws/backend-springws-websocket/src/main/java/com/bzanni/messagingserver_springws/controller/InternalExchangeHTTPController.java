package com.bzanni.messagingserver_springws.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.bzanni.messagingserver_springws.domain.Message;
import com.bzanni.messagingserver_springws.service.ActiveSessionServiceException;
import com.bzanni.messagingserver_springws.service.WebsocketSessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class InternalExchangeHTTPController {

	private static final Logger LOGGER = LogManager
			.getLogger(InternalExchangeHTTPController.class);
	
	@Resource
	private WebsocketSessionService websocketSessionService;

	private ObjectMapper mapper = new ObjectMapper();

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
	public void exchange(@RequestBody Message message,
			HttpServletRequest request, HttpServletResponse response)
			throws ActiveSessionServiceException {

		WebSocketSession read = websocketSessionService.read(message.getTo());

		try {
			read.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
		} catch (JsonProcessingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}

	}
}
