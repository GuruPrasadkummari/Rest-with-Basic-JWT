package com.securitydemo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.securitydemo.Entity.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, String> {

	Users findByUsername(String username);

	Users findByEmail(String email);
}
