package org.braketime.machinetrackerapi.security;


import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static JwtUserPrincipal currentUser() {
        return (JwtUserPrincipal)
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
    }

    public static String userId() {
        return currentUser().getUserId();
    }

    public static String role() {
        return currentUser().getRole();
    }
}

