package org.braketime.machinetrackerapi.mapper;

import org.braketime.machinetrackerapi.Dtos.MachineEntryRequest;
import org.braketime.machinetrackerapi.Dtos.MachineEntryResponse;
import org.braketime.machinetrackerapi.domain.MachineEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MachineEntryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "businessId", ignore = true)
    @Mapping(target = "periodId", ignore = true)
    @Mapping(target = "openedByUserId", ignore = true)
    @Mapping(target = "openedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "difference", ignore = true)
    @Mapping(target = "hasPreviousEntry", ignore = true)
    MachineEntry toEntity(MachineEntryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "businessId", ignore = true)
    @Mapping(target = "periodId", ignore = true)
    @Mapping(target = "openedByUserId", ignore = true)
    @Mapping(target = "openedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "difference", ignore = true)

    void updateEntity(
            MachineEntryRequest request,
            @MappingTarget MachineEntry entity
    );
    @Mapping(source = "id", target = "entryId")
    MachineEntryResponse toResponse(MachineEntry entity);
}
