package org.braketime.machinetrackerapi.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.ClosePeriodRequest;
import org.braketime.machinetrackerapi.Dtos.OpenPeriodRequest;
import org.braketime.machinetrackerapi.Dtos.PeriodResponse;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.braketime.machinetrackerapi.services.PeriodService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/periods")
public class PeriodController {

    final PeriodService periodService;

    // Get all periods for business ID
    @GetMapping("business/{id}")
    public ResponseEntity<Page<PeriodResponse>> getAllPeriodById(
            @PathVariable String id,
            Pageable pageable
    ){
        return ResponseEntity.ok(periodService.getAllPeriod(id,pageable));
    }

    @PostMapping("/open")
    public ResponseEntity<PeriodResponse> openPeriod(
            @RequestBody OpenPeriodRequest request
            ){
        String userId = SecurityUtils.userId();
        return ResponseEntity.ok(periodService.openPeriod(request,userId));
    }
    @PostMapping("/close")
    public ResponseEntity<PeriodResponse> closePeriod(
            @RequestBody ClosePeriodRequest request
    ){
        String userId = SecurityUtils.userId();
        return ResponseEntity.ok(periodService.closePeriod(request,userId));
    }

    @GetMapping("/active/{businessId}")
    public ResponseEntity<PeriodResponse> getActivePeriod(
            @PathVariable String businessId
    ) {
        return ResponseEntity.ok(periodService.getActivePeriod(businessId));
    }

    // get single period by period id
    @GetMapping("/{id}")
    public ResponseEntity<PeriodResponse> getPeriodByPeriodId(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(periodService.getPeriodById(id));
    }
}
