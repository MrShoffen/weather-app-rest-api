package org.mrshoffen.weather.http.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.entity.UserSession;
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

    @Value("${app.urls.without-auth-allowed}")
    private List<String> allowedUrlsWithoutAuth;

    @Value("${app.urls.always-allowed}")
    private List<String> alwaysAllowedUrls;

    @Value("${app.session.cookie.name}")
    private String sessionCookieName;

    @Value("${app.session.authorized-user-attribute-name}")
    private String authorizedUserAttributeName;

    private final SessionService sessionService;

    private final UserMapper userMapper;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isRequestAlwaysAllowed(request)) {
            return true;
        }


        Optional<UserSession> userSessionOpt = getUserSessionFromCookie(request);

        return isRequestAllowed(request, response, userSessionOpt);
    }

    private boolean isRequestAlwaysAllowed(HttpServletRequest request) {
        for (String url : alwaysAllowedUrls) {
            if (request.getRequestURI().startsWith(url)) {
                return true;
            }
        }

        return request.getMethod().equals("OPTIONS");
    }

    private Optional<UserSession> getUserSessionFromCookie(HttpServletRequest request) {
        return getCookieByName(request.getCookies(), sessionCookieName)
                .map(cookie -> UUID.fromString(cookie.getValue()))
                .map(sessionService::getSessionById);
    }

    private Boolean isRequestAllowed(HttpServletRequest request, HttpServletResponse response, Optional<UserSession> userSessionOpt) {
        return userSessionOpt
                .map(userSession -> handleAuthorizedUser(userSession, request, response))
                .orElseGet(() -> handleUnauthorizedUser(request));
    }

    private boolean handleAuthorizedUser(UserSession userSession, HttpServletRequest request, HttpServletResponse response) {
        if (userSession.isExpired()) {
            Cookie cookie = clearCustomCookie(sessionCookieName);
            response.addCookie(cookie);

            sessionService.removeSession(userSession);
            throw new SessionExpiredException("Your session has expired! Please login again.");
        }

        if (isPathAllowedForAuthUsers(request.getRequestURI())) {
            throw new UserAlreadyAuthorizedException("You are already authorized!");
        }

        //todo maybe set full user entity? or just an id? or session?
        request.setAttribute(authorizedUserAttributeName, userMapper.toResponseDto(userSession.getUser()));
        return true;
    }

    private boolean handleUnauthorizedUser(HttpServletRequest request) {
        if (!isPathAllowedForAuthUsers(request.getRequestURI())) {
            throw new UserUnauthorizedException("Permission denied! Only for authorized users!");
        }

        return true;
    }

    private boolean isPathAllowedForAuthUsers(String requestURI) {
        return allowedUrlsWithoutAuth.contains(requestURI);
    }
}
