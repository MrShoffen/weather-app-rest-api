package org.mrshoffen.weather.http.controller.authenticated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mrshoffen.weather.model.dto.in.UserEditPasswordDto;
import org.mrshoffen.weather.model.dto.in.UserEditProfileDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.UserService;
import org.mrshoffen.weather.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mrshoffen.weather.util.CookieUtil.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    String sessionCookieName = "CUSTOM_SESSION";

    String authorizedUserAttributeName = "authorizedUser";

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletResponse httpServletResponse;

    UserResponseDto mockUser;


    @BeforeEach
    void setUp() {
        setField(userController, "sessionCookieName", sessionCookieName);
        setField(userController, "authorizedUserAttributeName", authorizedUserAttributeName);

        mockUser = new UserResponseDto(1, "test_user", "test_avatar_url");
    }

    @Test
    void getCurrentUserFromRequest_UserIsPresentInRequest() {
        doReturn(mockUser)
                .when(httpServletRequest)
                .getAttribute(authorizedUserAttributeName);

        assertThat(userController.getCurrentUserFromRequest(httpServletRequest))
                .isEqualTo(mockUser);

        verify(httpServletRequest).getAttribute(authorizedUserAttributeName);
        verifyNoMoreInteractions(httpServletRequest);
    }


    @Test
    void getCurrentUser_UserAttributeIsPresent_ReturnsResponseEntity() {
        ResponseEntity<UserResponseDto> currentUser = userController
                .getCurrentUser(mockUser);

        assertThat(currentUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(currentUser.getBody()).isEqualTo(mockUser);
    }

    @Test
    void updateUserProfileTest() {
        //given
        UserEditProfileDto userEditProfileDto = new UserEditProfileDto("test2", "test_url_2");
        UserResponseDto updatedMockUser = new UserResponseDto(1, "test2", "test_ur_2");

        doReturn(updatedMockUser)
                .when(userService)
                .updateUserProfile(mockUser.getId(), userEditProfileDto);

        //when
        ResponseEntity<UserResponseDto> updatedResponseEntity = userController.updateUserProfile(mockUser, userEditProfileDto);

        //then
        assertThat(updatedResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedResponseEntity.getBody()).isEqualTo(updatedMockUser);

        verify(userService).updateUserProfile(mockUser.getId(), userEditProfileDto);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void updateUserPasswordTest() {
        //given
        UserEditPasswordDto userEditPasswordDto = new UserEditPasswordDto("old_pass", "new_pass");

        doReturn(mockUser)
                .when(userService)
                .updateUserPassword(mockUser.getId(), userEditPasswordDto);

        //when
        ResponseEntity<UserResponseDto> updatedResponseEntity = userController.updateUserPassword(mockUser, userEditPasswordDto);

        //then
        assertThat(updatedResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedResponseEntity.getBody()).isEqualTo(mockUser);

        verify(userService).updateUserPassword(mockUser.getId(), userEditPasswordDto);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void deleteUserTest() {
        ResponseEntity<Void> voidResponseEntity = userController.deleteUser(mockUser, httpServletResponse);

        assertThat(voidResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(voidResponseEntity.getBody()).isNull();

        verify(userService).deleteUser(mockUser.getId());
        verify(httpServletResponse).addCookie(clearCustomCookie(sessionCookieName));
    }
}