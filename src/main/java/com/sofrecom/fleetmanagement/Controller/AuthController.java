package com.sofrecom.fleetmanagement.Controller; // matches your existing folder casing

import com.sofrecom.fleetmanagement.model.User;
import com.sofrecom.fleetmanagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Endpoints expected by the frontend (fleet-manager.html):
 *   POST /api/auth/login    { username, password }  -> 200 {id, username, role} | 401
 *   POST /api/auth/register { username, password }  -> 201 {id, username, role} | 409 (username taken)
 *
 * Uses your existing User entity (com.sofrecom.fleetmanagement.model.User)
 * and UserRepository (com.sofrecom.fleetmanagement.Repository.UserRepository).
 * Roles in your schema: ADMIN / MANAGER / VIEWER.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin // TODO: restrict allowed origins in production
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Map<String, Object> toUserResponse(User u) {
        // Explicit HashMap<String, Object> avoids the Map.of(...) type-inference
        // error you get when mixing String and Long/other value types.
        Map<String, Object> response = new HashMap<>();
        response.put("id", u.getId());
        response.put("username", u.getUsername());
        response.put("role", u.getRole());
        return response;
    }

    private boolean passwordMatches(User user, String rawPassword) {
        String storedPassword = user.getPassword();
        if (storedPassword == null) {
            return false;
        }

        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        if (storedPassword.equals(rawPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username") == null ? null : body.get("username").trim();
        String password = body.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("username and password are required");
        }

        return userRepository.findByUsername(username)
                .filter(u -> passwordMatches(u, password))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(toUserResponse(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username or password"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username") == null ? null : body.get("username").trim();
        String password = body.get("password");
        // Self-registration always creates the lowest-privilege role.
        // ADMIN / MANAGER accounts must be created manually (DB or by an admin).
        String role = "VIEWER";

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body("username and password are required");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        User user = new User(); // Lombok @Data gives you the setters below
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(toUserResponse(user));
    }
}
