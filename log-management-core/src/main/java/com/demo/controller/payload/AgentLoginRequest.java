package com.demo.controller.payload;

import javax.validation.constraints.NotBlank;

public class AgentLoginRequest {
	@NotBlank
	private String clientId;

	@NotBlank
	private String secret;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
