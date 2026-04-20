package com.oems.service;

import com.oems.dto.LoginRequest;
import com.oems.dto.LoginResponse;
import com.oems.dto.MeResponse;
import com.oems.exception.ApiException;
import com.oems.model.User;
import com.oems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import com.oems.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final int maxFailedLoginAttempts;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       CurrentUserService currentUserService,
                       UserRepository userRepository,
                       @Value("${app.auth.max-failed-login-attempts:3}") int maxFailedLoginAttempts) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.maxFailedLoginAttempts = maxFailedLoginAttempts;
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.username());
        if (optionalUser.isPresent() && !optionalUser.get().isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Account is locked or deactivated. Contact admin");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException ex) {
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                if (user.getFailedLoginAttempts() >= maxFailedLoginAttempts) {
                    user.setActive(false);
                    userRepository.save(user);
                    throw new ApiException(HttpStatus.FORBIDDEN, "Account locked after too many failed login attempts. Contact admin");
                }
                userRepository.save(user);
            }
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User user = optionalUser.orElseGet(() -> userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials")));
        if (!user.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Account is locked or deactivated. Contact admin");
        }
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getId(), user.getRole(), user.getFullName());
    }

    public MeResponse me() {
        User user = currentUserService.requireCurrentUser();
        return new MeResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getRole(), user.isActive());
    }
}
