package org.bialger.gateway.infrastructure.mappers;

import org.bialger.gateway.entities.User;
import org.bialger.gateway.models.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserEntityToModelMapper {

    UserModel userEntityToUserModel(User userEntity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "ownerId", source = "ownerId")
    User userModelToUserEntity(UserModel userModel);
}
