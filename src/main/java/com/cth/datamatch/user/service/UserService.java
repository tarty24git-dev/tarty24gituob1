package com.cth.datamatch.user.service;

import com.cth.datamatch.user.entity.User;
import com.cth.datamatch.user.entity.UserActivityLog;
import com.cth.datamatch.user.repository.ActivityLogRepository;
import com.cth.datamatch.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.verification.enabled:false}")
    private boolean verificationEnabled;

    @Value("${app.security.verification.method:EMAIL}")
    private String verificationMethod;

    @Value("${app.password.expiration-days:90}")
    private int passwordExpirationDays;

    public UserService(UserRepository userRepository,
                       ActivityLogRepository activityLogRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean validatePasswordPolicy(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    @Transactional
    public User registerUser(User user) {
        if (!validatePasswordPolicy(user.getPassword())) {
            throw new IllegalArgumentException("Password does not meet complexity requirements (min 8 chars, uppercase, lowercase, digit, special char).");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());

        if (verificationEnabled) {
            user.setVerified(false);
            user.setVerificationCode(String.format("%06d", new Random().nextInt(900000) + 100000));
            // Demo mode notification log
            logActivity(user.getUsername(), "VERIFICATION_SENT",
                    "Verification code " + user.getVerificationCode() + " sent via " + verificationMethod + " (Demo Mode)", "127.0.0.1");
        } else {
            user.setVerified(true);
        }

        User saved = userRepository.save(user);
        logActivity(saved.getUsername(), "USER_REGISTERED", "User registered successfully", "127.0.0.1");
        return saved;
    }

    @Transactional
    public void toggleUserLock(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setLocked(!user.isLocked());
        userRepository.save(user);
        logActivity("admin", "TOGGLE_LOCK", "User " + user.getUsername() + " locked status set to " + user.isLocked(), "127.0.0.1");
    }

    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        if (!validatePasswordPolicy(newPassword)) {
            throw new IllegalArgumentException("New password does not meet security requirements.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordExpired(false);
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);
        logActivity("admin", "PASSWORD_RESET", "Password reset for user " + user.getUsername(), "127.0.0.1");
    }

    @Transactional
    public boolean verifyAccount(String username, String code) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {
            user.setVerified(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            logActivity(username, "ACCOUNT_VERIFIED", "Account verified successfully", "127.0.0.1");
            return true;
        }
        return false;
    }

    public void logActivity(String username, String action, String details, String ipAddress) {
        activityLogRepository.save(new UserActivityLog(username, action, details, ipAddress));
    }

    public List<UserActivityLog> getRecentAuditLogs() {
        return activityLogRepository.findTop50ByOrderByCreatedAtDesc();
    }
}
