-- Seed Data Script
-- Default Administrator Account
-- Default Password: Qwerty123$ (BCrypt encoded hash: $2a$10$e0MYzXyjpJS7Pd0RVvHwHe1z7xW2kCgA1b.K/6qG8T9mJ6qP4c8a2 -> or generated at startup if not present)

INSERT INTO USERS (USERNAME, PASSWORD, EMAIL, PHONE_NUMBER, FULL_NAME, ROLE, LOCKED, ENABLED, PASSWORD_EXPIRED, VERIFIED)
VALUES ('admin', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1z7xW2kCgA1b.K/6qG8T9mJ6qP4c8a2', 'admin@cth.com', '+15550199', 'System Administrator', 'ADMIN', 0, 1, 0, 1);
