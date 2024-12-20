package org.mrshoffen.weather.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.exception.UserAlreadyExistsException;
import org.mrshoffen.weather.exception.authentication.IncorrectPasswordException;
import org.mrshoffen.weather.exception.authentication.UserNotFoundException;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.model.dto.in.UserEditPasswordDto;
import org.mrshoffen.weather.model.dto.in.UserEditProfileDto;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.repository.UserRepository;
import org.mrshoffen.weather.util.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mrshoffen.weather.util.PasswordEncoder.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final SessionService sessionService;


    @Transactional
    public UserResponseDto save(UserRegistrationDto registrationDto) {
        checkForOccupiedUsername(registrationDto.getUsername());

        User user = userMapper.toEntity(registrationDto);
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public UserResponseDto updateUserProfile(Integer userId, UserEditProfileDto userProfileEditDto) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("User with id '%s' not found".formatted(userId)));

        String newUsername = userProfileEditDto.getNewUsername();
        String newAvatarUrl = userProfileEditDto.getNewAvatarUrl();

        if (!user.getUsername().equals(newUsername)) {
            checkForOccupiedUsername(newUsername);
        }

        user.setUsername(newUsername);
        user.setAvatarUrl(newAvatarUrl);
        userRepository.save(user);

        return userMapper.toResponseDto(user);
    }

    private void checkForOccupiedUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(u -> {
                    throw new UserAlreadyExistsException("User with username '%s' already exists!"
                            .formatted(u.getUsername()));
                });
    }


    @Transactional
    public UserResponseDto updateUserPassword(Integer userId, UserEditPasswordDto userEditPasswordDto) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("User with id '%s' not found".formatted(userId)));

        String oldPassword = userEditPasswordDto.getOldPassword();
        String newPassword = userEditPasswordDto.getNewPassword();

        if (!arePasswordsEqual(oldPassword, user.getPassword())) {
            throw new IncorrectPasswordException("Incorrect password!");
        }

        user.setPassword(hashPassword(newPassword));
        userRepository.save(user);

        return userMapper.toResponseDto(user);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        sessionService.removeAllUserSessions(userId);
        userRepository.deleteById(userId);
    }

}
