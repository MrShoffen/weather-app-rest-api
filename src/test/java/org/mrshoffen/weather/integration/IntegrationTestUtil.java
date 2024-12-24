package org.mrshoffen.weather.integration;


import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class IntegrationTestUtil {

    public static MockHttpServletRequestBuilder registerUser(String username, String password, String avatarUrl) {
        return MockMvcRequestBuilders.post("/weather/api/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "username" : "%s", "password" : "%s", "avatarUrl" : "%s"}
                        """.formatted(username, password, avatarUrl));
    }

    public static MockHttpServletRequestBuilder loginUser(String username, String password) {
        return MockMvcRequestBuilders.post("/weather/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "username" : "%s", "password" : "%s"}
                        """.formatted(username, password));
    }
}
