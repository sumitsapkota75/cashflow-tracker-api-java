package org.braketime.machinetrackerapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.MachineEntryRequest;
import org.braketime.machinetrackerapi.Dtos.MachineEntryResponse;
import org.braketime.machinetrackerapi.services.MachineEntryService;
import org.braketime.machinetrackerapi.services.PeriodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/machine-entry")
public class MachineEntryController {

    final MachineEntryService machineEntryService;

    @PostMapping
    public ResponseEntity<MachineEntryResponse> createMachineEntry(
            @RequestBody MachineEntryRequest request
            ){
        return ResponseEntity.ok(machineEntryService.createEntry(request));
    }

}
