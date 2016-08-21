package com.bzanni.messagingserver_springws.controller;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.bzanni.messagingserver_springws.domain.Message;
import com.bzanni.messagingserver_springws.service.ActiveSessionServiceException;
import com.bzanni.messagingserver_springws.service.IActiveSessionService;
import com.bzanni.messagingserver_springws.service.WebsocketSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handle websocket connection
 * 
 * @author bertrand
 *
 */
@Service
public class WebsocketHandler extends TextWebSocketHandler {

	private static final Logger LOGGER = LogManager.getLogger(WebsocketHandler.class);

	@Resource
	private WebsocketSessionService websocketSessionService;

	@Resource
	private IActiveSessionService activeSessionService;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String userId = (String) session.getAttributes().get("userId");
		String token = (String) session.getAttributes().get("token");
		LOGGER.debug("ws connect: " + userId + " " + token);
		websocketSessionService.create(userId, session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
		LOGGER.debug("ws ex: " + textMessage.getPayload());
		Message readValue = mapper.readValue(textMessage.getPayload(), Message.class);
		try {
			activeSessionService.exchange(readValue.getFrom(), readValue.getTo(), readValue.getContent());
			session.sendMessage(new TextMessage("OK"));
		} catch (ActiveSessionServiceException e) {
			LOGGER.error(e);
			session.sendMessage(new TextMessage("NOK: "+e.getMessage()));
		}

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		LOGGER.debug("ws close: " + status);
		String userId = (String) session.getAttributes().get("userId");
		String token = (String) session.getAttributes().get("token");
		websocketSessionService.delete(userId);
		activeSessionService.delete(token);
	}

}
