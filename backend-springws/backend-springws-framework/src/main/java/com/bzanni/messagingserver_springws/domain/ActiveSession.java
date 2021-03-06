package com.bzanni.messagingserver_springws.domain;

import java.io.Serializable;

/**
 * POJO defining session model
 * 
 * @author bzanni
 *
 */
public class ActiveSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1870971895736883462L;
	private String userId;
	private String listeningAddress;
	private String listeningKey;
	private boolean acked = false;

	public ActiveSession() {
	}

	public ActiveSession(String userId, String listeningAddress,
			String listeningKey) {
		this.setUserId(userId);
		this.setListeningAddress(listeningAddress);
		this.setListeningKey(listeningKey);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActiveSession[");
		builder.append("userId: ");
		builder.append(this.getUserId());
		builder.append(", listeningAddress: ");
		builder.append(this.getListeningAddress());
		builder.append(", listeningKey: ");
		builder.append(this.getListeningKey());
		builder.append("]");
		return builder.toString();
	}

	public String getUserId() {
		return userId;
	}

	private void setUserId(String userId) {
		this.userId = userId;
	}

	public String getListeningAddress() {
		return listeningAddress;
	}

	private void setListeningAddress(String listeningAddress) {
		this.listeningAddress = listeningAddress;
	}

	public String getListeningKey() {
		return listeningKey;
	}

	private void setListeningKey(String listeningKey) {
		this.listeningKey = listeningKey;
	}

	public boolean isAcked() {
		return acked;
	}

	public void setAcked(boolean acked) {
		this.acked = acked;
	}
}
