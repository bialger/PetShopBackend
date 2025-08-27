package org.bialger.pets.services.contracts;

import org.bialger.pets.entities.Color;
import org.bialger.pets.models.PetModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PetService {
    Optional<PetModel> getPetById(Long id);

    Page<PetModel> getAllPets(Pageable pageable);

    PetModel savePet(PetModel petModel);

    void deletePet(Long id);

    Page<PetModel> findPetsByColor(Color color, Pageable pageable);

    Page<PetModel> findPetsByBreed(String breed, Pageable pageable);

    Page<PetModel> findPetsByNameContaining(String name, Pageable pageable);

    Page<PetModel> findPetsByOwnerId(Long ownerId, Pageable pageable);

    void addFriendship(Long petId, Long friendId);

    void removeFriendship(Long petId, Long friendId);

    PetModel transferPetToNewOwner(Long petId, Long newOwnerId);

    PetModel assignOwnerToPet(Long petId, Long ownerId);
}
