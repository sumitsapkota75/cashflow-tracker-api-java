package org.braketime.machinetrackerapi.repository;

import java.util.List;
import java.util.Optional;

import org.braketime.machinetrackerapi.domain.Winner;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WinnerRepository extends MongoRepository<Winner,String> {
    Optional<Winner> findByIdAndBusinessId(String id, String businessId);
    Optional<List<Winner>> findAllByBusinessIdOrderByCreatedAtDesc(String businessId);
    
}
