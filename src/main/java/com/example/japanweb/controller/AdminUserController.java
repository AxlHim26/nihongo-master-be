package com.example.japanweb.controller;

import com.example.japanweb.dto.common.ApiResponse;
import com.example.japanweb.dto.request.admin.CreateUserRequest;
import com.example.japanweb.dto.request.admin.UpdateUserRequest;
import com.example.japanweb.dto.response.user.UserSummaryDTO;
import com.example.japanweb.entity.User;
import com.example.japanweb.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<List<UserSummaryDTO>> getUsers() {
        return ApiResponse.success(adminUserService.getUsers(), "Users fetched successfully");
    }

    @PostMapping
    public ApiResponse<UserSummaryDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success(adminUserService.createUser(request), "User created successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<UserSummaryDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ApiResponse.success(adminUserService.updateUser(id, request), "User updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id, @AuthenticationPrincipal User actor) {
        adminUserService.deleteUser(id, actor.getId());
        return ApiResponse.success(null, "User deleted successfully");
    }
}
