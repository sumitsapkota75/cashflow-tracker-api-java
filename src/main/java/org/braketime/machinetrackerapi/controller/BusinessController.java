package org.braketime.machinetrackerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.BusinessCreateUpdateRequest;
import org.braketime.machinetrackerapi.Dtos.BusinessResponse;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.braketime.machinetrackerapi.services.BusinessService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/business")
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    public ResponseEntity<BusinessResponse> createBusiness(@Valid @RequestBody BusinessCreateUpdateRequest request){
        log.info("Business API Hit.....");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(businessService.createBusiness(request));
    }

    @GetMapping
    public ResponseEntity<Page<BusinessResponse>> getAllBusinesses(
            @RequestParam(defaultValue = "true") boolean isActive,
            Pageable pageable
    ){
        String userId = SecurityUtils.userId();
        String role = SecurityUtils.role();
        log.info("USER_ID {}",userId);
        log.info("Role {}",role);

        return ResponseEntity.ok(businessService.getAll(isActive,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponse> getBusinessById(
            @PathVariable String id
    ){
        return ResponseEntity.ok(businessService.getByID(id));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<?> deactiveBusiness(
            @PathVariable String id
    ){
        return ResponseEntity.ok(businessService.deActive(id));
    }
    @PutMapping("/{id}/enable")
    public ResponseEntity<?> reActiveBusiness(
            @PathVariable String id
    ){
        return ResponseEntity.ok(businessService.reActive(id));
    }
}
