package com.securitydemo.Service;

import java.util.List;
import java.util.Optional;

import com.securitydemo.Entity.Users;


public interface UserService {

	List<Users>getAllUsers();
	Optional<Users> getUserByEmail(String email);
	Users saveUser(Users user);
	Users  updateUser(Users user);
	void deleteUserByEmail(String email);

}
