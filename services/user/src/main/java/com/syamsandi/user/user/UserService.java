package com.syamsandi.user.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserResponse> findAllUsersExceptSelf(Authentication authentication) {
        String currentUser = authentication.getName();
        return repository.findAllUsersExceptSelf(currentUser)
                .stream()
                .map(mapper::toUserResponse)
                .toList();
    }
}
