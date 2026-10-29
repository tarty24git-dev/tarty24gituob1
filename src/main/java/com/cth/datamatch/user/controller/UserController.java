package com.cth.datamatch.user.controller;

import com.cth.datamatch.user.entity.User;
import com.cth.datamatch.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;

    @Value("${app.security.ldap.enabled:false}")
    private boolean ldapEnabled;

    @Value("${app.security.verification.enabled:false}")
    private boolean verificationEnabled;

    @Value("${app.name:DataMatch Studio}")
    private String appName;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("errorMessage", "Invalid username or password.");
        if (logout != null) model.addAttribute("successMessage", "You have been logged out successfully.");
        model.addAttribute("ldapEnabled", ldapEnabled);
        model.addAttribute("appName", appName);
        return "login";
    }

    @GetMapping("/users")
    public String usersPage(Model model, Authentication authentication) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("logs", userService.getRecentAuditLogs());
        model.addAttribute("ldapEnabled", ldapEnabled);
        model.addAttribute("verificationEnabled", verificationEnabled);
        model.addAttribute("appName", appName);
        model.addAttribute("currentUsername", authentication != null ? authentication.getName() : "");
        return "users";
    }

    @PostMapping("/api/users/register")
    @ResponseBody
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registered = userService.registerUser(user);
            return ResponseEntity.ok(Map.of("message", "User registered successfully!", "user", registered));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/users/{id}/lock")
    @ResponseBody
    public ResponseEntity<?> toggleLock(@PathVariable Long id) {
        try {
            userService.toggleUserLock(id);
            return ResponseEntity.ok(Map.of("message", "User lock status updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/users/{id}/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String newPassword = payload.get("newPassword");
            userService.resetPassword(id, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/users/verify")
    @ResponseBody
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String code = payload.get("code");
        boolean verified = userService.verifyAccount(username, code);
        if (verified) {
            return ResponseEntity.ok(Map.of("message", "Account verified successfully."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid verification code."));
        }
    }
}
