package org.bialger.owners.infrastructure.mappers;

import org.bialger.owners.controllers.dto.OwnerDto;
import org.bialger.owners.models.OwnerModel;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface OwnerModelToDtoMapper {

    OwnerModel ownerDtoToOwnerModel(OwnerDto ownerDto);

    OwnerDto ownerModelToOwnerDto(OwnerModel ownerModel);
}
