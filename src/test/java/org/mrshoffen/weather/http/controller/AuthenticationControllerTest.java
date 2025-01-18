package org.mrshoffen.weather.http.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mrshoffen.weather.model.dto.in.UserLoginDto;
import org.mrshoffen.weather.model.dto.in.UserRegistrationDto;
import org.mrshoffen.weather.model.dto.out.SessionResponseDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mrshoffen.weather.util.CookieUtil.clearCustomCookie;
import static org.mrshoffen.weather.util.CookieUtil.createCustomCookie;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    int sessionCookieAge = 1;

    String sessionCookieName = "CUSTOM_SESSION";

    @Mock
    AuthenticationService authenticationService;

    @Mock
    HttpServletResponse httpServletResponse;

    @InjectMocks
    AuthenticationController authenticationController;


    @BeforeEach
    void setUp() {
        setField(authenticationController, "sessionCookieName", sessionCookieName);
        setField(authenticationController, "sessionCookieAge", sessionCookieAge);

    }

    @Test
    void register() throws URISyntaxException {
        //given
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("test", "test_password", "test_avatar_url");
        UserResponseDto expectedRegisteredUser = new UserResponseDto(1, "test_user", "test_avatar_url");

        doReturn(expectedRegisteredUser)
                .when(authenticationService)
                .register(userRegistrationDto);

        //when
        ResponseEntity<UserResponseDto> registered = authenticationController.register(userRegistrationDto);

        //then
        assertThat(registered.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registered.getBody()).isEqualTo(expectedRegisteredUser);

        verify(authenticationService).register(userRegistrationDto);
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    void login() {
        //given
        UserLoginDto userLoginDto = new UserLoginDto("test_user", "test_password");
        UserResponseDto expectedRegisteredUser = new UserResponseDto(1, "test_user", "test_avatar_url");

        UUID sessionId = UUID.randomUUID();
        SessionResponseDto sessionResponseDto = new SessionResponseDto(sessionId, expectedRegisteredUser);

        doReturn(sessionResponseDto)
                .when(authenticationService)
                .login(userLoginDto);

        //when
        ResponseEntity<UserResponseDto> loginUser = authenticationController.login(userLoginDto, httpServletResponse);

        //then
        assertThat(loginUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginUser.getBody()).isEqualTo(expectedRegisteredUser);

        verify(authenticationService).login(userLoginDto);
        verifyNoMoreInteractions(authenticationService);

        verify(httpServletResponse).addCookie(createCustomCookie(sessionCookieName,
                sessionId.toString(),
                sessionCookieAge * 60 * 60));

    }

    @Test
    void logout() {
        //given
        UUID sessionId = UUID.randomUUID();

        //when
        ResponseEntity<Void> voidResponseEntity = authenticationController.logout(sessionId, httpServletResponse);

        //then
        assertThat(voidResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(voidResponseEntity.getBody()).isNull();

        verify(authenticationService).logout(sessionId);
        verify(httpServletResponse).addCookie(clearCustomCookie(sessionCookieName));

    }
}