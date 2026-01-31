package org.braketime.machinetrackerapi.repository;

import org.braketime.machinetrackerapi.domain.WinnerPayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WinnerPayoutRepository extends MongoRepository<WinnerPayout,String> {
    List<WinnerPayout> findAllByWinnerIdOrderByPayoutDateDesc(String id);
    Page<WinnerPayout> findAllByBusinessIdOrderByPayoutDateDesc(String id, Pageable pageable);
    List<WinnerPayout> findAllByBusinessIdAndPeriodIdOrderByPayoutDateDesc(String businessId, String periodId);
}
