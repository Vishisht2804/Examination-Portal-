package com.oems.service;

import com.oems.dto.LoginRequest;
import com.oems.dto.LoginResponse;
import com.oems.dto.MeResponse;
import com.oems.exception.ApiException;
import com.oems.model.User;
import com.oems.repository.UserRepository;
import com.oems.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       CurrentUserService currentUserService,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!user.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User is deactivated");
        }
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getId(), user.getRole(), user.getFullName());
    }

    public MeResponse me() {
        User user = currentUserService.requireCurrentUser();
        return new MeResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getRole(), user.isActive());
    }
}
