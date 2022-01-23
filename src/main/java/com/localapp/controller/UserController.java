package com.localapp.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.localapp.model.User;
import com.localapp.model.UserLogin;
import com.localapp.service.UserService;


@RequestMapping("/user")
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class UserController {
	private static final Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	UserService userService;
	
	
	@PostMapping("/register")
	public User saveUser(@RequestBody User user) {

		User newuser = new User();

		System.out.println(user);

		try
		{
			newuser = userService.saveUserByuserName(user.getUsername(), user.getPhoneno(), user.getEmail(), user.getPassword(), user.getRole());

			if(newuser != null) {
				logger.info("User: {} saved successfully!",newuser.getUsername());
				return newuser;
			}
			else
			{
				logger.error("User: {} could not be saved successfully!",user.getUsername());
				return null;
			}
		}

		catch(Exception e)
		{
			logger.error("User: {} could not be saved successfully!",user.getUsername());
		}

		return new User();
	}
	
	@PostMapping("/login")
	public String checkUserLogin(@RequestBody UserLogin userObject) {
		// Checks if user present in database, if yes returns userId

		User checkuser = new User();
		try
		{
			logger.info("Authenticating User with Email: {} :",userObject.getEmail());
			checkuser = userService.checkUser(userObject);
			if(checkuser!=null)
			{
				logger.info("SUCCESS");
				return checkuser.getEmail();
			}
			else
				logger.error("FAILURE");
			return null;
		}
		catch(Exception e)
		{
			logger.error("FAILURE");
			return null;
		}

	}

}
