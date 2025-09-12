package com.lobato.lobato.controller;

import com.lobato.lobato.model.User;
import com.lobato.lobato.service.CustomUserDetailsService;
import com.lobato.lobato.service.InMemoryUserService;
import com.lobato.lobato.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

import jakarta.validation.Valid;
import java.security.Principal;

@Controller
public class AuthController {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InMemoryUserService inMemoryUserService;
    
    @GetMapping("/")
    public String home() {
        return "home";
    }
    
    @GetMapping("/home")
    public String homePage() {
        return "home";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully!");
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult bindingResult, 
                              Model model) {
        
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        if (userDetailsService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }
        
        if (userDetailsService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already exists!");
            return "register";
        }
        
        userDetailsService.saveUser(user);
        model.addAttribute("message", "User registered successfully! Please login.");
        return "login";
    }
    
    @GetMapping("/create-simple-user")
    @ResponseBody
    public String createSimpleUser() {
        try {
            // Create user with all required fields
            User user = new User();
            user.setUsername("simpleuser");
            user.setEmail("simple@example.com"); 
            user.setPassword("password123");
            
            // Set all UserDetails fields explicitly
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            
            User savedUser = userDetailsService.saveUser(user);
            return "User created successfully! ID: " + savedUser.getId() + 
                   ". Try login with username: simpleuser, password: password123";
        } catch (Exception e) {
            return "Error: " + e.getMessage() + " - " + e.getClass().getSimpleName();
        }
    }
    
    @GetMapping("/list-all-users")
    @ResponseBody
    public String listAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            StringBuilder response = new StringBuilder();
            response.append("Total users found in MongoDB: ").append(users.size()).append("<br><br>");
            
            for (User user : users) {
                response.append("Username: ").append(user.getUsername()).append("<br>");
                response.append("Email: ").append(user.getEmail()).append("<br>");
                response.append("Enabled: ").append(user.isEnabled()).append("<br>");
                response.append("Roles: ").append(user.getRoles()).append("<br>");
                response.append("Password (first 20 chars): ").append(user.getPassword().substring(0, Math.min(20, user.getPassword().length()))).append("...<br>");
                response.append("---<br><br>");
            }
            
            return response.toString();
        } catch (Exception e) {
            // Fall back to in-memory service
            StringBuilder response = new StringBuilder();
            response.append("MongoDB error (").append(e.getMessage()).append("), showing in-memory users:<br><br>");
            
            // Access in-memory users through the service
            try {
                java.util.Map<String, User> inMemoryUsers = inMemoryUserService.getAllUsers();
                response.append("Total users found in memory: ").append(inMemoryUsers.size()).append("<br><br>");
                
                for (User user : inMemoryUsers.values()) {
                    response.append("Username: ").append(user.getUsername()).append("<br>");
                    response.append("Email: ").append(user.getEmail()).append("<br>");
                    response.append("Enabled: ").append(user.isEnabled()).append("<br>");
                    response.append("Password (first 20 chars): ").append(user.getPassword().substring(0, Math.min(20, user.getPassword().length()))).append("...<br>");
                    response.append("---<br><br>");
                }
            } catch (Exception ex) {
                response.append("Error accessing in-memory users: ").append(ex.getMessage());
            }
            
            return response.toString();
        }
    }
    
    @GetMapping("/test-mongodb-connection")
    @ResponseBody
    public String testMongoConnection() {
        StringBuilder response = new StringBuilder();
        response.append("=== MongoDB Connection Test ===<br><br>");
        
        try {
            // Test 1: Basic connection
            response.append("1. Testing basic connection...<br>");
            long userCount = userRepository.count();
            response.append("✅ Connection successful! Found ").append(userCount).append(" users<br><br>");
            
            // Test 2: Test write operation
            response.append("2. Testing write operation...<br>");
            User testUser = new User();
            testUser.setUsername("connectiontest_" + System.currentTimeMillis());
            testUser.setEmail("test@connection.com");
            testUser.setPassword("testpass");
            testUser.setEnabled(true);
            testUser.setAccountNonExpired(true);
            testUser.setAccountNonLocked(true);
            testUser.setCredentialsNonExpired(true);
            
            User savedUser = userRepository.save(testUser);
            response.append("✅ Write successful! Created user with ID: ").append(savedUser.getId()).append("<br><br>");
            
            // Test 3: Test read operation
            response.append("3. Testing read operation...<br>");
            java.util.Optional<User> foundUser = userRepository.findByUsername(testUser.getUsername());
            if (foundUser.isPresent()) {
                response.append("✅ Read successful! Found user: ").append(foundUser.get().getUsername()).append("<br><br>");
            } else {
                response.append("❌ Read failed! Could not find created user<br><br>");
            }
            
            // Test 4: Test delete operation
            response.append("4. Testing delete operation...<br>");
            userRepository.delete(testUser);
            response.append("✅ Delete successful!<br><br>");
            
            response.append("🎉 All MongoDB operations working correctly!");
            
        } catch (Exception e) {
            response.append("❌ MongoDB Connection Failed:<br>");
            response.append("Error: ").append(e.getClass().getSimpleName()).append("<br>");
            response.append("Message: ").append(e.getMessage()).append("<br>");
            response.append("Cause: ").append(e.getCause() != null ? e.getCause().getMessage() : "N/A").append("<br><br>");
            
            if (e.getMessage().contains("command insert not found")) {
                response.append("🚨 <strong>Atlas SQL Issue Detected!</strong><br>");
                response.append("Your MongoDB Atlas appears to be configured as Atlas SQL, not regular MongoDB.<br>");
                response.append("Please create a proper MongoDB cluster instead of Atlas SQL.<br><br>");
            }
            
            response.append("Falling back to in-memory storage...");
        }
        
        return response.toString();
    }
    
    @GetMapping("/test-user")
    @ResponseBody
    public String createTestUser() {
        User testUser = new User();
        testUser.setUsername("cu2");
        testUser.setEmail("cu2@example.com");
        testUser.setPassword("12345678");
        
        try {
            userDetailsService.saveUser(testUser);
            return "Test user 'cu2' created successfully! You can now login with username: cu2, password: 12345678";
        } catch (Exception e) {
            return "Error creating user: " + e.getMessage();
        }
    }
    
    @GetMapping("/debug-users")
    @ResponseBody
    public String debugUsers() {
        try {
            // Check if user exists by username
            boolean existsByUsername = userDetailsService.existsByUsername("cu2");
            
            // Try to load user details
            String userFound = "not found";
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername("cu2");
                userFound = "found: " + userDetails.getUsername();
            } catch (Exception e) {
                userFound = "error: " + e.getMessage();
            }
            
            return "User cu2 exists by username: " + existsByUsername + ", Load user: " + userFound;
        } catch (Exception e) {
            return "Debug error: " + e.getMessage();
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "dashboard";
    }
    
    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "profile";
    }
    
    @GetMapping("/settings")
    public String settings(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "settings";
    }
    
    @GetMapping("/reports")
    public String reports(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "reports";
    }
}