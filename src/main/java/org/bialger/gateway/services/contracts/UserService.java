package org.bialger.gateway.services.contracts;

import org.bialger.gateway.entities.Role; // Используем Role из gateway
import org.bialger.gateway.models.UserModel; // Используем UserModel из gateway
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    UserModel createUser(UserModel userModel);

    Optional<UserModel> getUserById(Long id);

    Optional<UserModel> getUserByUsername(String username);

    Page<UserModel> getAllUsers(Pageable pageable);

    UserModel saveUser(UserModel userModel); // Может использоваться для обновления

    void deleteUser(Long id);

    UserModel changeUserRole(Long userId, Role newRole);

    UserModel assignOwnerToUser(Long userId, Long ownerId);

    UserModel removeOwnerFromUser(Long userId);

    UserModel updateUserPassword(Long userId, String newPassword);

    boolean existsByUsername(String username);
}
