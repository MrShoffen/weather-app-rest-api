package org.mrshoffen.weather.util;

import jakarta.servlet.http.Cookie;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieUtil {


    public static Cookie createCustomCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        return cookie;
    }
}
