package com.bzanni.messagingserver_springws.domain;

import java.io.Serializable;

/**
 * POJO defining dummy message exchange objects
 * 
 * @author bzanni
 *
 */
public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1310323913063637140L;
	private String from;
	private String to;
	private String content;

	public Message() {
	}

	public Message(String from, String to, String content) {
		this.from = from;
		this.to = to;
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
}
