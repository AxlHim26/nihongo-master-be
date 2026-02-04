package com.example.japanweb.service;

import com.example.japanweb.dto.request.admin.CreateUserRequest;
import com.example.japanweb.dto.response.user.UserSummaryDTO;

import java.util.List;

public interface AdminUserService {
    UserSummaryDTO createUser(CreateUserRequest request);
    List<UserSummaryDTO> getUsers();
}
