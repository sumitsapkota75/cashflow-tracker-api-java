package org.braketime.machinetrackerapi.controller;


import java.util.List;

import org.braketime.machinetrackerapi.Dtos.WinnerCreateRequest;
import org.braketime.machinetrackerapi.domain.Winner;
import org.braketime.machinetrackerapi.services.WinnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
}
