package org.braketime.machinetrackerapi.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.OpenPeriodRequest;
import org.braketime.machinetrackerapi.Dtos.PeriodResponse;
import org.braketime.machinetrackerapi.domain.Period;
import org.braketime.machinetrackerapi.mapper.PeriodMapper;
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
    final PeriodMapper periodMapper;

    @GetMapping("/{id}")
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
        return ResponseEntity.ok(periodService.openPeriod(request,"1234"));
    }
}
