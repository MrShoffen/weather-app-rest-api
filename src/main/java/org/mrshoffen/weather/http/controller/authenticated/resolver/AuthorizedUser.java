package org.mrshoffen.weather.http.controller.authenticated.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // Используем для параметров методов
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizedUser {
}
