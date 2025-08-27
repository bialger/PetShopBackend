package org.bialger.gateway.models;

import lombok.Getter;
import lombok.Setter;
import org.bialger.gateway.entities.Role;

@Setter
@Getter
public class UserModel {
    private Long id;

    private String username;

    private String password;

    private Role role;

    private Long ownerId;

    public UserModel() {
    }

    public UserModel(Long id, String username, Role role, Long ownerId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.ownerId = ownerId;
    }
}
