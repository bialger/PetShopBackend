package org.bialger.gateway.services.application;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bialger.gateway.entities.Role;
import org.bialger.gateway.entities.User;
import org.bialger.gateway.infrastructure.mappers.UserEntityToModelMapper;
import org.bialger.gateway.models.UserModel;
import org.bialger.gateway.repositories.UserRepository;
import org.bialger.gateway.services.contracts.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEntityToModelMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserModel createUser(UserModel userModel) {
        User user = userMapper.userModelToUserEntity(userModel);
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.userEntityToUserModel(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserModel> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::userEntityToUserModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserModel> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::userEntityToUserModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserModel> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::userEntityToUserModel);
    }

    @Override
    public UserModel saveUser(UserModel userModel) {
        User userToSave;

        if (userModel.getId() != null) {
            User existingUser = userRepository.findById(userModel.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + userModel.getId() + " not found"));

            existingUser.setUsername(userModel.getUsername());
            if (userModel.getPassword() != null && !userModel.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userModel.getPassword()));
            }

            if (userModel.getRole() != null) {
                existingUser.setRole(userModel.getRole());
            }

            existingUser.setOwnerId(userModel.getOwnerId());
            userToSave = existingUser;
        } else {
            userToSave = userMapper.userModelToUserEntity(userModel);
            userToSave.setPassword(passwordEncoder.encode(userModel.getPassword()));
        }

        User savedUser = userRepository.save(userToSave);

        return userMapper.userEntityToUserModel(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserModel changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        user.setRole(newRole);
        User savedUser = userRepository.save(user);

        return userMapper.userEntityToUserModel(savedUser);
    }

    @Override
    public UserModel assignOwnerToUser(Long userId, Long ownerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        user.setOwnerId(ownerId);
        User savedUser = userRepository.save(user);

        return userMapper.userEntityToUserModel(savedUser);
    }

    @Override
    public UserModel removeOwnerFromUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        user.setOwnerId(null);
        User savedUser = userRepository.save(user);

        return userMapper.userEntityToUserModel(savedUser);
    }

    @Override
    public UserModel updateUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);

        return userMapper.userEntityToUserModel(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}

