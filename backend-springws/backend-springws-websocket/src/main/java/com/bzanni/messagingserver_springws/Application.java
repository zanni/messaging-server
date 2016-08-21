package com.bzanni.messagingserver_springws;

import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.bzanni.messagingserver_springws.controller.WebsocketHandler;
import com.bzanni.messagingserver_springws.controller.WebsocketHandshakeHandler;

/**
 * Spring boot main class
 * 
 * @author bzanni
 *
 */
@SpringBootApplication
@EnableWebSocket
public class Application extends SpringBootServletInitializer implements WebSocketConfigurer {

	@Resource
	private WebsocketHandler websocketHandler;

	@Resource
	private WebsocketHandshakeHandler websocketHandshakeHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		registry.addHandler(websocketHandler, "/ws").setAllowedOrigins("*").addInterceptors(websocketHandshakeHandler)
				.withSockJS();

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}