package org.braketime.machinetrackerapi.repository;

import org.braketime.machinetrackerapi.domain.Machine;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MachineRepository extends MongoRepository<Machine, String> {
    List<Machine> findAllByBusinessId(String businessId);
}
