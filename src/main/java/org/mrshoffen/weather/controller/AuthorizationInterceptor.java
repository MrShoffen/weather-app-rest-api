package org.mrshoffen.weather.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.entity.UserSession;
import org.mrshoffen.weather.exception.authorization.SessionExpiredException;
import org.mrshoffen.weather.exception.authorization.UserAlreadyAuthorizedException;
import org.mrshoffen.weather.exception.authorization.UserUnauthorizedException;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.service.SessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mrshoffen.weather.util.CookieUtil.*;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Value("${session.urls-without-authorization}")
    private List<String> allowedUrlsWithoutAuth;


    @Value("${session.cookie-name}")
    private String sessionCookieName;

    private final SessionService sessionService;

    private final UserMapper userMapper;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        Optional<UserSession> userSessionOpt = getCookieByName(request.getCookies(), sessionCookieName)
                .map(cookie -> UUID.fromString(cookie.getValue()))
                .map(sessionService::getSessionById);

        return userSessionOpt
                .map(userSession -> handleAuthorizedUser(userSession, request, requestURI))
                .orElseGet(() -> handleUnauthorizedUser(requestURI));

    }

    private boolean handleAuthorizedUser(UserSession userSession, HttpServletRequest request, String requestURI) {
        if (userSession.isExpired()) {
            sessionService.removeSession(userSession);
            throw new SessionExpiredException("Your session has expired! Please login again.");
        }

        if (isAuthPath(requestURI)) {
            throw new UserAlreadyAuthorizedException("You are already authorized!");
        }

        request.setAttribute("authorizedUser", userMapper.toResponseDto(userSession.getUser()));
        return true;
    }

    private boolean handleUnauthorizedUser(String requestURI) {
        if (!isAuthPath(requestURI)) {
            throw new UserUnauthorizedException("Permission denied! Only for authorized users!");
        }

        return true;
    }

    private boolean isAuthPath(String requestURI) {
        return allowedUrlsWithoutAuth.contains(requestURI);
    }
}
