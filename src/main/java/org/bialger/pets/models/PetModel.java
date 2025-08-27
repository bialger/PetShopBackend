package org.bialger.pets.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class PetModel {

    private Long id;

    private String name;

    private LocalDate birthDate;

    private String breed;

    private String color;

    private Long ownerId;

    private Double tailLength;

    private Set<Long> friendIds = new HashSet<>();

    public PetModel() {
    }

    public PetModel(Long id, String name, LocalDate birthDate, String breed, String color, Long ownerId, Double tailLength) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.breed = breed;
        this.color = color;
        this.ownerId = ownerId;
        this.tailLength = tailLength;
    }
}
