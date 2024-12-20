package org.mrshoffen.weather.http.controller.authenticated;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.in.UserEditPasswordDto;
import org.mrshoffen.weather.model.dto.in.UserEditProfileDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.mrshoffen.weather.util.CookieUtil.clearCustomCookie;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    @Value("${app.session.authorized-user-attribute-name}")
    private String authorizedUserAttributeName;

    @Value("${app.session.cookie.name}")
    private String sessionCookieName;

    private final UserService userService;


    @ModelAttribute("currentUser")
    public UserResponseDto getCurrentUserFromRequest(HttpServletRequest req) {
        return (UserResponseDto) req.getAttribute(authorizedUserAttributeName);
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getCurrentUser(@ModelAttribute("currentUser") UserResponseDto authorizedUser) {
        return ResponseEntity.ok(authorizedUser);
    }


    @PatchMapping("/profile")
    public ResponseEntity<UserResponseDto> updateUserProfile(@ModelAttribute("currentUser") UserResponseDto authorizedUser,
                                                             @RequestBody UserEditProfileDto userProfileEditDto) {

        UserResponseDto updatedUser = userService.updateUserProfile(authorizedUser.getId(), userProfileEditDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/password")
    public ResponseEntity<UserResponseDto> updateUserPassword(@ModelAttribute("currentUser") UserResponseDto authorizedUser,
                                                              @RequestBody UserEditPasswordDto userEditPasswordDto) {

        UserResponseDto updatedUser = userService.updateUserPassword(authorizedUser.getId(), userEditPasswordDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@ModelAttribute("currentUser") UserResponseDto authorizedUser, HttpServletResponse response) {
        userService.deleteUser(authorizedUser.getId());

        Cookie cookie = clearCustomCookie(sessionCookieName);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

}
