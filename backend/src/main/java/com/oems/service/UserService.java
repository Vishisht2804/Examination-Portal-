package com.oems.service;

import com.oems.dto.UserCreateRequest;
import com.oems.dto.UserResponse;
import com.oems.dto.UserUpdateRequest;
import com.oems.exception.ApiException;
import com.oems.model.User;
import com.oems.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapperService mapperService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MapperService mapperService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapperService = mapperService;
    }

    public UserResponse create(UserCreateRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username already exists");
        });
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists");
        });
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(request.role())
                .active(true)
                .build();
        return mapperService.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(mapperService::toUserResponse).toList();
    }

    public UserResponse update(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        boolean wasInactive = !user.isActive();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setActive(request.active());
        if (wasInactive && request.active()) {
            user.setFailedLoginAttempts(0);
        }
        return mapperService.toUserResponse(userRepository.save(user));
    }

    public void deactivate(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        user.setActive(false);
        userRepository.save(user);
    }
}

