package org.mrshoffen.weather.http.controller.authenticated.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.exception.authorization.SessionNotFoundException;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.SessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

import static org.mrshoffen.weather.util.CookieUtil.getCookieByName;

@Component
@RequiredArgsConstructor
public class AuthorizedUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Value("${app.session.cookie.name}")
    private String sessionCookieName;

    private final SessionService sessionService;

    private final UserMapper userMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserResponseDto.class)
                && parameter.hasParameterAnnotation(AuthorizedUser.class); // проверяем наличие кастомной аннотации
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        UserResponseDto authorizedUser = getCookieByName(request.getCookies(), sessionCookieName)
                .map(cookie -> UUID.fromString(cookie.getValue()))
                .map(sessionService::getSessionById)
                .map(session -> userMapper.toResponseDto(session.getUser()))
                .orElseThrow(() -> new SessionNotFoundException("Session not found!"));

        return authorizedUser;
    }
}
