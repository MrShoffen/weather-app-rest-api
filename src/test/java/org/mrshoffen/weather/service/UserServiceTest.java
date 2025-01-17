package org.mrshoffen.weather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mrshoffen.weather.exception.authentication.UserAlreadyExistsException;
import org.mrshoffen.weather.exception.authentication.IncorrectPasswordException;
import org.mrshoffen.weather.exception.authentication.UserNotFoundException;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.model.dto.in.UserEditPasswordDto;
import org.mrshoffen.weather.model.dto.in.UserEditProfileDto;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mrshoffen.weather.util.PasswordEncoder.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    SessionService sessionService;

    @InjectMocks
    UserProfileService userService;

    UserResponseDto mockUserResponseDto;

    @BeforeEach
    void setUp() {

        mockUserResponseDto = new UserResponseDto(1, "test_user", "test_avatar");
    }

    @Test
    void save_UsernameIsFree_SuccessReturnSavedEntity() {
        //given
        User user = new User(1, "test", "hashed_pass", "test_avatar");
        doReturn(Optional.empty())
                .when(userRepository)
                .findByUsernameIgnoreCase("test");

        UserRegistrationDto registrationDto = new UserRegistrationDto("test", "hashed_pass", "test_avatar");
        doReturn(user)
                .when(userMapper)
                .toEntity(registrationDto);

        doReturn(user)
                .when(userRepository)
                .save(user);

        doReturn(mockUserResponseDto)
                .when(userMapper)
                .toResponseDto(user);

        //when
        UserResponseDto response = userService.save(registrationDto);

        //then
        assertThat(response).isEqualTo(mockUserResponseDto);

        verify(userRepository).findByUsernameIgnoreCase("test");
        verify(userRepository).save(user);
    }

    @Test
    void save_UsernameIsAlreadyExist_ThrowException() {
        //given
        User user = new User(1, "test", "hashed_pass", "test_avatar");
        doReturn(Optional.of(user))
                .when(userRepository)
                .findByUsernameIgnoreCase("test");

        UserRegistrationDto registrationDto = new UserRegistrationDto("test", "hashed_pass", "test_avatar");

        //then
        assertThatThrownBy(() -> userService.save(registrationDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with username 'test' already exists!");

        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUserProfile_NewUsername_SuccessReturnUpdatedEntity() {
        //given
        User user = new User(1, "test", "hashed_pass", "test_avatar");
        UserEditProfileDto userEditDto = new UserEditProfileDto("test_new", "new_avatar");
        User updatedUser = new User(1, "test_new", "hashed_pass", "new_avatar");


        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(1);

        doReturn(updatedUser)
                .when(userRepository)
                .save(updatedUser);

        UserResponseDto updatedDto = new UserResponseDto(1, "test_new", "new_avatar");

        doReturn(updatedDto)
                .when(userMapper)
                .toResponseDto(updatedUser);

        //when
        UserResponseDto response = userService.updateUserProfile(1, userEditDto);

        //then
        assertThat(response).isEqualTo(updatedDto);

        verify(userRepository).findById(1);
        verify(userRepository).save(updatedUser);
    }

    @Test
    void updateUserProfile_CurrentUserNotFound_ThrowException() {
        //given
        User user = new User(1, "test", "hashed_pass", "test_avatar");
        doReturn(Optional.empty())
                .when(userRepository)
                .findById(1);

        UserEditProfileDto userEditProfileDto = new UserEditProfileDto("new_test", "new_avatar_url");

        //then
        assertThatThrownBy(() -> userService.updateUserProfile(1, userEditProfileDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id '1' not found");

        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUserProfile_NewUsernameIsAlreadyExist_ThrowException() {
        //given
        User user = new User(1, "test", "hashed_pass", "test_avatar");
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(1);

        UserEditProfileDto userEditProfileDto = new UserEditProfileDto("new_test", "new_avatar_url");
        doReturn(Optional.of(new User(2, "new_test", "another_pass", "new_avatar_url")))
                .when(userRepository)
                .findByUsernameIgnoreCase("new_test");

        //then
        assertThatThrownBy(() -> userService.updateUserProfile(1, userEditProfileDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with username 'new_test' already exists!");

        verify(userRepository, never()).save(user);
    }


    @Test
    void updateUserPassword_OldPasswordIsCorrect_SuccessReturnUpdatedEntity() {
        //given
        User user = new User(1, "test", hashPassword("old_password"), "test_avatar");
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(1);

        UserEditPasswordDto editPassDto = new UserEditPasswordDto("old_password", "new_password");

        User updatedUser = new User(1, "test", hashPassword("new_password"), "test_avatar");
        doReturn(updatedUser)
                .when(userRepository)
                .save(any(User.class));

        doReturn(mockUserResponseDto)
                .when(userMapper)
                .toResponseDto(any(User.class));

        //when
        UserResponseDto response = userService.updateUserPassword(1, editPassDto);

        //then

        assertThat(response).isEqualTo(mockUserResponseDto);

        verify(userRepository).findById(1);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserPassword_OldPasswordIncorrect_ThrowException() {
        //given
        String oldPassword = "old_password";
        User user = new User(1, "test", hashPassword(oldPassword), "test_avatar");
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(1);

        UserEditPasswordDto editPassDto = new UserEditPasswordDto("wrong_pass", "new_password");

        //then
        assertThatThrownBy(() -> userService.updateUserPassword(1, editPassDto))
                .isInstanceOf(IncorrectPasswordException.class)
                .hasMessage("Incorrect password!");

        verify(userRepository, never()).save(user);
    }

}