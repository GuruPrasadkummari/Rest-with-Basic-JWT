package com.securitydemo.Service; // Declares the package location of this class

// Importing required classes
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service // Marks this class as a Spring Service for dependency injection
public class JWTService {

    private String secretKey = ""; // Variable to store the secret key for signing tokens

    // Constructor to auto-generate a secret key during service creation
    public JWTService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256"); // Create a key generator for HMAC SHA-256
            SecretKey sk = keyGen.generateKey(); // Generate the secret key
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded()); // Encode the key as a Base64 string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // If algorithm is not found, throw a runtime exception
        }
    }

    // Method to generate a JWT token based on the user's email
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>(); // Create an empty set of claims (can add custom claims if needed)
        return Jwts.builder() // Start building the JWT
                .claims() // Set claims
                .add(claims)
                .subject(email) // Set the subject (the user's email)
                .issuedAt(new Date(System.currentTimeMillis())) // Set the current time as issued time
                .expiration(new Date(System.currentTimeMillis() + 100 * 60 * 30)) // Set token expiration time (current + 30 minutes)
                .and()
                .signWith(getKey()) // Sign the token with secret key
                .compact(); // Build the token into a compact, URL-safe string
    }

    // Helper method to decode the secret key into a SecretKey object
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Decode the Base64 secret key
        return Keys.hmacShaKeyFor(keyBytes); // Generate HMAC-SHA key for signing tokens
    }

    // Method to extract the email (subject) from a JWT token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject); // Extracts the subject field from the claims
    }

    // Generic method to extract a claim from a token using a resolver function
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token); // Extract all claims first
        return claimResolver.apply(claims); // Apply the resolver to extract specific claim
    }

    // Method to extract all claims from the JWT token
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // Create a JWT parser
                .verifyWith(getKey()) // Set the key to verify the signature
                .build()
                .parseSignedClaims(token) // Parse the token and validate it
                .getPayload(); // Get the payload part (claims)
    }

    // Method to validate the token against user details (checks email and expiration)
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token); // Extract the email from the token
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Return true if emails match and token is not expired
    }

    // Method to check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // Check if expiration date is before the current time
    }

    // Method to extract the expiration date from the token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); // Extract expiration claim
    }
}
