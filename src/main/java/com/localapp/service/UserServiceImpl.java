package com.localapp.service;

import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.localapp.model.User;
import com.localapp.model.UserLogin;
import com.localapp.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
	@Override
	public User saveUserByuserName(String userName, String phoneNumber, String email, String password, String role) {

		User user = new User();
		int added = 0;
		try
		{
			added = userRepository.addUserByuserName(userName, phoneNumber, email, password, role);
			if(added ==1)
			{
				user.setUsername(userName);
				user.setPhoneno(phoneNumber);
				user.setEmail(email);;
				user.setPassword(password);;
				user.setRole(role);

				logger.info("User with UserName {} added", userName);
			}
		}
		catch(Exception e)
		{
			logger.error("User with UserName {} could not be added", userName);	
		}
		return user;
	}
	@Override
	public User checkUser(UserLogin userObject) {

		User checkuser;
		String encodedPassword = decodeString(userObject.getPassword());
		String password = new StringBuffer(encodedPassword).reverse().toString();

		checkuser = userRepository.checkUser(userObject,password);
		if(checkuser != null) {
			System.out.println("\n");
			logger.info("User found in database");
		}
		else {
			System.out.println("\n");
			logger.info("User not found in database.User Login UnsSuccessful" );
			System.out.println("\n");
			return checkuser;
		}


		if(checkuser.getPassword().equals(password.replaceAll("\\s",""))) {
			logger.info("User Login Successful. User: {} - ",userObject.getEmail());
			System.out.println("\n");
			return checkuser;

		}
		else
		{
			logger.info("User Login UnsSuccessful");
			System.out.println("\n");
			return null;

		}

	}
	public String decodeString(String encodedPassword) {

		String decoded = new String(Base64.getDecoder().decode(encodedPassword));
		StringBuilder password = new StringBuilder();
		password.append(decoded);
		password = password.reverse();
		return password.toString();
	}
}

