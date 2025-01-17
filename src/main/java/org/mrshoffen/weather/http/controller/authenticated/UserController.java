package org.mrshoffen.weather.http.controller.authenticated;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.http.controller.authenticated.resolver.AuthorizedUser;
import org.mrshoffen.weather.model.dto.in.UserEditPasswordDto;
import org.mrshoffen.weather.model.dto.in.UserEditProfileDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.UserProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.mrshoffen.weather.util.CookieUtil.clearCustomCookie;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/user")
public class UserController {

    @Value("${app.session.cookie.name}")
    private String sessionCookieName;

    private final UserProfileService userService;


    @GetMapping
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthorizedUser UserResponseDto authorizedUser) {
        return ResponseEntity.ok(authorizedUser);
    }


    @PatchMapping("/profile")
    public ResponseEntity<UserResponseDto> updateUserProfile(@AuthorizedUser UserResponseDto authorizedUser,
                                                             @Valid @RequestBody UserEditProfileDto userProfileEditDto) {

        UserResponseDto updatedUser = userService.updateUserProfile(authorizedUser.getId(), userProfileEditDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/password")
    public ResponseEntity<UserResponseDto> updateUserPassword(@AuthorizedUser UserResponseDto authorizedUser,
                                                              @Valid @RequestBody UserEditPasswordDto userEditPasswordDto) {

        UserResponseDto updatedUser = userService.updateUserPassword(authorizedUser.getId(), userEditPasswordDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthorizedUser UserResponseDto authorizedUser, HttpServletResponse response) {
        userService.deleteUser(authorizedUser.getId());

        Cookie cookie = clearCustomCookie(sessionCookieName);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

}
