package org.mrshoffen.weather.http.controller.authenticated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mrshoffen.weather.model.dto.in.UserEditPasswordDto;
import org.mrshoffen.weather.model.dto.in.UserEditProfileDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mrshoffen.weather.util.CookieUtil.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    String sessionCookieName = "CUSTOM_SESSION";

    @Mock
    UserProfileService userService;

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

        mockUser = new UserResponseDto(1, "test_user", "test_avatar_url");
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