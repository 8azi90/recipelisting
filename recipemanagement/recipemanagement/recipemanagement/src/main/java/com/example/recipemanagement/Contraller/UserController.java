package com.example.recipemanagement.Contraller;

import com.example.recipemanagement.Model.User;
import com.example.recipemanagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

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

        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "Username already exists.";
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
