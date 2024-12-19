package org.mrshoffen.weather.service;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.in.UserLoginDto;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.SessionResponseDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.model.entity.UserSession;
import org.mrshoffen.weather.exception.authentication.IncorrectPasswordException;
import org.mrshoffen.weather.exception.UserAlreadyExistsException;
import org.mrshoffen.weather.exception.authentication.UserNotFoundException;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.repository.UserRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mrshoffen.weather.util.PasswordEncoder.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;

    private final SessionService sessionService;

    public UserResponseDto register(UserRegistrationDto registrationDto) {
        registrationDto.setPassword(hashPassword(registrationDto.getPassword()));

        return userService.save(registrationDto);
    }

    @Transactional
    public SessionResponseDto login(UserLoginDto loginDto) {
        User user = userService.findByUsername(loginDto.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("User with username '%s' not found!"
                                .formatted(loginDto.getPassword()))
                );

        if (!arePasswordsEqual(loginDto.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Incorrect password!");
        }

        return sessionService.createSession(user);
    }

    @Transactional
    public void logout(UUID sessionId) {
        UserSession session = sessionService.getSessionById(sessionId);
        sessionService.removeSession(session);
    }


}
