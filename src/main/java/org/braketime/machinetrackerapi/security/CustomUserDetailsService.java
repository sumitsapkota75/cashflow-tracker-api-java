package org.braketime.machinetrackerapi.security;

import lombok.RequiredArgsConstructor;
import org.braketime.machinetrackerapi.domain.Role;
import org.braketime.machinetrackerapi.domain.User;
import org.braketime.machinetrackerapi.repository.RoleRepository;
import org.braketime.machinetrackerapi.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found."));
        return new CustomUserDetails(user,user.getRole());
    }

}
