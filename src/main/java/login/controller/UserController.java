package login.controller;

import login.config.JwtUtil;
import login.model.User;
import login.domain.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin()
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user, user.getPassword());
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/validate-email")
    public ResponseEntity<?> validateEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Email not found.");
        }
        return ResponseEntity.ok("Email is valid.");
    }
    // Updated Reset Password Endpoint to use RequestBody
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody User user) {
        boolean success = userService.resetPasswordByEmail(user.getEmail(), user.getPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successful.");
        }
        return ResponseEntity.badRequest().body("Password reset failed.");
    }

    // New Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User loggedInUser = userService.login(user.getEmail(), user.getPassword());
        if (loggedInUser == null) {
            return ResponseEntity.badRequest().body("Invalid email or password.");
        }

        // Generate JWT token
        String token = JwtUtil.generateToken(loggedInUser.getEmail());

        // You can return a DTO or map with the token
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", loggedInUser.getEmail()
        ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching users: " + e.getMessage());
        }
    }
}
