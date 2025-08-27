package org.bialger.pets.infrastructure.mappers;

import org.bialger.pets.entities.Pet;
import org.bialger.pets.models.PetModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring")
public interface PetEntityToModelMapper {

    @Mapping(source = "friends", target = "friendIds", qualifiedByName = "friendEntitiesToFriendIds")
    PetModel petEntityToPetModel(Pet petEntity);

    @Mapping(target = "friends", ignore = true)
    Pet petModelToPetEntity(PetModel petModel);

    @Named("friendEntitiesToFriendIds")
    default Set<Long> friendEntitiesToFriendIds(Set<Pet> friends) {
        if (friends == null) {
            return null;
        }
        return friends.stream()
                .map(Pet::getId)
                .collect(Collectors.toSet());
    }
}
