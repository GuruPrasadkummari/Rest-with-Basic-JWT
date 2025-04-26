package com.securitydemo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users{
	@Column(name = "user_name")
	private String username;
	@Id
	private String email;
	private String password;
	private String role;
	private String address1;
	private String address2;
}