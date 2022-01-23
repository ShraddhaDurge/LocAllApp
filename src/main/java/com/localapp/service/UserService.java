package com.localapp.service;

import com.localapp.model.User;
import com.localapp.model.UserLogin;

public interface UserService {
	public User saveUserByuserName(String userName, String phoneNumber, String email, String password, String role);
	public User checkUser(UserLogin userObject);

}
