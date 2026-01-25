package org.braketime.machinetrackerapi.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.domain.Business;
import org.braketime.machinetrackerapi.domain.Machine;
import org.braketime.machinetrackerapi.repository.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class MachineService {
    private final MachineRepository machineRepository;

    public List<String> createMachineForBusiness(Business business){
        int num = business.getNumberOfMachines();
        List<Machine> machines = new ArrayList<>();
        log.info("business id:{}",business.getId());
        for (int i=1; i<= num; i++){
            machines.add(
                    Machine.builder()
                            .businessId(business.getId())
                            .name("Machine " + i)
                            .active(true)
                            .build()
            );
        }
       machines = machineRepository.saveAll(machines);
        log.info("Created {} machines for business {}", num, business.getName());
        return machines.stream().map(Machine::getId).collect(Collectors.toList());
    }
}
