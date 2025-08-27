package org.bialger.gateway.services.security;

import org.bialger.gateway.entities.User;
import org.bialger.gateway.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("userSecurity")
public class UserSecurity {

    private static final Logger log = LoggerFactory.getLogger(UserSecurity.class);
    private final UserRepository userRepository;

    @Autowired
    public UserSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isSelf(Authentication authentication, Long userId) {
        if (userId == null) {
            return false;
        }

        Optional<User> userOptional = extractUser(authentication);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean result = userId.equals(user.getId());
            log.debug("UserSecurity.isSelf for user '{}' (ID {}) and target userId {}: {}", user.getUsername(), user.getId(), userId, result);
            return result;
        }

        log.debug("UserSecurity.isSelf: User not found from authentication for target userId {}", userId);

        return false;
    }

    private Optional<User> extractUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        String username = null;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        if (username != null) {
            return userRepository.findByUsername(username);
        }

        return Optional.empty();
    }
}
