package com.teasound.teasound_api.service;

import org.springframework.stereotype.Service;

import com.teasound.teasound_api.dto.request.ChangePasswordRequest;
import com.teasound.teasound_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
