package org.braketime.machinetrackerapi.mapper;

import org.braketime.machinetrackerapi.Dtos.UserCreateResponse;
import org.braketime.machinetrackerapi.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {

    User toEntity(User user);

}
