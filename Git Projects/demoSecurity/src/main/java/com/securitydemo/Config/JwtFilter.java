package com.securitydemo.Config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.securitydemo.Service.JWTService;
import com.securitydemo.Service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This filter is responsible for:
 * - Checking if the incoming request has a JWT token
 * - Validating the token
 * - Setting the authentication context if the token is valid
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    // Inject JWT service to handle token extraction and validation
    @Autowired
    private JWTService jwtservice;

    // Inject ApplicationContext to get beans dynamically (like MyUserDetailsService)
    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header from incoming HTTP request
        String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        //   Check if Authorization header is present and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 3️⃣ Remove "Bearer " part and extract only the token
            token = authHeader.substring(7);

            //   Extract email (username) from the token
            email = jwtservice.extractEmail(token);
        }

        //  If email is extracted and no authentication is set yet
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details (like username, password, roles) from database using email
            UserDetails userDetails = 
                    context.getBean(MyUserDetailsService.class).loadUserByUsername(email);

            //  Validate the token by checking if it matches the user details
            if (jwtservice.validateToken(token, userDetails)) {

                //   If valid, create a UsernamePasswordAuthenticationToken
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null,
                                userDetails.getAuthorities() // roles/authorities
                        );

                //  Attach additional authentication details from HTTP request
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 🔟 Set the authentication into the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 🔥 Continue filter chain after processing authentication
        filterChain.doFilter(request, response);
    }
}
