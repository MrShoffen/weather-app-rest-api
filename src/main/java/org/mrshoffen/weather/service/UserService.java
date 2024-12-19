package org.mrshoffen.weather.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.exception.UserAlreadyExistsException;
import org.mrshoffen.weather.exception.authentication.UserNotFoundException;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User with username '%s' not found!"
                                .formatted(username))
                );
    }

}
