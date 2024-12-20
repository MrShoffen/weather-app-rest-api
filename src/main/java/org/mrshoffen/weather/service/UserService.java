package org.mrshoffen.weather.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.exception.UserAlreadyExistsException;
import org.mrshoffen.weather.exception.authentication.UserNotFoundException;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.model.dto.in.UserEditDto;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;


    @Transactional
    public UserResponseDto save(UserRegistrationDto registrationDto) {
        userRepository.findByUsername(registrationDto.getUsername())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User with username '%s' already exists!"
                            .formatted(user.getUsername()));
                });

        User user = userMapper.toEntity(registrationDto);
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    UserResponseDto update(Integer userId, UserEditDto userEditDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id '%d' not found".formatted(userId)));

        if (!user.getUsername().equals(userEditDto.getUsername())) {
            user = updateUsername(user, userEditDto.getUsername());
        }

        if (!user.getAvatarUrl().equals(userEditDto.getAvatarUrl())) {
            user.setAvatarUrl(userEditDto.getAvatarUrl());
        }

        return null;
    }

    private User updateUsername(User user, String username) {
        //todo check if present
        user.setUsername(username);
        return user;
    }

}
