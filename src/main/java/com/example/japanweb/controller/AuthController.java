package com.example.japanweb.controller;

import com.example.japanweb.dto.common.ApiResponse;
import com.example.japanweb.dto.request.auth.AuthRequest;
import com.example.japanweb.dto.response.auth.AuthResponse;
import com.example.japanweb.dto.request.auth.RegisterRequest;
import com.example.japanweb.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = service.register(request);
        return ApiResponse.created(response);
    }

    @PostMapping("/authenticate")
    public ApiResponse<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = service.authenticate(request);
        return ApiResponse.success(response, "Authentication successful");
    }
}
