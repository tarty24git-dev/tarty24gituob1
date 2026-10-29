package com.cth.datamatch.user;

import com.cth.datamatch.user.entity.User;
import com.cth.datamatch.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserManagementTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("TC001: Default admin user account should exist and be active")
    public void testDefaultAdminExists() {
        assertTrue(userService.findByUsername("admin").isPresent());
        User admin = userService.findByUsername("admin").get();
        assertEquals("ADMIN", admin.getRole());
        assertFalse(admin.isLocked());
    }

    @Test
    @DisplayName("TC002: Password policy validation should enforce complexity rules")
    public void testPasswordPolicy() {
        assertTrue(userService.validatePasswordPolicy("Qwerty123$"));
        assertFalse(userService.validatePasswordPolicy("simplepass"));
        assertFalse(userService.validatePasswordPolicy("SHORT1!"));
        assertFalse(userService.validatePasswordPolicy("NoSpecialSymbol123"));
    }

    @Test
    @DisplayName("TC003: User account lock/unlock toggling")
    public void testUserLockToggle() {
        User newUser = new User();
        newUser.setUsername("testuser_lock");
        newUser.setPassword("Qwerty123$");
        newUser.setEmail("testlock@cth.com");
        newUser.setFullName("Test Lock User");
        newUser.setRole("USER");

        User registered = userService.registerUser(newUser);
        assertFalse(registered.isLocked());

        userService.toggleUserLock(registered.getId());
        User lockedUser = userService.findByUsername("testuser_lock").get();
        assertTrue(lockedUser.isLocked());

        userService.toggleUserLock(registered.getId());
        User unlockedUser = userService.findByUsername("testuser_lock").get();
        assertFalse(unlockedUser.isLocked());
    }
}
