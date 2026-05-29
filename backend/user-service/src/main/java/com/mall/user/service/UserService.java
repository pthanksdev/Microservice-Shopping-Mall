package com.mall.user.service;

import com.mall.common.exception.ResourceNotFoundException;
import com.mall.user.dto.UserResponse;
import com.mall.user.model.User;
import com.mall.user.model.UserStatus;
import com.mall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserResponse.from(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(String id, String firstName, String lastName, String profileImageUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (profileImageUrl != null) user.setProfileImageUrl(profileImageUrl);

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void softDeleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("User {} soft-deleted", id);
    }
}
