package com.example.unicode.service.impl;

import com.example.unicode.dto.request.UserCreateRequest;
import com.example.unicode.dto.request.UserUpdateRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.UserResponse;
import com.example.unicode.entity.Certificate;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Role;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.UserMapper;
import com.example.unicode.repository.CertificateRepository;
import com.example.unicode.repository.RoleRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.ultils.ExportCertificateUltils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private ExportCertificateUltils exportCertificateUltils;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createShouldThrowWhenEmailAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest("u@test.com", "secret", "User", null, null);
        when(usersRepository.existsByEmailAndDeletedFalse("u@test.com")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> userService.create(request));

        assertEquals(ErrorCode.USER_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void createShouldAssignDefaultLearnerRole() {
        UserCreateRequest request = new UserCreateRequest("u@test.com", "secret", "User", null, null);
        Users entity = new Users();
        Users saved = new Users();
        UserResponse response = UserResponse.builder().email("u@test.com").build();
        Role learner = new Role();
        learner.setRoleCode("LEARNER");

        when(usersRepository.existsByEmailAndDeletedFalse("u@test.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(roleRepository.findByRoleCodeAndDeletedFalse("LEARNER")).thenReturn(Optional.of(learner));
        when(usersRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertEquals("u@test.com", result.getEmail());
        assertEquals("encoded", entity.getPassword());
        assertEquals(1, entity.getRolesList().size());
    }

    @Test
    void createShouldUseProvidedRoleCodes() {
        UserCreateRequest request = new UserCreateRequest("u@test.com", "secret", "User", null, Set.of("ADMIN"));
        Users entity = new Users();
        Users saved = new Users();
        Role admin = new Role();
        admin.setRoleCode("ADMIN");

        when(usersRepository.existsByEmailAndDeletedFalse("u@test.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(roleRepository.findByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(Optional.of(admin));
        when(usersRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(UserResponse.builder().email("u@test.com").build());

        userService.create(request);

        assertEquals("encoded", entity.getPassword());
        assertEquals(1, entity.getRolesList().size());
    }

    @Test
    void createShouldThrowWhenProvidedRoleDoesNotExist() {
        UserCreateRequest request = new UserCreateRequest("u@test.com", "secret", "User", null, Set.of("ADMIN"));

        when(usersRepository.existsByEmailAndDeletedFalse("u@test.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(new Users());
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(roleRepository.findByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.create(request));

        assertEquals(ErrorCode.ROLE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getByIdAndGetByEmailShouldMapFoundUser() {
        UUID userId = UUID.randomUUID();
        Users user = new Users();
        UserResponse mapped = UserResponse.builder().email("u@test.com").build();

        when(usersRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(usersRepository.findByEmailAndDeletedFalse("u@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(mapped);

        assertEquals("u@test.com", userService.getById(userId).getEmail());
        assertEquals("u@test.com", userService.getByEmail("u@test.com").getEmail());
    }

    @Test
    void getByIdShouldThrowWhenNotFound() {
        UUID userId = UUID.randomUUID();
        when(usersRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.getById(userId));

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getByEmailShouldThrowWhenNotFound() {
        when(usersRepository.findByEmailAndDeletedFalse("missing@test.com")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.getByEmail("missing@test.com"));

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAllShouldReturnPagedResponse() {
        UUID currentId = UUID.randomUUID();
        Users current = new Users();
        current.setUserId(currentId);
        Users listed = new Users();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@test.com", "pwd", AuthorityUtils.NO_AUTHORITIES)
        );

        when(usersRepository.findByEmail("admin@test.com")).thenReturn(current);
        when(usersRepository.findAllByDeletedAndUserIdNot(eq(false), eq(currentId), eq(PageRequest.of(0, 10))))
                .thenReturn(new PageImpl<>(List.of(listed), PageRequest.of(0, 10), 1));
        when(userMapper.toResponseList(List.of(listed)))
                .thenReturn(List.of(UserResponse.builder().email("a@test.com").build()));

        PageResponse<UserResponse> response = userService.getAll(0, 10, false);

        assertEquals(1, response.getContent().size());
        assertEquals("a@test.com", response.getContent().get(0).getEmail());
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void updateShouldThrowWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.update(userId, new UserUpdateRequest()));

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateShouldThrowWhenRoleNotFound() {
        UUID userId = UUID.randomUUID();
        String sameName = "Name";
        UserUpdateRequest request = new UserUpdateRequest(sameName, null, true, Set.of("ADMIN"));
        Users user = new Users();
        user.setName(sameName);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.update(userId, request));

        assertEquals(ErrorCode.ROLE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateShouldRefreshCertificatesWhenNameChanges() {
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("New Name", null, true, Set.of("ADMIN"));
        Users user = new Users();
        user.setName("Old Name");
        Course course = new Course();
        Certificate certificate = new Certificate();
        certificate.setCourse(course);
        Role admin = new Role();
        admin.setRoleCode("ADMIN");

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(certificateRepository.getByLearner(user)).thenReturn(List.of(certificate));
        when(exportCertificateUltils.generateCertificate("New Name", course)).thenReturn("new-key");
        when(roleRepository.findByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(Optional.of(admin));
        when(usersRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder().name("New Name").build());

        UserResponse response = userService.update(userId, request);

        assertEquals("New Name", response.getName());
        assertEquals("new-key", certificate.getKeyUrl());
        assertEquals(1, user.getRolesList().size());
        verify(certificateRepository).saveAll(List.of(certificate));
        verify(userMapper).updateEntity(request, user);
    }

    @Test
    void updateShouldSkipRoleUpdateWhenRoleCodesNull() {
        UUID userId = UUID.randomUUID();
        Users user = new Users();
        user.setName("same");
        user.setRolesList(Set.of(new Role()));
        UserUpdateRequest request = new UserUpdateRequest("same", "avatar", true, null);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usersRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder().name("same").build());

        userService.update(userId, request);

        verify(roleRepository, never()).findByRoleCodeAndDeletedFalse(any());
    }

    @Test
    void modifiUserShouldSoftDeleteUserWhenDeleteTrue() {
        UUID userId = UUID.randomUUID();
        Users user = new Users();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@test.com", "pwd", AuthorityUtils.createAuthorityList("ROLE_ADMIN"))
        );

        userService.modifiUser(userId, true);

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(captor.capture());
        assertTrue(captor.getValue().getDeleted());
        assertEquals("admin@test.com", captor.getValue().getDeletedBy());
        assertNotNull(captor.getValue().getDeletedAt());
    }

    @Test
    void modifiUserShouldUseSystemWhenNoAuthentication() {
        UUID userId = UUID.randomUUID();
        Users user = new Users();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.modifiUser(userId, false);

        verify(usersRepository).save(user);
        assertFalse(user.getDeleted());
        assertEquals("SYSTEM", user.getDeletedBy());
        assertNotNull(user.getDeletedAt());
    }

    @Test
    void modifiUserShouldThrowWhenUserMissing() {
        UUID userId = UUID.randomUUID();
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.modifiUser(userId, true));

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getMyInfoShouldReturnMappedUser() {
        Users user = new Users();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user@test.com", "pwd", AuthorityUtils.NO_AUTHORITIES)
        );
        when(usersRepository.findByEmailAndDeletedFalse("user@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder().email("user@test.com").build());

        UserResponse response = userService.getMyInfo();

        assertEquals("user@test.com", response.getEmail());
    }

    @Test
    void getMyInfoShouldThrowWhenNotFound() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("missing@test.com", "pwd", AuthorityUtils.NO_AUTHORITIES)
        );
        when(usersRepository.findByEmailAndDeletedFalse("missing@test.com")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, userService::getMyInfo);

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getUsersShouldThrowWhenNoUserInRepository() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("missing@test.com", "pwd", AuthorityUtils.NO_AUTHORITIES)
        );
        when(usersRepository.findByEmail("missing@test.com")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, userService::getUsers);

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }
}
