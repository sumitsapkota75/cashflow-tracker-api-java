package org.braketime.machinetrackerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
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
        return ResponseEntity.ok(businessService.getAll(isActive,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponse> getBusinessById(
            @PathVariable String id
    ){
        return ResponseEntity.ok(businessService.getByID(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBusiness(
            @PathVariable String id,
            @RequestBody BusinessCreateUpdateRequest request
    ){
        return ResponseEntity.ok(businessService.update(id, request));
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
