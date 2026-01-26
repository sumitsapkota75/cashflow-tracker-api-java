package org.braketime.machinetrackerapi.mapper;


import org.braketime.machinetrackerapi.Dtos.ClosePeriodRequest;
import org.braketime.machinetrackerapi.Dtos.OpenPeriodRequest;
import org.braketime.machinetrackerapi.Dtos.PeriodResponse;
import org.braketime.machinetrackerapi.domain.Period;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PeriodMapper {

    // CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "openedByUserId", ignore = true)
    @Mapping(target = "closedByUserId", ignore = true)
    @Mapping(target = "openedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "netOpen", ignore = true)
    @Mapping(target = "netClose", ignore = true)
    Period toEntity(OpenPeriodRequest request);


    PeriodResponse toResponse(Period response);

    // UPDATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "businessId", ignore = true)
    @Mapping(target = "businessDate", ignore = true)
    @Mapping(target = "openedByUserId", ignore = true)
    @Mapping(target = "closedByUserId", ignore = true)
    @Mapping(target = "openedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void update(ClosePeriodRequest request, @MappingTarget Period period);
}
