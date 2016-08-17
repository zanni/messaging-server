package com.bzanni.messagingserver_springws.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.bzanni.messagingserver_springws.service.IActiveSessionService;

@Service
public class WebsocketHandshakeHandler implements HandshakeInterceptor {

	private static final Logger LOGGER = LogManager
			.getLogger(WebsocketHandshakeHandler.class);

	@Resource
	private IActiveSessionService activeSessionService;

	@Override
	public boolean beforeHandshake(ServerHttpRequest arg0,
			ServerHttpResponse arg1, WebSocketHandler arg2,
			Map<String, Object> arg3) throws Exception {

		String query = arg0.getURI().getQuery();
		Map<String, String> splitQuery = splitQuery(query);
		String userId = splitQuery.get("userId");
		String token = splitQuery.get("token");

		boolean checkValidity = activeSessionService.checkValidity(userId,
				token);

		if (!checkValidity) {
			LOGGER.warn("deny access to: " + userId + " with token: " + token);
		}
		arg3.put("userId", userId);
		arg3.put("token", token);
		return checkValidity;
	}

	/**
	 * http://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-
	 * value-collection
	 * 
	 * @param query
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private Map<String, String> splitQuery(String query)
			throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub
		
	}

}
