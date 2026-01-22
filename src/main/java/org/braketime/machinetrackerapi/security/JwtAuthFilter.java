package org.braketime.machinetrackerapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("FILTER HIT 1");
        log.info(path);
        // <-- This is a String
        if (path.startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        log.info("FILTER HIT 2");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return; // No JWT present
        }

        String token = authHeader.substring(7);
        log.info("JWT token received: {}", token);

        String username = jwtUtil.extractUsername(token);
        if (username == null) {
            log.warn("JWT username could not be extracted");
            chain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return; // Already authenticated
        }

        var userDetails = userDetailsService.loadUserByUsername(username);
        log.info("Loaded UserDetails: {}, authorities: {}", userDetails.getUsername(), userDetails.getAuthorities());

        if (jwtUtil.isTokenValid(token)) { // validate username and expiration
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("JWT validated and authentication set in context for user: {}", username);
        } else {
            log.warn("Invalid JWT token for user: {}", username);
        }

        chain.doFilter(request, response);
    }
}
