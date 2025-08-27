package org.bialger.owners.infrastructure.mappers;

import org.bialger.owners.entities.Owner;
import org.bialger.owners.models.OwnerModel;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface OwnerEntityToModelMapper {

    OwnerModel ownerEntityToOwnerModel(Owner ownerEntity);

    Owner ownerModelToOwnerEntity(OwnerModel ownerModel);
}

