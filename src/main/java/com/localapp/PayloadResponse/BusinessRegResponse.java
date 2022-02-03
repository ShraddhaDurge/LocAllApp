package com.localapp.PayloadResponse;

import com.localapp.model.Business;

public class BusinessRegResponse {
	private String message;
	private Business business;
	private String role;

	public BusinessRegResponse(String message, Business business, String role) {
		this.message = message;
		this.business = business;
		this.role = role;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "RegisterResponse{" +
				"message='" + message + '\'' +
				", business=" + business +
				'}';
	}
}
