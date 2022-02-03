package com.localapp.PayloadResponse;

import com.localapp.model.User;

public class RegisterResponse {
	private String message;
	private User user;
	private  String role;

	public RegisterResponse(String message, User user,String role) {
		this.message = message;
		this.user = user;
		this.role = role;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
				", user=" + user +
				'}';
	}
}
