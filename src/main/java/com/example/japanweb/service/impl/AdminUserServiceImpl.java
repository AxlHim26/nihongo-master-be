package com.example.japanweb.service.impl;

import com.example.japanweb.dto.request.admin.CreateUserRequest;
import com.example.japanweb.dto.request.admin.UpdateUserRequest;
import com.example.japanweb.dto.response.user.UserSummaryDTO;
import com.example.japanweb.entity.User;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.repository.UserRepository;
import com.example.japanweb.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserSummaryDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(ErrorCode.AUTH_USERNAME_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.AUTH_EMAIL_EXISTS);
        }

        User.Role role = request.getRole() != null ? request.getRole() : User.Role.USER;
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        User saved = userRepository.save(user);
        return toSummary(saved);
    }

    @Override
    public UserSummaryDTO updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_USER_NOT_FOUND));

        String normalizedUsername = request.getUsername().trim();
        if (!normalizedUsername.equals(user.getUsername())
                && userRepository.existsByUsername(normalizedUsername)) {
            throw new ApiException(ErrorCode.AUTH_USERNAME_EXISTS);
        }

        String normalizedEmail = request.getEmail().trim();
        if (!normalizedEmail.equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(normalizedEmail)) {
            throw new ApiException(ErrorCode.AUTH_EMAIL_EXISTS);
        }

        User.Role targetRole = request.getRole() != null ? request.getRole() : user.getRole();
        if (user.getRole() == User.Role.ADMIN
                && targetRole != User.Role.ADMIN
                && userRepository.countByRole(User.Role.ADMIN) <= 1) {
            throw new ApiException(ErrorCode.AUTH_ACCESS_DENIED, "Cannot demote the last admin account");
        }

        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setRole(targetRole);

        String newPassword = normalizeText(request.getPassword());
        if (newPassword != null) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        User saved = userRepository.save(user);
        return toSummary(saved);
    }

    @Override
    public void deleteUser(Long userId, Long actorUserId) {
        if (userId.equals(actorUserId)) {
            throw new ApiException(ErrorCode.AUTH_ACCESS_DENIED, "You cannot delete your own account");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_USER_NOT_FOUND));

        if (user.getRole() == User.Role.ADMIN && userRepository.countByRole(User.Role.ADMIN) <= 1) {
            throw new ApiException(ErrorCode.AUTH_ACCESS_DENIED, "Cannot delete the last admin account");
        }

        userRepository.delete(user);
    }

    @Override
    public List<UserSummaryDTO> getUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toSummary)
                .toList();
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private UserSummaryDTO toSummary(User user) {
        return UserSummaryDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
