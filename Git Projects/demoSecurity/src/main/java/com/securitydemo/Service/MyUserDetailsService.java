package com.securitydemo.Service; // Package declaration

// Importing necessary classes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.securitydemo.Entity.MyUserPrincipal;
import com.securitydemo.Entity.Users;
import com.securitydemo.Repository.UserRepository;

@Service // Marks this class as a Spring Service, so Spring will detect and create a bean of it
public class MyUserDetailsService implements UserDetailsService { // Implements UserDetailsService interface provided by Spring Security

    @Autowired // Automatically injects the UserRepository dependency
    private UserRepository userrepo;

    /**
     * This method is called automatically by Spring Security whenever it needs
     * to authenticate a user based on their username (email in this case).
     *
     * @param email the username/email used for login
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user from database using email
        Users user = userrepo.findByEmail(email);

        // If user is not found in the database
        if(user == null) {
            System.out.println("User Not Found"); // Print a debug message to console
            throw new UsernameNotFoundException("User Not Found"); // Throw an exception (Spring will catch it and fail authentication)
        }

        // If user is found, wrap it inside a MyUserPrincipal object and return it
        return new MyUserPrincipal(user);
    }

}
