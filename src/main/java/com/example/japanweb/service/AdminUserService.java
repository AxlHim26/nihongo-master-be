package com.example.japanweb.service;

import com.example.japanweb.dto.request.admin.CreateUserRequest;
import com.example.japanweb.dto.request.admin.UpdateUserRequest;
import com.example.japanweb.dto.response.user.UserSummaryDTO;

import java.util.List;

public interface AdminUserService {
    UserSummaryDTO createUser(CreateUserRequest request);
    UserSummaryDTO updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId, Long actorUserId);
    List<UserSummaryDTO> getUsers();
}
