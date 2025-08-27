package org.bialger.gateway.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.bialger.gateway.entities.Role;

@Setter
@Getter
public class UserDto {
    private Long id;

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;
    
    private Long ownerId;

    public UserDto() {
    }

    public UserDto(Long id, String username, Role role, Long ownerId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.ownerId = ownerId;
    }
}
