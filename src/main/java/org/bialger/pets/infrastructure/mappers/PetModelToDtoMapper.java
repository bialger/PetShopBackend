package org.bialger.pets.infrastructure.mappers;

import org.bialger.pets.controllers.dto.PetDto;
import org.bialger.pets.models.PetModel;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface PetModelToDtoMapper {

    PetModel petDtoToPetModel(PetDto petDto);

    PetDto petModelToPetDto(PetModel petModel);
}
