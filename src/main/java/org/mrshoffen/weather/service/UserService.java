package org.mrshoffen.weather.service;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.dto.UserRegistrationDto;
import org.mrshoffen.weather.dto.UserResponseDto;
import org.mrshoffen.weather.entity.User;
import org.mrshoffen.weather.exception.EntityAlreadyExistException;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.repository.UserRepository;
import org.mrshoffen.weather.util.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

import static org.mrshoffen.weather.util.PasswordEncoder.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto register(UserRegistrationDto registrationDto) {
        String hashedPassword = hashPassword(registrationDto.getPassword());
        registrationDto.setPassword(hashedPassword);

        User user = userMapper.toEntity(registrationDto);

        userRepository.findByUsername(registrationDto.getUsername())
                .ifPresentOrElse(
                        us -> {
                            throw new EntityAlreadyExistException("User with username '%s' already exists!"
                                    .formatted(us.getUsername())
                            );
                        },
                        () -> userRepository.save(user)
                );

        System.out.println();
        //todo think about returning value

        return userMapper.toResponseDto(user);


    }


}
