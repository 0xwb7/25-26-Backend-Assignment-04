package org.example.simplejwtexample.validator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserValidator {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static Long currentUserIDorNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        try {
            return Long.parseLong(authentication.getPrincipal().toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null &&
                authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(ROLE_ADMIN));
    }
}
