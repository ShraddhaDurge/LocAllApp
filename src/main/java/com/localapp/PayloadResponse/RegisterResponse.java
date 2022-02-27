package com.localapp.PayloadResponse;

import com.localapp.Model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
	private String message;
	private User user;
	private  String role;
}
