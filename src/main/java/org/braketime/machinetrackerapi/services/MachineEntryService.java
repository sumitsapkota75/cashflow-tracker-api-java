package org.braketime.machinetrackerapi.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.BusinessRequest;
import org.braketime.machinetrackerapi.Dtos.MachineEntryRequest;
import org.braketime.machinetrackerapi.Dtos.MachineEntryResponse;
import org.braketime.machinetrackerapi.domain.MachineEntry;
import org.braketime.machinetrackerapi.domain.Period;
import org.braketime.machinetrackerapi.enums.PeriodStatus;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.mapper.MachineEntryMapper;
import org.braketime.machinetrackerapi.repository.MachineEntryRepository;
import org.braketime.machinetrackerapi.repository.PeriodRepositoy;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class MachineEntryService {

    private final PeriodRepositoy periodRepositoy;
    private final MachineEntryMapper machineEntryMapper;
    private final MachineEntryRepository machineEntryRepository;

    public MachineEntryResponse createEntry(MachineEntryRequest request, String username){

        String userId = SecurityUtils.userId();
        String businessId = SecurityUtils.businessId();
        Period openPeriod = periodRepositoy.findByBusinessIdAndStatus(businessId, PeriodStatus.OPEN)
                .orElseThrow(()-> new NotFoundException("No open period found"));

        MachineEntry machineEntry = machineEntryMapper.toEntity(request);

        machineEntry.setDifference(
                request.getNetFromReport().subtract(request.getPhysicalCash())
        );

        machineEntry.setBusinessId(businessId);
        machineEntry.setOpenedByUserId(userId);
        machineEntry.setPeriodId(openPeriod.getId());
        machineEntry.setOpenedAt(LocalDateTime.now());
        machineEntry.setUsername(username);

        machineEntryRepository.save(machineEntry);

        log.info(
                "MachineEntry created. entryId={}, periodId={}, machineId={}",
                machineEntry.getId(),
                machineEntry.getPeriodId(),
                machineEntry.getMachineId()
        );
        // Aggregate in Period data
        BigDecimal currentSafeDrop =
                openPeriod.getSafeDrop() == null
                        ? BigDecimal.ZERO
                        : openPeriod.getSafeDrop();

        BigDecimal machineSafeDrop =
                machineEntry.getSafeDroppedAmount() == null
                        ? BigDecimal.ZERO
                        : machineEntry.getSafeDroppedAmount();
        openPeriod.setSafeDrop(currentSafeDrop.add(machineSafeDrop));

        BigDecimal periodPhysicalCash = openPeriod.getPhysicalCashCollected() == null ? BigDecimal.ZERO : openPeriod.getPhysicalCashCollected();
        openPeriod.setPhysicalCashCollected(periodPhysicalCash.add(machineEntry.getPhysicalCash()));
        periodRepositoy.save(openPeriod);
        return machineEntryMapper.toResponse(machineEntry);
    }


    public List<MachineEntryResponse> getEntriesForPeriod(
            String periodID, String businessId,LocalDate startDate, LocalDate endDate
    ){
        List<MachineEntry> entries;
        if (startDate != null && endDate != null) {
            entries =  machineEntryRepository.findByBusinessIdAndOpenedAtBetween(
                    businessId,
                    startDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX)
            );
        } else {
            log.info("business id and period id: {} {}",businessId,periodID);
            entries =  machineEntryRepository.findByBusinessIdAndPeriodId(businessId, periodID);
        }
        return entries.stream().map(machineEntryMapper::toResponse).toList();

    }
}
