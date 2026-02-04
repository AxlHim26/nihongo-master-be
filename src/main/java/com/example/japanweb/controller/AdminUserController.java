package com.example.japanweb.controller;

import com.example.japanweb.dto.common.ApiResponse;
import com.example.japanweb.dto.request.admin.CreateUserRequest;
import com.example.japanweb.dto.response.user.UserSummaryDTO;
import com.example.japanweb.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
}
