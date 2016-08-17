package com.bzanni.messagingserver_springws.controller;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.bzanni.messagingserver_springws.service.IActiveSessionService;
import com.bzanni.messagingserver_springws.service.WebsocketSessionService;

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

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String userId = (String) session.getAttributes().get("userId");
		String token = (String) session.getAttributes().get("token");
		activeSessionService.ack(token);
		websocketSessionService.create(userId, session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		LOGGER.debug(status);
		String userId = (String) session.getAttributes().get("userId");
		String token = (String) session.getAttributes().get("token");
		websocketSessionService.delete(userId);
		activeSessionService.delete(token);
	}

}
