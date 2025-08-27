package org.bialger.gateway.infrastructure.mappers;

import org.bialger.gateway.controllers.dto.UserDto;
import org.bialger.gateway.models.UserModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserModelToDtoMapper {

    UserModel userDtoToUserModel(UserDto userDto);

    UserDto userModelToUserDto(UserModel userModel);

    @AfterMapping
    default void nullifyPasswordInDto(@MappingTarget UserDto userDto) {
        userDto.setPassword(null);
    }
}
