package com.bzanni.messagingserver_springws.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebsocketSessionService {

	public Map<String, WebSocketSession> sessions = new ConcurrentHashMap<String, WebSocketSession>();

	public WebSocketSession read(String userId) {
		return sessions.get(userId);
	}

	public void create(String userId, WebSocketSession session) {
		sessions.put(userId, session);
	}

	public void update(String userId, WebSocketSession session) {
		sessions.put(userId, session);
	}

	public void delete(String userId) {
		sessions.remove(userId);
	}
}
