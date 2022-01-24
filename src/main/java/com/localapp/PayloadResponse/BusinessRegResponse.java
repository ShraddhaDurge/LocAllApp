package com.localapp.PayloadResponse;

import com.localapp.model.Business;

public class BusinessRegResponse {
	private String message;
	private Business business;

	public BusinessRegResponse(String message, Business business) {
		this.message = message;
		this.business = business;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	@Override
	public String toString() {
		return "RegisterResponse{" +
				"message='" + message + '\'' +
				", business=" + business +
				'}';
	}
}
