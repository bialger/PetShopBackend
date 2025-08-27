package org.bialger.pets.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private Date birthDate;

    @NotNull
    @Column(name = "breed", nullable = false)
    private String breed;

    @NotNull
    @Column(name = "color", nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color;

    @NotNull
    @Column(name = "tail_length", nullable = false)
    private Double tailLength;

    @Column(name = "owner_id", nullable = true)
    private Long ownerId;

    @ManyToMany
    @JoinTable(
            name = "pet_friends",
            joinColumns = @JoinColumn(name = "pet_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Pet> friends = new HashSet<>();

    public Pet() {
    }

    public Pet(String name, String breed, Date birthDate, Color color, double tailLength, Long ownerId) {
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.color = color;
        this.tailLength = tailLength;
        this.ownerId = ownerId;
    }

    public void addFriend(Pet friend) {
        if (friend != null && !this.equals(friend)) {
            this.friends.add(friend);

            if (!friend.getFriends().contains(this)) {
                friend.addFriend(this);
            }
        }
    }

    public void removeFriend(Pet friend) {
        if (friend != null) {
            this.friends.remove(friend);
            friend.getFriends().remove(this);
        }
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", breed='" + breed + '\'' +
                ", birthDate=" + birthDate +
                ", color=" + color +
                ", tailLength=" + tailLength +
                ", ownerId=" + ownerId +
                ", friendsCount=" + (friends != null ? friends.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return id != null && id.equals(pet.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
