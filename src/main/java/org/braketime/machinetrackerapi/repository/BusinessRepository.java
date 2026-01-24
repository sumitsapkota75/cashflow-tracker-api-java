package org.braketime.machinetrackerapi.repository;

import org.braketime.machinetrackerapi.domain.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BusinessRepository extends MongoRepository<Business, String> {
    boolean existsByNameAndActive(String name,boolean active );

    Optional<Business> findByIdAndActive(String id,boolean active);

    Page<Business> findAllByActive(boolean active, Pageable pageable);

}
