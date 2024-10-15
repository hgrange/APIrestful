package com.herve.petstore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity 
@Table(name="USERS")
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
@NamedQuery(name = "User.findUser", query = "SELECT u FROM User u WHERE "
    + "u.username = :username ")
public class User {
	@Column(name="ID") int id;
	@Id
	@Column(name="USERNAME") String username;
	@Column(name="FIRSTNAME") String firstName;
	@Column(name="LASTNAME")String lastName;
	@Column(name="EMAIL")String email;
	@Column(name="PASSWORD")String password;
	@Column(name="PHONE")String phone;
	@Column(name="USERSTATUS") int userStatus;
	
	public User() {
	}
	
	User(int id, String username, String firstName, String lastName, String email, String password, String phone, int userStatus) {
		setId(id);
		setUsername(username);
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
		setPassword(password);
		setPhone(phone);
		setUserStatus(userStatus);
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
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}
	public String toString() {
        return "User [ id="+getId()+", username="+getUsername()+", firstname="+getFirstName()+
        		", lastname="+getLastName()+", email="+getEmail()+", password=******, phone="+getPhone()+
        		", userStatus="+getUserStatus();
                
    }

}
