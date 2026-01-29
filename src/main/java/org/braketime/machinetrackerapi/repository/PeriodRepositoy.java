package org.braketime.machinetrackerapi.repository;

import org.braketime.machinetrackerapi.domain.Period;
import org.braketime.machinetrackerapi.enums.PeriodStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PeriodRepositoy extends MongoRepository<Period, String> {

    Optional<Period> findByBusinessIdAndBusinessDate(
            String businessId,
            LocalDate businessDate
    );
    Optional<Period> findByBusinessIdAndStatus(
            String businessId,
            PeriodStatus status
    );

    Page<Period> findAllByBusinessId(String businessId, Pageable pageable);

    Optional<Period> findFirstByBusinessIdAndStatusOrderByOpenedAtDesc(
            String businessId,
            PeriodStatus status
    );

    boolean existsByBusinessIdAndBusinessDateAndStatus(
            String businessId,
            LocalDate businessDate,
            PeriodStatus status
    );

}
