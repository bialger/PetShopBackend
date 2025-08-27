package org.bialger.owners.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class OwnerModel {
    private Long id;

    private String name;

    private LocalDate birthDate;

    public OwnerModel() {
    }

    public OwnerModel(Long id, String name, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
    }
}
