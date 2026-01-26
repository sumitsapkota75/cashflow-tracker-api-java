package org.braketime.machinetrackerapi.repository;

import org.braketime.machinetrackerapi.domain.MachineEntry;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MachineEntryRepository extends MongoRepository<MachineEntry,String> {
    List<MachineEntry> findByPeriodIdAndMachineId(
            String periodId,
            String machineId
    );

    List<MachineEntry> findAllByBusinessIdAndPeriodId(String businessId, String periodId, Limit limit);
    List<MachineEntry> findByOpenedByUserId(String userId);
    List<MachineEntry> findByBusinessIdAndOpenedAtBetween(
            String businessId,
            LocalDateTime start,
            LocalDateTime end
    );
}
