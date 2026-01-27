package org.braketime.machinetrackerapi.controller;


import lombok.AllArgsConstructor;
import org.braketime.machinetrackerapi.Dtos.WinnerPayoutCreateRequest;
import org.braketime.machinetrackerapi.domain.WinnerPayout;
import org.braketime.machinetrackerapi.services.WinnerPayoutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/payouts")
public class WinnerPayoutController {

    private final WinnerPayoutService winnerPayoutService;

    @GetMapping("/{id}")
    public ResponseEntity<List<WinnerPayout>> getAllPayoutForWinner(
            @PathVariable String id
    ){
        return ResponseEntity.ok(winnerPayoutService.getPayoutsByWinnerId(id));
    }

    @PostMapping
    public  ResponseEntity<WinnerPayout> cretePayout(@RequestBody WinnerPayoutCreateRequest request){
        return ResponseEntity.ok(winnerPayoutService.createPayout(request));
    }
    @GetMapping("/all")
    public  ResponseEntity<Page<WinnerPayout>> getPayoutForBusiness(Pageable pageable){
        return ResponseEntity.ok(winnerPayoutService.getPayoutsByBusinessId(pageable));
    }
}
