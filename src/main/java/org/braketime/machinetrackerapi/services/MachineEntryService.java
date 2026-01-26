package org.braketime.machinetrackerapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class MachineEntryService {

    private final PeriodRepositoy periodRepositoy;
    private final MachineEntryMapper machineEntryMapper;
    private final MachineEntryRepository machineEntryRepository;

    public MachineEntryResponse createEntry(MachineEntryRequest request){

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

        machineEntryRepository.save(machineEntry);

        log.info(
                "MachineEntry created. entryId={}, periodId={}, machineId={}",
                machineEntry.getId(),
                machineEntry.getPeriodId(),
                machineEntry.getMachineId()
        );
        // Aggregate in Period data
        openPeriod.setSafeDrop(openPeriod.getSafeDrop().add(machineEntry.getSafeDroppedAmount()));
        openPeriod.setPhysicalCashCollected(openPeriod.getPhysicalCashCollected().add(machineEntry.getPhysicalCash()));
        periodRepositoy.save(openPeriod);
        return machineEntryMapper.toResponse(machineEntry);
    }
}
