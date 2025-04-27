package com.example.recipemanagement.Controller;

import com.example.recipemanagement.Model.User;
import com.example.recipemanagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.regex.Pattern;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // For password encryption (optional)
    // private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return "Username and password are required.";
        }

        // Check if the username already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "Username already exists.";
        }

        // Password validation: must be at least 8 characters, include letters, and include special characters
        String password = user.getPassword();
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }

        // Regular expression for password: at least one letter and one special character
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        if (!Pattern.matches(passwordRegex, password)) {
            return "Password must contain at least one letter and one special character.";
        }

        // Manual auto-incrementing ID (as String)
        long count = userRepository.count();
        user.setId(String.valueOf(count + 1));  // ID will be "1", "2", "3", ...

        userRepository.save(user);
        return "User created successfully with ID: " + user.getId();
    }

    @PostMapping("/login")
    public Object login(@RequestBody User loginUser, HttpSession session) {
        User user = userRepository.findByUsername(loginUser.getUsername());

        if (user != null) {
            // If using encrypted passwords:
            // if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) { ... }

            if (user.getPassword().equals(loginUser.getPassword())) {
                session.setAttribute("user", user); // Store user in session
                return new java.util.HashMap<String, Object>() {{
                    put("success", true);
                    put("user", user.getUsername());
                }};
            }
        }

        return new java.util.HashMap<String, Object>() {{
            put("success", false);
        }};
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out successfully.";
    }
}
