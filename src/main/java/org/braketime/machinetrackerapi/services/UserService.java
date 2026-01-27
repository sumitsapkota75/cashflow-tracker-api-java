package org.braketime.machinetrackerapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.BusinessSummaryResponse;
import org.braketime.machinetrackerapi.Dtos.RegisterUserRequest;
import org.braketime.machinetrackerapi.Dtos.UserCreateResponse;
import org.braketime.machinetrackerapi.domain.Business;
import org.braketime.machinetrackerapi.domain.User;
import org.braketime.machinetrackerapi.exception.BadRequestException;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.repository.BusinessRepository;
import org.braketime.machinetrackerapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BusinessRepository businessRepository;

    public UserCreateResponse createUser(RegisterUserRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("Email already registered");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Optional<Business> business = businessRepository.findBusinessById(request.getBusinessId());
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .role(request.getRole())
                .businessId(request.getBusinessId())
                .businessName(business.get().getName())
                .active(true)
                .build();
        userRepository.save(user);
        return loadUsersWithBusiness(user);
    }

    // SINGLE PLACE that enriches users with business
    public UserCreateResponse loadUsersWithBusiness(User user){

        BusinessSummaryResponse businessSummary = null;
        if (user.getBusinessId() != null){
            Business business = businessRepository.findBusinessById(user.getBusinessId())
                    .orElseThrow(() -> new NotFoundException("Business not found"));
            businessSummary = new BusinessSummaryResponse(
                    business.getId(),
                    business.getName(),
                    business.getLocation(),
                    business.getCreatedAt(),
                    business.getNumberOfMachines()
            );
        }
        return new UserCreateResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getBusinessId(),
                user.isActive(),
                businessSummary
        );

    }

    public Page<UserCreateResponse> getAllUsers(Pageable pageable){
        Page<User> users = userRepository.findAllByActive(true, pageable);
        log.info("users in repo: {}",users.getTotalElements());
        return users.map(this::loadUsersWithBusiness);
    }

    public UserCreateResponse getUser(String id){
        User user = userRepository.findById(id)
                .orElseThrow(()->new NotFoundException("user not found"));

        return loadUsersWithBusiness(user);
    }
}
