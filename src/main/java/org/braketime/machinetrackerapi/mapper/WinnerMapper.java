package org.braketime.machinetrackerapi.mapper;

import org.braketime.machinetrackerapi.Dtos.WinnerCreateRequest;
import org.braketime.machinetrackerapi.domain.Winner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WinnerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "businessId", ignore = true)
    @Mapping(target = "createdByUsername", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Winner toEntity(WinnerCreateRequest request);
}
