package com.securitydemo.Entity; // Defines the package location of this class

// Importing necessary Java and Spring Security classes
import java.util.Collection; // For handling collections
import java.util.Collections; // For creating immutable collections

import org.springframework.security.core.GrantedAuthority; // Represents an authority granted to the user
import org.springframework.security.core.authority.SimpleGrantedAuthority; // A simple implementation of GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails; // Core interface representing user information

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * This class wraps around the application's Users entity to provide user information to Spring Security.
 */
public class MyUserPrincipal implements UserDetails {

    private Users user; // The Users entity instance representing the authenticated user

    /**
     * Constructor that initializes MyUserPrincipal with a Users object.
     * @param user The Users entity containing user details.
     */
    public MyUserPrincipal(Users user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user.
     * In this implementation, every user is granted a single authority "USER".
     * @return A collection containing the granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Returns a singleton collection with a SimpleGrantedAuthority of "USER"
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    /**
     * Returns the password used to authenticate the user.
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        // Retrieves the password from the Users entity
        return user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user.
     * In this case, the user's email is used as the username.
     * @return The user's email.
     */
    @Override
    public String getUsername() {
        // Retrieves the email from the Users entity, used as the username
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account has expired.
     * @return true, indicating the account is non-expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        // Always returns true, meaning the account is never expired
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * @return true, indicating the account is not locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        // Always returns true, meaning the account is never locked
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     * @return true, indicating the credentials are non-expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        // Always returns true, meaning the credentials are never expired
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * @return true, indicating the user is enabled.
     */
    @Override
    public boolean isEnabled() {
        // Always returns true, meaning the user is always enabled
        return true;
    }
}
