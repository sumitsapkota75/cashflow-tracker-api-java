package org.braketime.machinetrackerapi.controller;


import java.util.List;

import org.apache.catalina.connector.Response;
import org.braketime.machinetrackerapi.Dtos.UpdatePaymentPlanRequest;
import org.braketime.machinetrackerapi.Dtos.WinnerCreateRequest;
import org.braketime.machinetrackerapi.domain.Winner;
import org.braketime.machinetrackerapi.services.WinnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/winners")
public class WinnerController {
    
    private final WinnerService winnerService;
    @GetMapping
    public ResponseEntity<List<Winner>> getAllWinnersByBusinessId() {
        return ResponseEntity.ok(winnerService.getWinnerByBusinessId());
    }

    @PostMapping
    public ResponseEntity<Winner> createWinner(@RequestBody WinnerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(winnerService.createWinner(request));
    }

    @PutMapping("/update-plan/{id}")
    public ResponseEntity<Winner> updatePaymentPlan(
            @PathVariable String id,
            @RequestBody UpdatePaymentPlanRequest request
            ){
        return  ResponseEntity.ok(winnerService.updatePaymentPlan(request,id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Winner> getWinnerById(
            @PathVariable String id
    ){
        return ResponseEntity.ok(winnerService.getWinnerById(id));
    }
    
}
