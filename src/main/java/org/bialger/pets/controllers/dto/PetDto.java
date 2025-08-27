package org.bialger.pets.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class PetDto {

    private Long id;

    @NotBlank(message = "Pet name cannot be empty")
    private String name;

    @NotNull(message = "Birth date is required")
    @PastOrPresent(message = "Birth date must be in the past or present")
    private LocalDate birthDate;

    @NotBlank(message = "Breed cannot be empty")
    private String breed;

    @NotBlank(message = "Color cannot be empty")
    private String color;

    private Long ownerId;

    @NotNull(message = "Tail length is required")
    @Positive(message = "Tail length must be positive")
    private Double tailLength;

    private Set<Long> friendIds = new HashSet<>();

    public PetDto() {
    }

    public PetDto(Long id, String name, LocalDate birthDate, String breed, String color, Long ownerId, Double tailLength) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.breed = breed;
        this.color = color;
        this.ownerId = ownerId;
        this.tailLength = tailLength;
    }
}
