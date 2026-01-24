package org.braketime.machinetrackerapi.mapper;

import org.braketime.machinetrackerapi.Dtos.BusinessCreateUpdateRequest;
import org.braketime.machinetrackerapi.Dtos.BusinessResponse;
import org.braketime.machinetrackerapi.domain.Business;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BusinessMapper {
    BusinessResponse toDto(Business business);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Business toEntity(BusinessCreateUpdateRequest request);
    void update(BusinessCreateUpdateRequest request, @MappingTarget Business business);
}
