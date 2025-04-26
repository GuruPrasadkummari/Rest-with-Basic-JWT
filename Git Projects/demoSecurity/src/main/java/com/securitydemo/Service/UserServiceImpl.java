package com.securitydemo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.securitydemo.Entity.Users;
import com.securitydemo.Repository.UserRepository;
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthenticationManager authmanager;
	@Autowired
	private JWTService jwtService;

	private BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(12);	
	
	@Override
	public List<Users> getAllUsers() {
		return userRepository.findAll() ;
	}

	@Override
	public Optional<Users> getUserByEmail(String email) {
//		if(userRepository.findById(email).isEmpty()) {
//			throw new UserNotFoundException("User Not Found By Provided Email");
//
//		}
		return userRepository.findById(email);
	}

	@Override
	public Users saveUser(Users user) {
		user.setPassword(encoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public Users updateUser(Users user) {
		return userRepository.save(user);
	}

	@Override
	public void deleteUserByEmail(String email) {
		if (userRepository.existsById(email)) {
			userRepository.deleteById(email);
		} else {
			throw new RuntimeException("User not found with provided email : " + email);
		}
	}

	public String verify(Users user) {
		Authentication authentication = authmanager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
		if(authentication.isAuthenticated())
			return    jwtService.generateToken(user.getEmail());
		return "fail";
	}

}
