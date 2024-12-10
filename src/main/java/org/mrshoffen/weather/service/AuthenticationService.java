package org.mrshoffen.weather.service;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.dto.UserLoginDto;
import org.mrshoffen.weather.dto.UserRegistrationDto;
import org.mrshoffen.weather.dto.UserResponseDto;
import org.mrshoffen.weather.entity.User;
import org.mrshoffen.weather.exception.IncorrectPasswordException;
import org.mrshoffen.weather.exception.UserAlreadyExistsException;
import org.mrshoffen.weather.exception.UserNotFoundException;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mrshoffen.weather.util.PasswordEncoder.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final SessionService sessionService;

    @Transactional
    public UserResponseDto register(UserRegistrationDto registrationDto) {
        userRepository.findByUsername(registrationDto.getUsername())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User with username '%s' already exists!"
                            .formatted(user.getUsername()));
                });

        registrationDto.setPassword(hashPassword(registrationDto.getPassword()));
        User user = userMapper.toEntity(registrationDto);
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    public UUID login(UserLoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with username '%s' not found!"
                        .formatted(loginDto.getUsername())));

        if (!arePasswordsEqual(loginDto.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Invalid password!");
        }

        return sessionService.createSession(user).getId();
    }


}
