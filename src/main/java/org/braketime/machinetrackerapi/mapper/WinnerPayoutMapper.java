package org.braketime.machinetrackerapi.mapper;


import org.braketime.machinetrackerapi.Dtos.MachineEntryRequest;
import org.braketime.machinetrackerapi.Dtos.WinnerPayoutCreateRequest;
import org.braketime.machinetrackerapi.domain.MachineEntry;
import org.braketime.machinetrackerapi.domain.WinnerPayout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WinnerPayoutMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "payoutDate", ignore = true)
    WinnerPayout toEntity(WinnerPayoutCreateRequest request);
}
