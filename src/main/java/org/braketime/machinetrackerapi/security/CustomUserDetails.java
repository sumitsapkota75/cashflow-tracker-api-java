package org.braketime.machinetrackerapi.security;

import lombok.Getter;
import org.braketime.machinetrackerapi.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final String role;

    public CustomUserDetails(User user, String role){
        this.user = user;
        this.role = role.toUpperCase(); // normalize to uppercase
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        // Always prefix ROLE_ to match Spring Security convention
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash(); // BCrypt hashed password
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return user.isActive(); }
}
