package org.bialger.gateway.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bialger.gateway.controllers.dto.UserDto;
import org.bialger.gateway.entities.Role;
import org.bialger.gateway.infrastructure.mappers.UserModelToDtoMapper;
import org.bialger.gateway.models.UserModel;
import org.bialger.gateway.services.contracts.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserModelToDtoMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
        if (userService.existsByUsername(userDto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        UserModel userModel = userMapper.userDtoToUserModel(userDto);
        userModel.setRole(Role.ROLE_USER);
        UserModel createdUser = userService.createUser(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.userModelToUserDto(createdUser));
    }

    @PostMapping("/registerAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> registerAdmin(@Valid @RequestBody UserDto userDto) {
        if (userService.existsByUsername(userDto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        UserModel userModel = userMapper.userDtoToUserModel(userDto);
        userModel.setRole(Role.ROLE_ADMIN);
        UserModel createdUser = userService.createUser(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.userModelToUserDto(createdUser));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSelf(authentication, #id)")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(userMapper::userModelToUserDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        Page<UserModel> userModels = userService.getAllUsers(pageable);
        return ResponseEntity.ok(userModels.map(userMapper::userModelToUserDto));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSelf(authentication, #id)")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        UserModel userModel = userMapper.userDtoToUserModel(userDto);
        userModel.setId(id);
        UserModel updatedUser = userService.saveUser(userModel);
        return ResponseEntity.ok(userMapper.userModelToUserDto(updatedUser));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSelf(authentication, #id)")
    public ResponseEntity<Void> changeUserPassword(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password cannot be empty.");
        }
        userService.updateUserPassword(id, userDto.getPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> adminChangeUserRole(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (userDto.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New role cannot be null.");
        }
        Role newRole = Role.valueOf(userDto.getRole().name());
        UserModel updatedUser = userService.changeUserRole(id, newRole);
        return ResponseEntity.ok(userMapper.userModelToUserDto(updatedUser));
    }

    @PutMapping("/users/{userId}/assignOwner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> assignOwnerToUser(@PathVariable Long userId, @PathVariable Long ownerId) {
        UserModel updatedUser = userService.assignOwnerToUser(userId, ownerId);
        return ResponseEntity.ok(userMapper.userModelToUserDto(updatedUser));
    }

    @PutMapping("/users/{userId}/removeOwner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> removeOwnerFromUser(@PathVariable Long userId) {
        UserModel updatedUser = userService.removeOwnerFromUser(userId);
        return ResponseEntity.ok(userMapper.userModelToUserDto(updatedUser));
    }
}
