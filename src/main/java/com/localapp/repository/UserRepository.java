package com.localapp.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.localapp.model.User;
import com.localapp.model.UserLogin;

@Repository
public class UserRepository {
	private static final Logger logger = LogManager.getLogger(UserRepository.class);
	@Autowired
	JdbcTemplate template;


	public int addUserByuserName(String userName, String phoneNumber, String email, String password, String role) {
		// Registering User

		try
		{
			logger.info("Inserting into database Users for User: {} ",userName);
			int added = template.update("insert into users(username,phoneno,email,password,role) values(?,?,?,?,?)",
					userName,phoneNumber,email,password,role);

			if(added ==1)
			{
				logger.info("Insertion Successful for User: {} ",userName);
				return added;
			}
		}
		catch(Exception e)
		{
			logger.error("Insertion could not be done!");

		}
		return 0;
	}
	
	public User checkUser(UserLogin userObject, String password) {
		
		User temp = null;
		try
		{

			String FINDUSER = "select * from users where email=?";
			temp = template.queryForObject(FINDUSER, new RowMapper<User>() {

				@Override
				public User mapRow(ResultSet set, int arg1) throws SQLException {
					User userMaster = new User();
					userMaster.setUsername(set.getString(2));
					userMaster.setPhoneno(set.getString(3));
					userMaster.setEmail(set.getString(4));
					userMaster.setPassword(new StringBuffer(decodeString(set.getString(5))).reverse().toString());
					userMaster.setRole(set.getString(6));
					return userMaster;
				}

			}, userObject.getEmail());

			if(temp != null) {
				
				if(temp.getPassword().equals(password.replaceAll("\\s",""))) {
					return temp;
				}
				else
				{
					logger.error("Enter the correct Password!");
					return temp;

				}
			}
			else
			{
				logger.error("User not found in database");
				return null;
			}
		}
		catch(Exception e)
		{
			logger.error("Error Occured.User not found in database");
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
