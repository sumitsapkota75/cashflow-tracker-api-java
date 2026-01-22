package org.braketime.machinetrackerapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🔹 Skip authentication for auth endpoints
        if (path.startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // 🔹 No token → just continue (public or unauthenticated request)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            log.debug("JWT received for path {}: {}", path, token);

            String username = jwtUtil.extractUsername(token);

            if (username == null) {
                log.warn("JWT username could not be extracted");
                sendError(response, request,
                        HttpStatus.UNAUTHORIZED,
                        "Invalid token",
                        "Authentication token is invalid.");
                return;
            }

            // 🔹 Already authenticated → skip
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                chain.doFilter(request, response);
                return;
            }

            var userDetails = userDetailsService.loadUserByUsername(username);
            log.debug("Loaded user: {} with roles {}", userDetails.getUsername(), userDetails.getAuthorities());

            if (!jwtUtil.isTokenValid(token)) {
                log.warn("JWT validation failed for user {}", username);
                sendError(response, request,
                        HttpStatus.UNAUTHORIZED,
                        "Invalid token",
                        "Authentication token is invalid.");
                return;
            }

            // 🔹 Set authentication into context
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("JWT authentication successful for user {}", username);

            chain.doFilter(request, response);
        }

        // 🔴 TOKEN EXPIRED
        catch (ExpiredJwtException ex) {
            log.warn("JWT EXPIRED for {} → {}", path, ex.getMessage());
            sendError(response, request,
                    HttpStatus.UNAUTHORIZED,
                    "Token expired",
                    "Your session has expired. Please login again.");
        }

        // 🔴 INVALID TOKEN / SIGNATURE / FORMAT
        catch (JwtException ex) {
            log.warn("JWT INVALID for {} → {}", path, ex.getMessage());
            sendError(response, request,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token",
                    "Authentication token is invalid.");
        }

        // 🔴 ANY OTHER SECURITY FAILURE
        catch (Exception ex) {
            log.error("JWT FILTER ERROR on path {}", path, ex);
            sendError(response, request,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Authentication error",
                    "Authentication processing failed.");
        }
    }

    // 🔹 Sends clean JSON error response instead of stacktrace
    private void sendError(HttpServletResponse response,
                           HttpServletRequest request,
                           HttpStatus status,
                           String error,
                           String message) throws IOException {

        ErrorResponse body = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
