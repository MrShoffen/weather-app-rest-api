package org.mrshoffen.weather.http.controller.authenticated;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    @Value("${app.session.authorized-user-attribute-name}")
    private String authorizedUserAttributeName;

    private final UserService userService;


    @ModelAttribute("currentUser")
    public UserResponseDto getCurrentUserFromRequest(HttpServletRequest req) {
        return (UserResponseDto) req.getAttribute(authorizedUserAttributeName);
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getCurrentUser(@ModelAttribute("currentUser") UserResponseDto authorizedUser) {
        return ResponseEntity.ok(authorizedUser);
    }

    @PatchMapping
    public ResponseEntity<UserResponseDto> updateCurrentUser(@ModelAttribute("currentUser") UserResponseDto authorizedUser) {

        return null;
    }

}
