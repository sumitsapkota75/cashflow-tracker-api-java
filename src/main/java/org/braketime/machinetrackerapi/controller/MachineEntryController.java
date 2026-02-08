package org.braketime.machinetrackerapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.MachineEntryRequest;
import org.braketime.machinetrackerapi.Dtos.MachineEntryResponse;
import org.braketime.machinetrackerapi.domain.MachineEntry;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.braketime.machinetrackerapi.services.MachineEntryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/machine-entry")
public class MachineEntryController {

    final MachineEntryService machineEntryService;
    final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MachineEntryResponse> createMachineEntry(
            @RequestPart("payload") String payload,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
            ){
        String username = SecurityUtils.username();
        MachineEntryRequest request;
        try {
            request = objectMapper.readValue(payload, MachineEntryRequest.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid machine entry payload JSON", ex);
        }
        return ResponseEntity.ok(machineEntryService.createEntry(request, files, username));
    }
    @GetMapping("/{periodID}")
    public ResponseEntity<List<MachineEntryResponse>> getEntriesForPeriod(
            @PathVariable String periodID,
            @RequestParam(required = false) String businessIdParam,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        log.info("Entry list API");
        String businessId;
        String role = SecurityUtils.role();
        if (!"OWNER".equals(role)) {
            // Non-owners must always use their own businessId
            businessId = SecurityUtils.businessId();
        } else {
            // Owner must provide businessId as query param
            if (businessIdParam == null || businessIdParam.isEmpty()) {
                throw new NotFoundException("OWNER must provide businessId as query parameter");
            }
            businessId = businessIdParam;
        }
        log.info("Resolved businessId={}", businessId);

        return ResponseEntity.ok(machineEntryService.getEntriesForPeriod(periodID,businessId,startDate,endDate));

    }

    @GetMapping("/recent")
    public ResponseEntity<MachineEntry> getRecentMachineEntry(
            @RequestParam String periodId,
            @RequestParam String machineId
    ){
        return ResponseEntity.ok(machineEntryService.getLastMachineEntryForPeriod(periodId,machineId));
    }

}
