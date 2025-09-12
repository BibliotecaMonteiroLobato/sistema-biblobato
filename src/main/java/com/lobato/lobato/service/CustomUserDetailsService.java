package com.lobato.lobato.service;

import com.lobato.lobato.model.User;
import com.lobato.lobato.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InMemoryUserService inMemoryUserService;
    
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Try MongoDB first
            User user = userRepository.findByUsername(username)
                    .orElse(null);
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            System.out.println("MongoDB error, falling back to in-memory: " + e.getMessage());
        }
        
        // Fall back to in-memory service
        User user = inMemoryUserService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user;
    }
    
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            System.out.println("MongoDB save error, falling back to in-memory: " + e.getMessage());
            return inMemoryUserService.save(user);
        }
    }
    
    public boolean existsByUsername(String username) {
        try {
            return userRepository.existsByUsername(username);
        } catch (Exception e) {
            return inMemoryUserService.existsByUsername(username);
        }
    }
    
    public boolean existsByEmail(String email) {
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            return inMemoryUserService.existsByEmail(email);
        }
    }
}