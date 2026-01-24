# QUY CHUẨN DỰ ÁN UNICODE

> Tài liệu mô tả chi tiết kiến trúc, quy chuẩn code, naming convention và mẫu code chuẩn cho dự án UniCode.
> Bao gồm cả Backend (Spring Boot) và Frontend (ReactJS).

---

## 📁 Mục lục

1. [Tổng quan dự án](#1-tổng-quan-dự-án)
2. [Kiến trúc hệ thống](#2-kiến-trúc-hệ-thống)
3. [Cấu trúc thư mục](#3-cấu-trúc-thư-mục)
4. [Quy chuẩn API (RESTful)](#4-quy-chuẩn-api-restful)
5. [Quy chuẩn Git Flow](#5-quy-chuẩn-git-flow)
6. [Quy chuẩn Naming Convention](#6-quy-chuẩn-naming-convention)
7. [Mẫu code Backend (Spring Boot)](#7-mẫu-code-backend-spring-boot)
8. [Mẫu code Frontend (ReactJS)](#8-mẫu-code-frontend-reactjs)
9. [Xử lý lỗi & Error Codes](#9-xử-lý-lỗi--error-codes)
10. [Best Practices](#10-best-practices)

---

## 1. Tổng quan dự án

### 1.1. Công nghệ sử dụng

| Layer      | Technology                       |
| :--------- | :------------------------------- |
| Backend    | Spring Boot 3.5.6, Java 21       |
| Database   | PostgreSQL                       |
| Security   | Spring Security + JWT (OAuth2)   |
| API Docs   | SpringDoc OpenAPI (Swagger)      |
| ORM        | Spring Data JPA                  |
| Mapper     | MapStruct 1.5.5                  |
| Storage    | AWS S3                           |
| Frontend   | ReactJS + Vite (khuyến nghị)     |

### 1.2. Tên package gốc

```
com.example.unicode
```

---

## 2. Kiến trúc hệ thống

### 2.1. Layered Architecture (Backend)

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT                               │
└─────────────────────────┬───────────────────────────────────┘
                          │ HTTP Request
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                         │
│     (Nhận request, validate, gọi service, trả response)     │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                     SERVICE LAYER                           │
│           (Business logic, transaction handling)            │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   REPOSITORY LAYER                          │
│              (Data access, database queries)                │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE                               │
│                    (PostgreSQL)                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2. Luồng dữ liệu (Data Flow)

```
Request ──► Controller ──► Service ──► Repository ──► Database
   │             │             │             │
   │             │             │             └── Entity
   │             │             └── Entity/DTO mapping via Mapper
   │             └── DTO (Request/Response)
   └── JSON body
```

---

## 3. Cấu trúc thư mục

### 3.1. Backend (Spring Boot)

```
src/main/java/com/example/unicode/
├── UniCodeApplication.java          # Main application entry point
├── base/                             # Base classes
│   ├── ApiResponse.java              # Unified response wrapper
│   └── BaseEntity.java               # Base entity với audit fields
├── configuration/                    # Config classes
│   ├── SecurityConfiguration.java
│   ├── SwaggerConfiguration.java
│   ├── S3Config.java
│   └── ...
├── controller/                       # REST Controllers
│   ├── UserController.java
│   ├── AuthenticationController.java
│   └── ...
├── dto/                              # Data Transfer Objects
│   ├── request/                      # Request DTOs
│   │   ├── UserCreateRequest.java
│   │   └── ...
│   └── response/                     # Response DTOs
│       ├── UserResponse.java
│       └── ...
├── entity/                           # JPA Entities
│   ├── Users.java
│   ├── Role.java
│   └── ...
├── enums/                            # Enums
│   ├── StatusCourse.java
│   └── ...
├── exception/                        # Exception handling
│   ├── AppException.java
│   ├── ErrorCode.java
│   └── GlobalExceptionHandler.java
├── mapper/                           # MapStruct mappers
│   ├── UserMapper.java
│   └── ...
├── repository/                       # JPA Repositories
│   ├── UsersRepo.java
│   └── ...
├── service/                          # Service interfaces
│   ├── UserService.java
│   └── impl/                         # Service implementations
│       ├── UserServiceImpl.java
│       └── ...
└── utils/                            # Utility classes
```

### 3.2. Frontend (ReactJS - Đề xuất)

```
src/
├── main.jsx                          # Entry point
├── App.jsx                           # Root component
├── api/                              # API configurations
│   ├── axiosClient.js                # Axios instance
│   └── endpoints/                    # API endpoint modules
│       ├── userApi.js
│       └── ...
├── assets/                           # Static assets
│   ├── images/
│   └── styles/
├── components/                       # Reusable components
│   ├── common/                       # Common UI components
│   │   ├── Button/
│   │   ├── Input/
│   │   └── Modal/
│   └── layout/                       # Layout components
│       ├── Header/
│       ├── Footer/
│       └── Sidebar/
├── constants/                        # Constants & Enums
│   ├── apiConstants.js
│   ├── errorCodes.js                 # Mirror from backend
│   └── enums.js                      # Mirror from backend
├── contexts/                         # React Context
│   ├── AuthContext.jsx
│   └── ThemeContext.jsx
├── hooks/                            # Custom hooks
│   ├── useAuth.js
│   └── useFetch.js
├── pages/                            # Page components
│   ├── HomePage/
│   ├── LoginPage/
│   └── Dashboard/
├── routes/                           # Route configurations
│   └── index.jsx
├── services/                         # Business logic services
│   ├── authService.js
│   └── userService.js
├── store/                            # State management (Redux/Zustand)
│   ├── slices/
│   └── index.js
└── utils/                            # Utility functions
    ├── formatters.js
    ├── validators.js
    └── storage.js
```

---

## 4. Quy chuẩn API (RESTful)

### 4.1. Quy tắc đặt URL (Endpoint Naming)

| Quy tắc                                 | Đúng ✅                                | Sai ❌                       |
| :-------------------------------------- | :------------------------------------- | :--------------------------- |
| Dùng danh từ, KHÔNG dùng động từ        | `POST /api/v1/users`                   | `POST /api/v1/createUser`    |
| Dùng số nhiều                           | `/users`, `/courses`                   | `/user`, `/course`           |
| Kebab-case                              | `/payment-methods`                     | `/paymentMethods`            |
| Quan hệ cha-con                         | `/courses/{id}/lessons`                | `/courseLessons`             |
| Versioning                              | `/api/v1/...`                          | `/api/...`                   |

### 4.2. HTTP Methods

| Method   | Mục đích                  | Ví dụ                       |
| :------- | :------------------------ | :-------------------------- |
| `GET`    | Lấy dữ liệu               | `GET /api/v1/users`         |
| `POST`   | Tạo mới                   | `POST /api/v1/users`        |
| `PUT`    | Cập nhật toàn bộ          | `PUT /api/v1/users/{id}`    |
| `PATCH`  | Cập nhật một phần         | `PATCH /api/v1/users/{id}`  |
| `DELETE` | Xóa                       | `DELETE /api/v1/users/{id}` |

### 4.3. HTTP Status Codes

| Code  | Ý nghĩa               | Khi nào dùng                      |
| :---- | :-------------------- | :-------------------------------- |
| `200` | OK                    | Request thành công (GET, PUT)     |
| `201` | Created               | Tạo resource thành công (POST)    |
| `204` | No Content            | Xóa thành công                    |
| `400` | Bad Request           | Validation lỗi, dữ liệu không hợp lệ |
| `401` | Unauthorized          | Chưa đăng nhập                    |
| `403` | Forbidden             | Không có quyền                    |
| `404` | Not Found             | Resource không tồn tại            |
| `500` | Internal Server Error | Lỗi server                        |

---

## 5. Quy chuẩn Git Flow

### 5.1. Mô hình nhánh

```
          ┌─────────────┐
          │    main     │  ◄── Production (STABLE)
          └──────┬──────┘
                 │
         merge   │   merge
    ◄────────────┴────────────►
                 │
          ┌──────┴──────┐
          │   develop   │  ◄── Development/Staging
          └──────┬──────┘
                 │
    ┌────────┬───┴───┬────────┐
    │        │       │        │
┌───┴───┐┌───┴───┐┌──┴──┐┌────┴────┐
│ feat/ ││ fix/  ││chore││ hotfix/ │
└───────┘└───────┘└─────┘└─────────┘
```

### 5.2. Naming Convention cho nhánh

Format: `type/short-description` hoặc `type/ticket-id-description`

| Type         | Mục đích                      | Ví dụ                              |
| :----------- | :---------------------------- | :--------------------------------- |
| `feat`       | Tính năng mới                 | `feat/user-authentication`         |
| `fix`        | Sửa lỗi dev/test              | `fix/login-validation`             |
| `refactor`   | Refactor code                 | `refactor/user-service`            |
| `chore`      | Config, setup                 | `chore/docker-setup`               |
| `hotfix`     | Lỗi khẩn cấp Production       | `hotfix/payment-crash`             |

### 5.3. Commit Message

Format: `[TYPE] Description` hoặc `type: description`

| Type        | Mục đích           |
| :---------- | :----------------- |
| `[FEAT]`    | Tính năng mới      |
| `[FIX]`     | Sửa lỗi            |
| `[DOCS]`    | Tài liệu           |
| `[STYLE]`   | Format, styling    |
| `[REFACTOR]`| Refactor           |
| `[TEST]`    | Test cases         |

**Ví dụ:**
- ✅ `[FEAT] Add user registration endpoint`
- ✅ `fix: resolve null pointer in UserService`
- ❌ `update code` (quá chung chung)

---

## 6. Quy chuẩn Naming Convention

### 6.1. Java (Backend)

| Thành phần      | Convention          | Ví dụ                                |
| :-------------- | :------------------ | :----------------------------------- |
| Package         | lowercase           | `com.example.unicode.service`        |
| Class           | PascalCase          | `UserService`, `UserCreateRequest`   |
| Interface       | PascalCase          | `UserRepository`                     |
| Method          | camelCase           | `findByEmail()`, `createUser()`      |
| Variable        | camelCase           | `userId`, `userEmail`                |
| Constant        | UPPER_SNAKE_CASE    | `MAX_RETRY_COUNT`, `DEFAULT_PAGE`    |
| Enum class      | PascalCase          | `StatusCourse`, `UserRole`           |
| Enum value      | UPPER_SNAKE_CASE    | `IN_PROGRESS`, `COMPLETED`           |

### 6.2. File naming

| File type         | Pattern                        | Ví dụ                        |
| :---------------- | :----------------------------- | :--------------------------- |
| Entity            | `{Entity}.java`                | `Users.java`, `Course.java`  |
| Repository        | `{Entity}Repo.java`            | `UsersRepo.java`             |
| Service Interface | `{Entity}Service.java`         | `UserService.java`           |
| Service Impl      | `{Entity}ServiceImpl.java`     | `UserServiceImpl.java`       |
| Controller        | `{Entity}Controller.java`      | `UserController.java`        |
| Mapper            | `{Entity}Mapper.java`          | `UserMapper.java`            |
| Request DTO       | `{Entity}{Action}Request.java` | `UserCreateRequest.java`     |
| Response DTO      | `{Entity}Response.java`        | `UserResponse.java`          |

### 6.3. ReactJS (Frontend)

| Thành phần      | Convention          | Ví dụ                                |
| :-------------- | :------------------ | :----------------------------------- |
| Component       | PascalCase          | `UserProfile.jsx`, `LoginForm.jsx`   |
| Folder          | PascalCase hoặc kebab-case | `UserProfile/`, `login-page/`  |
| Hook            | camelCase (use*)    | `useAuth.js`, `useFetch.js`          |
| Context         | PascalCase          | `AuthContext.jsx`                    |
| Service         | camelCase           | `userService.js`                     |
| Constant        | UPPER_SNAKE_CASE    | `API_BASE_URL`, `MAX_ITEMS`          |
| CSS file        | kebab-case          | `user-profile.css`                   |

---

## 7. Mẫu code Backend (Spring Boot)

### 7.1. Base Entity

```java
package com.example.unicode.base;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @LastModifiedBy
    private String updatedBy;

    private Boolean deleted = false;

    private LocalDateTime deletedAt;

    private String deletedBy;
}
```

### 7.2. Entity

```java
package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    private String avatarUrl;

    private boolean isActive = true;

    // Relationships
    @ManyToMany(mappedBy = "userslist")
    private Set<Role> rolesList = new HashSet<>();
}
```

### 7.3. Repository

```java
package com.example.unicode.repository;

import com.example.unicode.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersRepo extends JpaRepository<Users, UUID> {

    Optional<Users> findByUserIdAndDeletedFalse(UUID userId);

    Optional<Users> findByEmailAndDeletedFalse(String email);

    List<Users> findAllByDeletedFalse();

    boolean existsByEmailAndDeletedFalse(String email);
}
```

### 7.4. Service Interface

```java
package com.example.unicode.service;

import com.example.unicode.dto.request.UserCreateRequest;
import com.example.unicode.dto.request.UserUpdateRequest;
import com.example.unicode.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse create(UserCreateRequest request);

    UserResponse getById(UUID userId);

    List<UserResponse> getAll();

    UserResponse update(UUID userId, UserUpdateRequest request);

    void delete(UUID userId);
}
```

### 7.5. Service Implementation

```java
package com.example.unicode.service.impl;

import com.example.unicode.dto.request.UserCreateRequest;
import com.example.unicode.dto.request.UserUpdateRequest;
import com.example.unicode.dto.response.UserResponse;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.UserMapper;
import com.example.unicode.repository.UsersRepo;
import com.example.unicode.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UsersRepo usersRepo;
    private final UserMapper userMapper;

    @Override
    public UserResponse create(UserCreateRequest request) {
        // Validation: Check if email already exists
        if (usersRepo.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Users user = userMapper.toEntity(request);
        user = usersRepo.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID userId) {
        Users user = usersRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userMapper.toResponseList(usersRepo.findAllByDeletedFalse());
    }

    @Override
    public UserResponse update(UUID userId, UserUpdateRequest request) {
        Users user = usersRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateEntity(request, user);
        user = usersRepo.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public void delete(UUID userId) {
        Users user = usersRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Soft delete
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(getCurrentUser());
        usersRepo.save(user);
    }

    private String getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }
        return authentication.getName();
    }
}
```

### 7.6. Controller

```java
package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.UserCreateRequest;
import com.example.unicode.dto.request.UserUpdateRequest;
import com.example.unicode.dto.response.UserResponse;
import com.example.unicode.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getById(
            @PathVariable UUID userId) {
        UserResponse response = userService.getById(userId);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        List<UserResponse> response = userService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", response));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.update(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user by ID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
}
```

### 7.7. Request DTO

```java
package com.example.unicode.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    private String avatarUrl;

    private Set<String> roleCodes;
}
```

### 7.8. Response DTO

```java
package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID userId;
    private String email;
    private String name;
    private String avatarUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
    private Set<RoleResponse> roles;
}
```

### 7.9. Mapper (MapStruct)

```java
package com.example.unicode.mapper;

import com.example.unicode.dto.request.UserCreateRequest;
import com.example.unicode.dto.request.UserUpdateRequest;
import com.example.unicode.dto.response.UserResponse;
import com.example.unicode.entity.Users;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "rolesList", ignore = true)
    @Mapping(target = "password", ignore = true)
    Users toEntity(UserCreateRequest request);

    @Mapping(source = "rolesList", target = "roles")
    UserResponse toResponse(Users user);

    List<UserResponse> toResponseList(List<Users> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rolesList", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(UserUpdateRequest request, @MappingTarget Users user);
}
```

### 7.10. API Response Wrapper

```java
package com.example.unicode.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int code;
    private String message;
    private boolean success;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(1000)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(1000)
                .message("Successfully")
                .data(data)
                .success(true)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .code(1000)
                .message(message)
                .success(true)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(1004)
                .message(message)
                .build();
    }
}
```

### 7.11. Error Code Enum

```java
package com.example.unicode.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // AUTHENTICATION ERRORS (1xxx)
    INVALID_AUTHENTICATION(1001, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    INVALID_LOGIN_REQUEST(1002, "Username or password wrong", HttpStatus.BAD_REQUEST),

    // TOKEN ERRORS (2xxx)
    REFRESH_TOKEN_NOT_FOUND(2001, "Refresh token not found", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_EXPIRED(2002, "Refresh token has expired", HttpStatus.BAD_REQUEST),

    // USER ERRORS (3xxx)
    USER_NOT_FOUND(3001, "User not found", HttpStatus.NOT_FOUND),
    USER_INACTIVE(3002, "User is inactive", HttpStatus.FORBIDDEN),
    USER_ALREADY_EXISTS(3003, "User with this email already exists", HttpStatus.BAD_REQUEST),

    // ROLE ERRORS (5xxx)
    ROLE_NOT_FOUND(5001, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS(5002, "Role already exists", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
```

### 7.12. Custom Exception

```java
package com.example.unicode.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

### 7.13. Global Exception Handler

```java
package com.example.unicode.exception;

import com.example.unicode.base.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(1004, "Validation failed"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(9999, "Internal server error"));
    }
}
```

---

## 8. Mẫu code Frontend (ReactJS)

### 8.1. Axios Client

```javascript
// src/api/axiosClient.js
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Request interceptor - Add auth token
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle errors
axiosClient.interceptors.response.use(
  (response) => response.data,
  async (error) => {
    const originalRequest = error.config;

    // Handle 401 - Refresh token
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(`${API_BASE_URL}/api/auth/refresh`, {
          refreshToken,
        });
        const { accessToken } = response.data.data;
        localStorage.setItem('accessToken', accessToken);
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return axiosClient(originalRequest);
      } catch (refreshError) {
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosClient;
```

### 8.2. API Endpoint Module

```javascript
// src/api/endpoints/userApi.js
import axiosClient from '../axiosClient';

const USER_ENDPOINT = '/api/v1/users';

const userApi = {
  /**
   * Get all users
   * @returns {Promise} ApiResponse<UserResponse[]>
   */
  getAll: () => axiosClient.get(USER_ENDPOINT),

  /**
   * Get user by ID
   * @param {string} userId - UUID of the user
   * @returns {Promise} ApiResponse<UserResponse>
   */
  getById: (userId) => axiosClient.get(`${USER_ENDPOINT}/${userId}`),

  /**
   * Create new user
   * @param {Object} data - UserCreateRequest
   * @returns {Promise} ApiResponse<UserResponse>
   */
  create: (data) => axiosClient.post(USER_ENDPOINT, data),

  /**
   * Update user
   * @param {string} userId - UUID of the user
   * @param {Object} data - UserUpdateRequest
   * @returns {Promise} ApiResponse<UserResponse>
   */
  update: (userId, data) => axiosClient.put(`${USER_ENDPOINT}/${userId}`, data),

  /**
   * Delete user
   * @param {string} userId - UUID of the user
   * @returns {Promise} ApiResponse<void>
   */
  delete: (userId) => axiosClient.delete(`${USER_ENDPOINT}/${userId}`),

  /**
   * Get current user info
   * @returns {Promise} ApiResponse<UserResponse>
   */
  getMyInfo: () => axiosClient.get(`${USER_ENDPOINT}/me`),
};

export default userApi;
```

### 8.3. Constants - Error Codes (Mirror from Backend)

```javascript
// src/constants/errorCodes.js

/**
 * Error codes mirrored from backend
 * Keep in sync with: com.example.unicode.exception.ErrorCode
 */
export const ERROR_CODES = {
  // AUTHENTICATION ERRORS (1xxx)
  INVALID_AUTHENTICATION: 1001,
  INVALID_LOGIN_REQUEST: 1002,

  // TOKEN ERRORS (2xxx)
  REFRESH_TOKEN_NOT_FOUND: 2001,
  REFRESH_TOKEN_EXPIRED: 2002,

  // USER ERRORS (3xxx)
  USER_NOT_FOUND: 3001,
  USER_INACTIVE: 3002,
  USER_ALREADY_EXISTS: 3003,

  // ROLE ERRORS (5xxx)
  ROLE_NOT_FOUND: 5001,
  ROLE_ALREADY_EXISTS: 5002,
};

/**
 * Error messages for display (Vietnamese)
 */
export const ERROR_MESSAGES = {
  [ERROR_CODES.INVALID_AUTHENTICATION]: 'Thông tin đăng nhập không hợp lệ',
  [ERROR_CODES.INVALID_LOGIN_REQUEST]: 'Tên đăng nhập hoặc mật khẩu sai',
  [ERROR_CODES.REFRESH_TOKEN_NOT_FOUND]: 'Phiên đăng nhập đã hết hạn',
  [ERROR_CODES.REFRESH_TOKEN_EXPIRED]: 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại',
  [ERROR_CODES.USER_NOT_FOUND]: 'Không tìm thấy người dùng',
  [ERROR_CODES.USER_INACTIVE]: 'Tài khoản đã bị vô hiệu hóa',
  [ERROR_CODES.USER_ALREADY_EXISTS]: 'Email này đã được sử dụng',
  [ERROR_CODES.ROLE_NOT_FOUND]: 'Không tìm thấy vai trò',
  [ERROR_CODES.ROLE_ALREADY_EXISTS]: 'Vai trò đã tồn tại',
};

/**
 * Get user-friendly error message
 * @param {number} code - Error code from backend
 * @returns {string} Localized error message
 */
export const getErrorMessage = (code) => {
  return ERROR_MESSAGES[code] || 'Đã xảy ra lỗi. Vui lòng thử lại sau.';
};
```

### 8.4. Constants - Enums (Mirror from Backend)

```javascript
// src/constants/enums.js

/**
 * Enums mirrored from backend
 * Keep in sync with: com.example.unicode.enums.*
 */

export const StatusCourse = {
  COMPLETED: 'COMPLETED',
  IN_PROGRESS: 'IN_PROGRESS',
  NOT_STARTED: 'NOT_STARTED',
};

export const StatusPayment = {
  PENDING: 'PENDING',
  SUCCESS: 'SUCCESS',
  FAILED: 'FAILED',
  CANCELLED: 'CANCELLED',
};

export const ContentType = {
  VIDEO: 'VIDEO',
  DOCUMENT: 'DOCUMENT',
  QUIZ: 'QUIZ',
};

export const QuestionType = {
  SINGLE_CHOICE: 'SINGLE_CHOICE',
  MULTIPLE_CHOICE: 'MULTIPLE_CHOICE',
  TRUE_FALSE: 'TRUE_FALSE',
};

/**
 * Get display label for enum value (Vietnamese)
 */
export const StatusCourseLabel = {
  [StatusCourse.COMPLETED]: 'Hoàn thành',
  [StatusCourse.IN_PROGRESS]: 'Đang học',
  [StatusCourse.NOT_STARTED]: 'Chưa bắt đầu',
};

export const StatusPaymentLabel = {
  [StatusPayment.PENDING]: 'Đang xử lý',
  [StatusPayment.SUCCESS]: 'Thành công',
  [StatusPayment.FAILED]: 'Thất bại',
  [StatusPayment.CANCELLED]: 'Đã hủy',
};
```

### 8.5. Auth Context

```jsx
// src/contexts/AuthContext.jsx
import { createContext, useContext, useState, useEffect } from 'react';
import authApi from '../api/endpoints/authApi';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      if (token) {
        const response = await authApi.getMyInfo();
        setUser(response.data);
      }
    } catch (error) {
      localStorage.clear();
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    const response = await authApi.login(credentials);
    const { accessToken, refreshToken, user } = response.data;
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    setUser(user);
    return response;
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      localStorage.clear();
      setUser(null);
    }
  };

  const value = {
    user,
    loading,
    isAuthenticated: !!user,
    login,
    logout,
    checkAuth,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
```

### 8.6. Custom Hook - useFetch

```javascript
// src/hooks/useFetch.js
import { useState, useEffect, useCallback } from 'react';

/**
 * Custom hook for fetching data
 * @param {Function} fetchFn - API function to call
 * @param {Object} options - Options { immediate: boolean, deps: array }
 */
const useFetch = (fetchFn, options = {}) => {
  const { immediate = true, deps = [] } = options;

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const execute = useCallback(async (...args) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetchFn(...args);
      setData(response.data);
      return response;
    } catch (err) {
      setError(err.response?.data || err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [fetchFn]);

  useEffect(() => {
    if (immediate) {
      execute();
    }
  }, deps);

  return { data, loading, error, execute, setData };
};

export default useFetch;
```

### 8.7. Component - Example Form

```jsx
// src/components/UserForm/UserForm.jsx
import { useState } from 'react';
import PropTypes from 'prop-types';
import './UserForm.css';

const UserForm = ({ onSubmit, initialData, loading }) => {
  const [formData, setFormData] = useState({
    email: initialData?.email || '',
    name: initialData?.name || '',
    password: '',
    avatarUrl: initialData?.avatarUrl || '',
  });

  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    if (!formData.email) {
      newErrors.email = 'Email là bắt buộc';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email không hợp lệ';
    }
    if (!formData.name) {
      newErrors.name = 'Tên là bắt buộc';
    }
    if (!initialData && !formData.password) {
      newErrors.password = 'Mật khẩu là bắt buộc';
    } else if (formData.password && formData.password.length < 6) {
      newErrors.password = 'Mật khẩu phải có ít nhất 6 ký tự';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // Clear error on change
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validate()) {
      onSubmit(formData);
    }
  };

  return (
    <form className="user-form" onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          className={errors.email ? 'error' : ''}
          disabled={loading}
        />
        {errors.email && <span className="error-message">{errors.email}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="name">Tên</label>
        <input
          type="text"
          id="name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          className={errors.name ? 'error' : ''}
          disabled={loading}
        />
        {errors.name && <span className="error-message">{errors.name}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="password">Mật khẩu</label>
        <input
          type="password"
          id="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          className={errors.password ? 'error' : ''}
          disabled={loading}
          placeholder={initialData ? 'Để trống nếu không đổi' : ''}
        />
        {errors.password && <span className="error-message">{errors.password}</span>}
      </div>

      <button type="submit" disabled={loading}>
        {loading ? 'Đang xử lý...' : initialData ? 'Cập nhật' : 'Tạo mới'}
      </button>
    </form>
  );
};

UserForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  initialData: PropTypes.object,
  loading: PropTypes.bool,
};

UserForm.defaultProps = {
  initialData: null,
  loading: false,
};

export default UserForm;
```

### 8.8. Page Component - Example

```jsx
// src/pages/UsersPage/UsersPage.jsx
import { useState, useEffect } from 'react';
import userApi from '../../api/endpoints/userApi';
import useFetch from '../../hooks/useFetch';
import { getErrorMessage } from '../../constants/errorCodes';
import UserForm from '../../components/UserForm/UserForm';
import './UsersPage.css';

const UsersPage = () => {
  const { data: users, loading, error, execute: fetchUsers } = useFetch(
    userApi.getAll,
    { immediate: true }
  );

  const [creating, setCreating] = useState(false);
  const [showForm, setShowForm] = useState(false);

  const handleCreate = async (formData) => {
    setCreating(true);
    try {
      await userApi.create(formData);
      setShowForm(false);
      fetchUsers(); // Refresh list
    } catch (err) {
      const errorCode = err.response?.data?.code;
      alert(getErrorMessage(errorCode));
    } finally {
      setCreating(false);
    }
  };

  const handleDelete = async (userId) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa?')) return;

    try {
      await userApi.delete(userId);
      fetchUsers();
    } catch (err) {
      const errorCode = err.response?.data?.code;
      alert(getErrorMessage(errorCode));
    }
  };

  if (loading) return <div className="loading">Đang tải...</div>;
  if (error) return <div className="error">Lỗi: {getErrorMessage(error.code)}</div>;

  return (
    <div className="users-page">
      <header className="page-header">
        <h1>Quản lý người dùng</h1>
        <button onClick={() => setShowForm(true)}>Thêm mới</button>
      </header>

      {showForm && (
        <div className="modal">
          <div className="modal-content">
            <h2>Tạo người dùng mới</h2>
            <UserForm onSubmit={handleCreate} loading={creating} />
            <button onClick={() => setShowForm(false)}>Hủy</button>
          </div>
        </div>
      )}

      <table className="users-table">
        <thead>
          <tr>
            <th>Email</th>
            <th>Tên</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {users?.map((user) => (
            <tr key={user.userId}>
              <td>{user.email}</td>
              <td>{user.name}</td>
              <td>
                <span className={`status ${user.isActive ? 'active' : 'inactive'}`}>
                  {user.isActive ? 'Hoạt động' : 'Vô hiệu'}
                </span>
              </td>
              <td>
                <button onClick={() => handleDelete(user.userId)}>Xóa</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UsersPage;
```

### 8.9. Service Layer

```javascript
// src/services/userService.js
import userApi from '../api/endpoints/userApi';
import { getErrorMessage } from '../constants/errorCodes';

/**
 * User service - Business logic layer
 */
const userService = {
  /**
   * Get all users with error handling
   */
  async getAllUsers() {
    try {
      const response = await userApi.getAll();
      return { success: true, data: response.data };
    } catch (error) {
      return {
        success: false,
        error: getErrorMessage(error.response?.data?.code),
      };
    }
  },

  /**
   * Create user with validation
   */
  async createUser(userData) {
    // Client-side validation
    if (!userData.email || !userData.name) {
      return { success: false, error: 'Vui lòng điền đầy đủ thông tin' };
    }

    try {
      const response = await userApi.create(userData);
      return { success: true, data: response.data };
    } catch (error) {
      return {
        success: false,
        error: getErrorMessage(error.response?.data?.code),
      };
    }
  },

  /**
   * Update user
   */
  async updateUser(userId, userData) {
    try {
      const response = await userApi.update(userId, userData);
      return { success: true, data: response.data };
    } catch (error) {
      return {
        success: false,
        error: getErrorMessage(error.response?.data?.code),
      };
    }
  },

  /**
   * Delete user
   */
  async deleteUser(userId) {
    try {
      await userApi.delete(userId);
      return { success: true };
    } catch (error) {
      return {
        success: false,
        error: getErrorMessage(error.response?.data?.code),
      };
    }
  },
};

export default userService;
```

### 8.10. Utils - Storage

```javascript
// src/utils/storage.js

const STORAGE_PREFIX = 'unicode_';

/**
 * Storage utility with prefix
 */
const storage = {
  get(key) {
    try {
      const item = localStorage.getItem(STORAGE_PREFIX + key);
      return item ? JSON.parse(item) : null;
    } catch {
      return null;
    }
  },

  set(key, value) {
    try {
      localStorage.setItem(STORAGE_PREFIX + key, JSON.stringify(value));
    } catch (error) {
      console.error('Storage set error:', error);
    }
  },

  remove(key) {
    localStorage.removeItem(STORAGE_PREFIX + key);
  },

  clear() {
    Object.keys(localStorage)
      .filter((key) => key.startsWith(STORAGE_PREFIX))
      .forEach((key) => localStorage.removeItem(key));
  },
};

export default storage;
```

---

## 9. Xử lý lỗi & Error Codes

### 9.1. Cấu trúc mã lỗi

Format: `CATEGORY_NUMBER` (e.g., `3001`)

| Range     | Category          | Ví dụ                          |
| :-------- | :---------------- | :----------------------------- |
| 1xxx      | Authentication    | 1001 - Invalid credentials     |
| 2xxx      | Token             | 2001 - Token not found         |
| 3xxx      | User              | 3001 - User not found          |
| 4xxx      | Privilege         | 4001 - Privilege not found     |
| 5xxx      | Role              | 5001 - Role not found          |
| 6xxx      | Certificate       | 6001 - Certificate not found   |
| 7xxx      | Course            | 7001 - Course not found        |
| 9xxx      | System            | 9999 - Internal server error   |

### 9.2. Quy trình xử lý lỗi

1. **Backend**: Ném `AppException` với `ErrorCode` tương ứng
2. **GlobalExceptionHandler**: Bắt exception, trả về `ApiResponse.error()`
3. **Frontend**: Nhận error code, tra cứu `ERROR_MESSAGES` để hiển thị

> ⚠️ **QUAN TRỌNG**: Không bao giờ hiển thị message gốc từ Exception (NullPointerException, etc.) cho user cuối!

---

## 10. Best Practices

### 10.1. Backend

| Quy tắc                              | Mô tả                                                       |
| :----------------------------------- | :---------------------------------------------------------- |
| Luôn dùng `@Transactional`           | Đặt ở class-level cho Service implementation                |
| Dùng `readOnly = true` cho GET       | `@Transactional(readOnly = true)` cho các method chỉ đọc    |
| Soft Delete                          | Không xóa thật, set `deleted = true`                        |
| Validation ở DTO                     | Dùng Jakarta Validation annotations                         |
| Logging                              | Log ở service layer, không log ở controller                 |
| Mapper                               | Luôn dùng MapStruct, không map thủ công                     |

### 10.2. Frontend

| Quy tắc                              | Mô tả                                                       |
| :----------------------------------- | :---------------------------------------------------------- |
| Mirror Enums & ErrorCodes            | Sync với backend, đặt trong `/constants`                    |
| API Error Handling                   | Dùng interceptor để handle globally                         |
| Form Validation                      | Validate cả client-side và server-side                      |
| State Management                     | Dùng Context cho auth, Zustand/Redux cho complex state      |
| Component Structure                  | Folder-based: `ComponentName/index.jsx`, `ComponentName.css`|

### 10.3. Cả hai

| Quy tắc                              | Mô tả                                                       |
| :----------------------------------- | :---------------------------------------------------------- |
| Không hardcode                       | Dùng constants, env variables                               |
| Code review                          | Tối thiểu 1 reviewer trước khi merge                        |
| Documentation                        | JSDoc cho FE, JavaDoc cho BE                                |
| Git commit                           | Tuân thủ Conventional Commits                               |

---

## 📚 Tài liệu tham khảo

- [GIT_GUIDELINE.md](./GIT_GUIDELINE.md) - Quy chuẩn Git Flow chi tiết
- [PROJECT_SOP.md](./PROJECT_SOP.md) - Quy chuẩn kỹ thuật & luồng phát triển

---

> **Lưu ý**: Tài liệu này cần được cập nhật khi có thay đổi về kiến trúc hoặc quy chuẩn dự án.
> 
> **Ngày cập nhật**: 2026-01-24
