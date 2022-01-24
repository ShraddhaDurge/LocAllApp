package com.localapp.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
