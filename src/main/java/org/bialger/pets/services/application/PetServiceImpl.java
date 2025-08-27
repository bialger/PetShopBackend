package org.bialger.pets.services.application;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bialger.pets.entities.Color;
import org.bialger.pets.entities.Pet;
import org.bialger.pets.infrastructure.mappers.PetEntityToModelMapper;
import org.bialger.pets.models.PetModel;
import org.bialger.pets.repositories.PetRepository;
import org.bialger.pets.services.contracts.PetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetEntityToModelMapper petMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<PetModel> getPetById(Long id) {
        return petRepository.findById(id)
                .map(petMapper::petEntityToPetModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetModel> getAllPets(Pageable pageable) {
        return petRepository.findAll(pageable)
                .map(petMapper::petEntityToPetModel);
    }

    @Override
    public PetModel savePet(PetModel petModel) {
        Pet petEntity = petMapper.petModelToPetEntity(petModel);
        if (petModel.getFriendIds() != null && !petModel.getFriendIds().isEmpty()) {
            Set<Pet> friends = petModel.getFriendIds().stream()
                    .map(friendId -> petRepository.findById(friendId)
                            .orElseThrow(() -> new EntityNotFoundException("Friend pet with id " + friendId + " not found")))
                    .collect(Collectors.toSet());
            petEntity.setFriends(friends);
        }
        Pet savedPet = petRepository.save(petEntity);
        return petMapper.petEntityToPetModel(savedPet);
    }

    @Override
    public void deletePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new EntityNotFoundException("Pet with id " + id + " not found, cannot delete.");
        }
        petRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetModel> findPetsByColor(Color color, Pageable pageable) {
        return petRepository.findByColor(color, pageable)
                .map(petMapper::petEntityToPetModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetModel> findPetsByBreed(String breed, Pageable pageable) {
        return petRepository.findByBreed(breed, pageable)
                .map(petMapper::petEntityToPetModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetModel> findPetsByNameContaining(String name, Pageable pageable) {
        return petRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(petMapper::petEntityToPetModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetModel> findPetsByOwnerId(Long ownerId, Pageable pageable) {
        return petRepository.findByOwnerId(ownerId, pageable)
                .map(petMapper::petEntityToPetModel);
    }

    @Override
    public void addFriendship(Long petId, Long friendId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet with id " + petId + " not found"));
        Pet friend = petRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend pet with id " + friendId + " not found"));

        pet.addFriend(friend);
        petRepository.save(pet);
    }

    @Override
    public void removeFriendship(Long petId, Long friendId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet with id " + petId + " not found"));
        Pet friend = petRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend pet with id " + friendId + " not found"));

        pet.removeFriend(friend);
        petRepository.save(pet);
    }

    @Override
    public PetModel transferPetToNewOwner(Long petId, Long newOwnerId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet with id " + petId + " not found"));
        pet.setOwnerId(newOwnerId);
        Pet savedPet = petRepository.save(pet);
        return petMapper.petEntityToPetModel(savedPet);
    }

    @Override
    public PetModel assignOwnerToPet(Long petId, Long ownerId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet with id " + petId + " not found"));
        pet.setOwnerId(ownerId);
        Pet savedPet = petRepository.save(pet);
        return petMapper.petEntityToPetModel(savedPet);
    }
}
