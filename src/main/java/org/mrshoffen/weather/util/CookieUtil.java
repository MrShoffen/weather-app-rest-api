package org.mrshoffen.weather.util;

import jakarta.servlet.http.Cookie;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Optional;

@UtilityClass
public class CookieUtil {

    public static Cookie createCustomCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        return cookie;
    }

    public static Cookie clearCustomCookie(String name) {
        return createCustomCookie(name, null, 0);
    }

    public static Optional<Cookie> getCookieByName(Cookie[] cookies, String name) {
        Optional<Cookie> cookie = Optional.empty();

        if (cookies != null && name != null) {
            cookie = Arrays.stream(cookies)
                    .filter(c -> name.equals(c.getName()))
                    .findFirst();
        }

        return cookie;
    }
}
