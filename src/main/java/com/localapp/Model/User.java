package com.localapp.Model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User{

	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	@Column
	int id;
	@Column(name ="username")
	String username;
	@Column(name ="phoneno")
	String phoneno;
	@Column(name ="email")
	String email;
	@Column(name ="password")
	String password;
	@Column(name ="role")
	String role;

	public User(String username, String phoneno, String email, String password, String role) {
		this.username = username;
		this.phoneno = phoneno;
		this.email = email;
		this.password = password;
		this.role = role;
	}
}
