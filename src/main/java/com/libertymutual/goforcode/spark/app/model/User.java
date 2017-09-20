package com.libertymutual.goforcode.spark.app.model;

import org.javalite.activejdbc.Model;

public class User extends Model {
	
	public User() {}
	
	public User(String firstName, String lastName, String email, String password) {
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
		setPassword(password);
	}
		
	
	public String getFirstName() {
		return getString("first_Name");
	}
	public void setFirstName(String firstName) {
		set("first_Name", firstName);
	}
	
	
	public String getLastName() {
		return getString("last_Name");
	}
	public void setLastName(String lastName) {
		set("last_Name", lastName);
	}	
	
	
	public String getEmail() {
		return getString("email");
	}
	public void setEmail(String email) {
		set("email", email);
	}	
		
	public String getPassword() {
		return getString("password");
	}
	public void setPassword(String password) {
		set("password", password);
	}

}
