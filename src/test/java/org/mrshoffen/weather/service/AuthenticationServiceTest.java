package org.mrshoffen.weather.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mrshoffen.weather.exception.authentication.IncorrectPasswordException;
import org.mrshoffen.weather.exception.authentication.UserNotFoundException;
import org.mrshoffen.weather.model.dto.in.UserLoginDto;
import org.mrshoffen.weather.model.dto.out.SessionResponseDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.service.AuthenticationService;
import org.mrshoffen.weather.service.SessionService;
import org.mrshoffen.weather.service.UserService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mrshoffen.weather.util.PasswordEncoder.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    UserService userService;

    @Mock
    SessionService sessionService;

    @InjectMocks
    AuthenticationService authenticationService;


    @Test
    void login_UsernameAndPasswordCorrect_SuccessReturnUserResponse() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("test", "password");
        User user = new User(1, "test", hashPassword("password"), "avatar_url");

        doReturn(Optional.of(user))
                .when(userService)
                .findByUsername("test");

        UserResponseDto responseUser = new UserResponseDto(1, "test", "avatar_url");
        SessionResponseDto expectedSessionDto = new SessionResponseDto(UUID.randomUUID(), responseUser);

        doReturn(expectedSessionDto)
                .when(sessionService)
                .createSession(user);

        //when
        SessionResponseDto sessionAfterLogin = authenticationService.login(userLoginDto);

        //then
        assertThat(sessionAfterLogin).isEqualTo(expectedSessionDto);

        verify(userService).findByUsername("test");
        verify(sessionService).createSession(user);
    }

    @Test
    void login_UsernameIncorrect_ThrowsException() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("test", "password");
        User user = new User(1, "test", hashPassword("password"), "avatar_url");

        doReturn(Optional.empty())
                .when(userService)
                .findByUsername("test");

        //then
       assertThatThrownBy(() -> authenticationService.login(userLoginDto))
               .isInstanceOf(UserNotFoundException.class)
                       .hasMessage("User with username 'test' not found!");

        verify(userService).findByUsername("test");
        verify(sessionService, never()).createSession(user);
    }

    @Test
    void login_PasswordIncorrect_ThrowsException() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("test", "wrong_pass");
        User user = new User(1, "test", hashPassword("password"), "avatar_url");

        doReturn(Optional.of(user))
                .when(userService)
                .findByUsername("test");

        //then
        assertThatThrownBy(() -> authenticationService.login(userLoginDto))
                .isInstanceOf(IncorrectPasswordException.class)
                .hasMessage("Incorrect password!");

        verify(userService).findByUsername("test");
        verify(sessionService, never()).createSession(user);
    }

}