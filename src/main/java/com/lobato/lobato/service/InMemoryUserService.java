package com.lobato.lobato.service;

import com.lobato.lobato.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class InMemoryUserService {
    
    private final Map<String, User> users = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public InMemoryUserService() {
        // Create a default test user
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setEnabled(true);
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);
        users.put("testuser", testUser);
        
        // Create simpleuser
        User simpleUser = new User();
        simpleUser.setUsername("simpleuser");
        simpleUser.setEmail("simple@example.com");
        simpleUser.setPassword(passwordEncoder.encode("password123"));
        simpleUser.setEnabled(true);
        simpleUser.setAccountNonExpired(true);
        simpleUser.setAccountNonLocked(true);
        simpleUser.setCredentialsNonExpired(true);
        users.put("simpleuser", simpleUser);
        
        // Create cu2 user
        User cu2User = new User();
        cu2User.setUsername("cu2@gmail.com");
        cu2User.setEmail("cu2@gmail.com");
        cu2User.setPassword(passwordEncoder.encode("12345678"));
        cu2User.setEnabled(true);
        cu2User.setAccountNonExpired(true);
        cu2User.setAccountNonLocked(true);
        cu2User.setCredentialsNonExpired(true);
        users.put("cu2@gmail.com", cu2User);
    }
    
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }
    
    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }
    
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }
    
    public User save(User user) {
        users.put(user.getUsername(), user);
        return user;
    }
    
    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }
}