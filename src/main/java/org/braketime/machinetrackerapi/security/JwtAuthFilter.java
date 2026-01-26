package org.braketime.machinetrackerapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.extractClaims(token);
            String email = claims.getSubject();
            String userId = claims.get("userId", String.class);
            String role = claims.get("role", String.class);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                JwtUserPrincipal principal = new JwtUserPrincipal(
                        userId,
                        email,
                        role,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            chain.doFilter(request, response);
        }
        catch (ExpiredJwtException e) {
            sendError(response, request, HttpStatus.UNAUTHORIZED,
                    "Token expired", "Please login again.");
        }
        catch (JwtException e) {
            sendError(response, request, HttpStatus.UNAUTHORIZED,
                    "Invalid token", "Authentication token is invalid.");
        }
        catch (Exception e) {
            sendError(response, request, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Authentication error", "Authentication failed.");
        }
    }

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
