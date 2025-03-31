package com.springboot.bankapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.springboot.bankapp.service.AccountService;

// annotation configuration is used to define beans and configuration settings for the Spring application context
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    AccountService accountService;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        // BCryptPasswordEncoder is a password encoder that uses the bcrypt hashing algorithm
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            // CSRF = Cross-Site Request Forgery
            // CSRF is a type of attack where a malicious website tricks a user's browser into making requests to another site
            // CSRF is enabled by default in Spring Security to protect against CSRF attacks
            // in this case, we are disabling it for simplicity
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/styles/**","/register").permitAll() // allow access to these endpoints without authentication
                .anyRequest().authenticated() // require authentication for any other request
            )
            .formLogin(form -> form
                .loginPage("/login") // specify the custom login page
                .loginProcessingUrl("/login") // specify the URL to handle the login form submission
                .defaultSuccessUrl("/dashboard", true) // redirect to the dashboard after successful login
                .permitAll() // allow access to the login page for all users
            )
            .logout(logout -> logout
                .invalidateHttpSession(true) // invalidate the session on logout so that the user is logged out
                .clearAuthentication(true) // clear authentication
                // antpathrequestmatcher is used to match the URL pattern for the logout request which means it will match any URL that starts with /logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // specify the URL to handle the logout request
                .logoutSuccessUrl("/login?logout") // redirect to the login page after logout
                .permitAll() // allow access to the logout page for all users
            )
            .headers(header -> header
                // allow frames from the same origin, frames is used to embed another HTML document within the current HTML document,
                // same origin means that the frame can only be loaded from the same domain as the current page
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) 

            );

        return http.build();
            
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // throws an exception if the authentication manager cannot be configured
        // userDetailsService is used to specify the service that will be used to load user details
        // passwordEncoder is used to specify the password encoder that will be used to encode passwords
        auth.userDetailsService(accountService).passwordEncoder(passwordEncoder());
    }
}