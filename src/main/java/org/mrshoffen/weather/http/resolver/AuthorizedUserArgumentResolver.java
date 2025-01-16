package org.mrshoffen.weather.http.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthorizedUserArgumentResolver  implements HandlerMethodArgumentResolver {

    @Value("${app.session.authorized-user-attribute-name}")
    private String authorizedUserAttributeName;

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
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        return servletRequest.getAttribute(authorizedUserAttributeName);
    }
}
