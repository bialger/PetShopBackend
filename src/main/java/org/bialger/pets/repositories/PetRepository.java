package org.bialger.pets.repositories;

import org.bialger.pets.entities.Pet;
import org.bialger.pets.entities.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {
    Page<Pet> findByColor(Color color, Pageable pageable);

    Page<Pet> findByBreed(String breed, Pageable pageable);

    Page<Pet> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Pet> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
