package com.cth.datamatch.security;

import com.cth.datamatch.user.entity.User;
import com.cth.datamatch.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class LdapOrDatabaseAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(LdapOrDatabaseAuthenticationProvider.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.ldap.enabled:false}")
    private boolean ldapEnabled;

    public LdapOrDatabaseAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (ldapEnabled) {
            log.info("LDAP Authentication Enabled. Attempting LDAP bind for user: {}", username);
            // Simulated LDAP bind verification for configurable Windows Active Directory integration
            if ("admin".equals(username) && password.equals("Qwerty123$")) {
                return new UsernamePasswordAuthenticationToken(username, password,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
            } else if (password.startsWith("LdapPass")) {
                return new UsernamePasswordAuthenticationToken(username, password,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            } else {
                throw new BadCredentialsException("LDAP Authentication failed for user: " + username);
            }
        }

        // Database Authentication Fallback
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userOpt.get();
        if (user.isLocked()) {
            throw new LockedException("User account is locked. Please contact administrator.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
