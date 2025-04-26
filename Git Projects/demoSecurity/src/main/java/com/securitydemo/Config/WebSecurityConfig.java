package com.securitydemo.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class configures Spring Security for the application.
 */
@Configuration // Marks this class as a Spring configuration
@EnableWebSecurity // Enables Spring Security support
public class WebSecurityConfig {

    // Inject the custom UserDetailsService (fetches user info from DB)
    @Autowired
    private UserDetailsService userDetailsService;

    // Inject the custom JWT Filter
    @Autowired
    private JwtFilter jwtFilter;

    /**
     * Define the security filter chain
     * - Disable CSRF
     * - Set public and secured endpoints
     * - Configure stateless session
     * - Add JWT filter before UsernamePasswordAuthenticationFilter
     */

    
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            // 1️⃣ Disable CSRF protection (good for APIs, not needed if token-based auth)
            .csrf(csrf -> csrf.disable())

            // 2️⃣ Define which endpoints are public and which need authentication
            .authorizeHttpRequests(request -> request
                .requestMatchers("/users/Register", "/users/login") // Allow register and login without auth
                .permitAll()
                .anyRequest()
                .authenticated() // Any other request must be authenticated
            )
//    		.httpBasic(Customizer.withDefaults())
//    		.formLogin(Customizer.withDefaults());

            // 3️⃣ Set session management to STATELESS (no server-side sessions, each request must carry token)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4️⃣ Set custom AuthenticationProvider (connect authentication to our userDetailsService and passwordEncoder)
            .authenticationProvider(authenticationProvider())

            // 5️⃣ Add our JWT filter before Spring's built-in UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 🔥 Finally, build and return the security filter chain
        return httpSecurity.build();
    }

    
 

//	@Bean
//	public AuthenticationManager authenticationManager(
//			UserDetailsService userDetailsService,
//			PasswordEncoder passwordEncoder) {
//		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//		authenticationProvider.setUserDetailsService(userDetailsService);
//		authenticationProvider.setPasswordEncoder(passwordEncoder);
//
//		return new ProviderManager(authenticationProvider);
//	}
//
////	@Bean
////	public UserDetailsService userDetailsService() {
////		UserDetails userDetails = User
////									.withUsername("prasad")
////									.password("{bcrypt}$2a$12$4DXaAHChi5bix4Cm1OYgDeqCoveoQDbEC6mJvYTu0evkZOrGcaiP6")//Prasad@123
////									.roles("USER")
////									.build();
////		return new InMemoryUserDetailsManager(userDetails);
////	}
//
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//	}

    /**
     * Define a custom AuthenticationProvider
     * - Links UserDetailsService with PasswordEncoder
     * - Used by Spring Security for user authentication
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12)); // Set password encoder with strength 12
        provider.setUserDetailsService(userDetailsService); // Set the custom user details service
        return provider;
    }

    /**
     * Define AuthenticationManager
     * - Used internally by Spring to manage authentication
     * - Retrieved from AuthenticationConfiguration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


	
	
//	
//	
//	@Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder()); // Use the proper encoder
//        provider.setUserDetailsService(userDetailsService);
//        return provider;
//    }
//	

}
