package com.oems.controller;

import com.oems.dto.LoginRequest;
import com.oems.dto.LoginResponse;
import com.oems.dto.MeResponse;
import com.oems.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/me")
    public MeResponse me() {
        return authService.me();
    }
}
