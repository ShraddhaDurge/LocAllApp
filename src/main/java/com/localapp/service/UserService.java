package com.localapp.service;

import java.util.Base64;

import com.localapp.model.Business;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.localapp.model.User;
import com.localapp.PayloadRequest.LoginRequest;
import com.localapp.repository.UserRepository;

@Service
public class UserService{

	@Autowired
	UserRepository userRepository;

	private static final Logger logger = LogManager.getLogger(UserService.class);

	public User saveUser(User user) {
		userRepository.save(user);
		return findByEmail(user.getEmail());
	}

	public Boolean userExistsByEmail( String email) {
		return userRepository.existsByEmail(email);
	}

	public User findByEmail( String email) {
		return userRepository.findByEmail(email);
	}

	public User findById( int id) {
		return userRepository.findById(id);
	}

	public Boolean existsById( int id) {
		return userRepository.existsById(id);
	}



	//check if valid user is trying to login
	public User checkUser(LoginRequest userObject) {
		String reversedPassword = new StringBuffer(decodeString(userObject.getPassword())).reverse().toString();
		System.out.println(reversedPassword);

		User user = userRepository.findByEmail(userObject.getEmail());

		System.out.println(user +"Service checkuser");

		String decodedPassword = new StringBuffer(decodeString(user.getPassword())).reverse().toString();
		System.out.println(decodedPassword);
		String loginPassword = reversedPassword.replaceAll("\\s","");

		if(decodedPassword.equals(loginPassword)) {
			logger.info("User Login Successful. User: {} - ",userObject.getEmail());
			return user;
		}
		else
		{
			logger.info("User Login UnSuccessful");
			return null;
		}
	}

	//decode base64 string
	public String decodeString(String encodedPassword) {

		String decoded = new String(Base64.getDecoder().decode(encodedPassword));
		StringBuilder password = new StringBuilder();
		password.append(decoded);
		password = password.reverse();
		return password.toString();
	}
}

