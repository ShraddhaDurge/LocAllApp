package com.localapp.Controller;

import com.localapp.PayloadResponse.BusinessRegResponse;
import com.localapp.PayloadResponse.MessageResponse;
import com.localapp.PayloadResponse.RegisterResponse;
import com.localapp.Model.Business;
import com.localapp.Service.UserService;
import com.localapp.Service.BusinessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.localapp.Model.User;
import com.localapp.PayloadRequest.LoginRequest;


@RequestMapping("/user")
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class UserController {
	private static final Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	UserService userService;

	@Autowired
	BusinessService vendorService;

	//register users and save user data in database
	@PostMapping("/register")
	public ResponseEntity<?> saveUser(@RequestBody User newUser) {

		//check if user email already exist in database
		if (userService.userExistsByEmail(newUser.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already taken!"));
		}
		User user = new User(newUser.getUsername(),newUser.getPhoneno() ,newUser.getEmail(), newUser.getPassword(), newUser.getRole());

		User registeredUser = userService.saveUser(user);             //save new user details in database

		return ResponseEntity.ok(new RegisterResponse("User registered successfully!",registeredUser,registeredUser.getRole()));

	}

	//authenticate users
	@PostMapping("/login")
	public ResponseEntity<?> checkUserLogin(@RequestBody LoginRequest userObject) {
		System.out.println(userObject);
		try
		{
			logger.info("Authenticating User with Email: {} :",userObject.getEmail());
			User checkuser = userService.checkUser(userObject);

			System.out.println(checkuser);
			if(checkuser!=null)
			{
				logger.info("SUCCESS");
				if(checkuser.getRole().equals("vendor")) {
					Business b = vendorService.getBusinessVendor(checkuser);
					System.out.println(b);
					return ResponseEntity.ok(new BusinessRegResponse("Vendor Login successfully!", b, checkuser.getRole()));
				}
				else
					return ResponseEntity.ok(new RegisterResponse("User login successfully!",checkuser,checkuser.getRole()));
			}
			else
				return ResponseEntity
						.badRequest()
					.body(new MessageResponse("Error: Password does not match!"));
		}
		catch(Exception e)
		{
			logger.error("FAILURE");
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: User does not exists!"));
		}

	}

}
