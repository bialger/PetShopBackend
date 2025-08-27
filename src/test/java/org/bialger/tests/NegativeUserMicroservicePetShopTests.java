package org.bialger.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bialger.gateway.MicroservicePetShopApp;
import org.bialger.gateway.controllers.UserController;
import org.bialger.gateway.controllers.dto.UserDto;
import org.bialger.gateway.entities.Role;
import org.bialger.gateway.infrastructure.mappers.UserModelToDtoMapper;
import org.bialger.gateway.services.contracts.UserService;
import org.bialger.gateway.services.security.UserSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = MicroservicePetShopApp.class)
public class NegativeUserMicroservicePetShopTests {

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

    @BeforeEach
    void setUp() {
        when(userSecurity.isSelf(any(Authentication.class), anyLong())).thenReturn(false);
    }

    @Test
    void testRegisterAdminUnauthenticated() throws Exception {
        UserDto adminRegisterDto = new UserDto();
        adminRegisterDto.setUsername("newadmin");
        adminRegisterDto.setPassword("adminpass");
        adminRegisterDto.setRole(Role.ROLE_ADMIN);

        mockMvc.perform(post("/auth/registerAdmin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRegisterDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserByIdUnauthenticated() throws Exception {
        mockMvc.perform(get("/auth/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllUsersUnauthenticated() throws Exception {
        mockMvc.perform(get("/auth/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUserUnauthenticated() throws Exception {
        UserDto updateDto = new UserDto(1L, "updateduser", Role.ROLE_USER, 10L);
        mockMvc.perform(put("/auth/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testChangeUserPasswordUnauthenticated() throws Exception {
        UserDto passwordDto = new UserDto();
        passwordDto.setPassword("newStrongPassword");
        mockMvc.perform(put("/auth/users/1/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testChangeUserPasswordMissingPassword() throws Exception {
        UserDto passwordDto = new UserDto();
        when(userSecurity.isSelf(any(Authentication.class), eq(1L))).thenReturn(true);

        mockMvc.perform(put("/auth/users/1/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("New password cannot be empty."));
    }

    @Test
    void testAdminChangeUserRoleUnauthenticated() throws Exception {
        UserDto roleDto = new UserDto();
        roleDto.setRole(Role.ROLE_ADMIN);
        mockMvc.perform(put("/auth/users/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminChangeUserRoleMissingRole() throws Exception {
        UserDto roleDto = new UserDto();
        roleDto.setUsername("dummyUser");
        roleDto.setPassword("dummyPass");


        mockMvc.perform(put("/auth/users/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("New role cannot be null."));
    }

    @Test
    void testDeleteUserUnauthenticated() throws Exception {
        mockMvc.perform(delete("/auth/users/1").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUserNotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());
        mockMvc.perform(delete("/auth/users/999").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with id: 999"));
    }
}
