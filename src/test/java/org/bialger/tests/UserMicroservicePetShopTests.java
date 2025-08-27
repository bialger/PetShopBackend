package org.bialger.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bialger.gateway.MicroservicePetShopApp;
import org.bialger.gateway.controllers.UserController;
import org.bialger.gateway.controllers.dto.UserDto;
import org.bialger.gateway.entities.Role;
import org.bialger.gateway.infrastructure.mappers.UserModelToDtoMapper;
import org.bialger.gateway.models.UserModel;
import org.bialger.gateway.services.contracts.UserService;
import org.bialger.gateway.services.security.UserSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = MicroservicePetShopApp.class)
public class UserMicroservicePetShopTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserModelToDtoMapper userDtoMapper;

    @MockBean
    private UserSecurity userSecurity;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel testUserModel;

    @BeforeEach
    void setUp() {
        testUserModel = new UserModel(1L, "testuser", Role.ROLE_USER, 10L);

        UserDto testUserDto = new UserDto(1L, "testuser", Role.ROLE_USER, 10L);

        when(userDtoMapper.userModelToUserDto(any(UserModel.class))).thenReturn(testUserDto);
        when(userDtoMapper.userDtoToUserModel(any(UserDto.class))).thenReturn(testUserModel);

        when(userSecurity.isSelf(any(Authentication.class), anyLong())).thenReturn(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRegisterAdminSuccess() throws Exception {
        UserDto adminRegisterDto = new UserDto();
        adminRegisterDto.setUsername("newadmin");
        adminRegisterDto.setPassword("adminpass");
        adminRegisterDto.setRole(Role.ROLE_ADMIN);

        when(userService.existsByUsername("newadmin")).thenReturn(false);
        UserModel createdAdminModel = new UserModel(3L, "newadmin", Role.ROLE_ADMIN, null);
        UserDto createdAdminDto = new UserDto(3L, "newadmin", Role.ROLE_ADMIN, null);

        UserModel modelFromAdminDto = new UserModel();
        modelFromAdminDto.setUsername(adminRegisterDto.getUsername());
        modelFromAdminDto.setPassword(adminRegisterDto.getPassword());
        modelFromAdminDto.setRole(Role.ROLE_ADMIN);

        when(userDtoMapper.userDtoToUserModel(any(UserDto.class))).thenAnswer(invocation -> {
            UserDto dtoArg = invocation.getArgument(0);
            if ("newadmin".equals(dtoArg.getUsername())) {
                modelFromAdminDto.setRole(Role.ROLE_ADMIN);
                return modelFromAdminDto;
            }
            return testUserModel;
        });

        when(userService.createUser(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel modelArg = invocation.getArgument(0);
            if ("newadmin".equals(modelArg.getUsername()) && Role.ROLE_ADMIN.equals(modelArg.getRole())) {
                return createdAdminModel;
            }
            throw new IllegalArgumentException("Mock for createUser (admin) not matching expected input");
        });
        when(userDtoMapper.userModelToUserDto(createdAdminModel)).thenReturn(createdAdminDto);

        mockMvc.perform(post("/auth/registerAdmin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRegisterDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newadmin"))
                .andExpect(jsonPath("$.role").value(Role.ROLE_ADMIN.name()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetUserByIdSelf() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUserModel));
        when(userSecurity.isSelf(any(Authentication.class), eq(1L))).thenReturn(true);

        mockMvc.perform(get("/auth/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserByIdAdminAccess() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUserModel));

        mockMvc.perform(get("/auth/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(testUserModel)));

        mockMvc.perform(get("/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUserAdmin() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUserModel));
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/auth/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testChangeUserPasswordSelf() throws Exception {
        UserDto passwordDto = new UserDto();
        passwordDto.setPassword("newStrongPassword");
        passwordDto.setUsername("testuser");
        passwordDto.setRole(Role.ROLE_USER);

        when(userSecurity.isSelf(any(Authentication.class), eq(1L))).thenReturn(true);
        UserModel updatedPasswordModel = new UserModel(1L, "testuser", Role.ROLE_USER, 10L);
        when(userService.updateUserPassword(1L, "newStrongPassword")).thenReturn(updatedPasswordModel);

        mockMvc.perform(put("/auth/users/1/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminChangeUserRole() throws Exception {
        UserDto roleDto = new UserDto();
        roleDto.setRole(Role.ROLE_ADMIN);
        UserModel changedRoleModel = new UserModel(1L, "testuser", Role.ROLE_ADMIN, 10L);
        UserDto changedRoleResponseDto = new UserDto(1L, "testuser", Role.ROLE_ADMIN, 10L);

        when(userService.changeUserRole(1L, Role.ROLE_ADMIN)).thenReturn(changedRoleModel);
        when(userDtoMapper.userModelToUserDto(changedRoleModel)).thenReturn(changedRoleResponseDto);

        mockMvc.perform(put("/auth/users/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value(Role.ROLE_ADMIN.name()));
    }
}
