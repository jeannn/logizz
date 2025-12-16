package com.example.logizz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.logizz.model.User;

import jakarta.annotation.PostConstruct;

@Service
public class UserService implements UserDetailsService {
    
    //in memory storage for users using map
    Map<String,User> users = new HashMap<>();

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void registerUser(String username, String email, String password, String role) {
        if(users.containsKey(username)){
            throw new IllegalArgumentException("Username already exists");
        } else {
            String encodedPassword = passwordEncoder.encode(password);
            User newUser = new User(username, email, encodedPassword, role);
            users.put(username, newUser);
        }
        
    }

    /*returns user Details object when provided with username and password, 
        exeption raised if username not found or password is invalid
    */
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
        
        
    }

    @PostConstruct
    public void seedAdmin() {
        if (!users.containsKey("admin")) {
            users.put("admin", new com.example.logizz.model.User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode("adminpass1"),
                "ADMIN"
            ));
        }
    }

}
