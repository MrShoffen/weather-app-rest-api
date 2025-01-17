package org.mrshoffen.weather.integration;

import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mrshoffen.weather.model.entity.UserSession;
import org.mrshoffen.weather.repository.SessionRepository;
import org.mrshoffen.weather.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mrshoffen.weather.integration.IntegrationTestUtil.loginUser;
import static org.mrshoffen.weather.integration.IntegrationTestUtil.registerUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerIT {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    SessionRepository sessionRepository;

    @Value("${app.session.cookie.name}")
    private String sessionCookieName;

    Cookie cookie;


    void registerAndLoginToAccount(String username, String password, String avatarUrl) throws Exception {
        mockMvc.perform(
                registerUser(username, password, avatarUrl)
        );
        cookie = mockMvc.perform(
                        loginUser(username, password)
                )
                .andReturn()
                .getResponse()
                .getCookie(sessionCookieName);
    }

    @AfterEach
    void resetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE weather.users ALTER COLUMN id RESTART WITH 1;");
    }

    @Test
    void anyMethod_UserUnauthorized_ReturnProblemDetail() throws Exception {
        //when
        mockMvc.perform(get("/weather/api/user"))
                //then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"UserUnauthorizedException","status":401,"detail":"Permission denied! Only for authorized users!"}
                                """)
                );
    }

    @Test
    void anyMethod_SessionExpired_ReturnProblemDetail() throws Exception {
        //given
        registerAndLoginToAccount("username", "password", "url");

        UserSession userSession = sessionRepository
                .findUserSessionById(UUID.fromString(cookie.getValue()))
                .orElseThrow(() -> new RuntimeException("Session not found"));

        //imitate that session is expired
        userSession.setExpiresAt(LocalDateTime.now().minusMinutes(10));
        sessionRepository.save(userSession);

        //when
        mockMvc.perform(get("/weather/api/user").cookie(cookie))
                //then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"SessionExpiredException","status":401,"detail":"Your session has expired! Please login again."}
                                """),
                        cookie().value(sessionCookieName, Matchers.nullValue())

                );
    }

    @Test
    void anyMethod_SessionCookieSpoofing_AccessDeniedReturnProblemDetail() throws Exception {
        //given
        Cookie spoofCookie = CookieUtil.createCustomCookie(sessionCookieName, UUID.randomUUID().toString(), 200);

        //when
        mockMvc.perform(get("/weather/api/user").cookie(spoofCookie))
                //then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"SessionNotFoundException","status":404}
                                """)
                );
    }

    @Test
    void getCurrentUser_UserAuthorized_ReturnResponseEntity() throws Exception {
        //given
        registerAndLoginToAccount("username", "password", "url");

        //when
        mockMvc.perform(get("/weather/api/user").cookie(cookie))
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id":1,"username":"username","avatarUrl":"url"}
                                """)
                );
    }

    @Test
    void updateCurrentUser_EditDtoIsCorrect_ReturnResponseEntity() throws Exception {
        //given
        registerAndLoginToAccount("username", "password", "url");

        var updateProfileRequest = patch("/weather/api/user/profile")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"newUsername": "new_username", "newAvatar": null}
                        """);

        //when
        mockMvc.perform(updateProfileRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id":1,"username":"new_username","avatarUrl":null}
                                """)
                );
    }

    @Test
    void updateCurrentUser_NewUsernameAlreadyOccupied_ReturnProblemDetail() throws Exception {
        //given
        registerAndLoginToAccount("username", "password", "url");

        mockMvc.perform(
                registerUser("username_two", "password", "")
        );

        var updateProfileRequest = patch("/weather/api/user/profile")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"newUsername": "username_two", "newAvatar": null}
                        """);

        //when
        mockMvc.perform(updateProfileRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isConflict(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"UserAlreadyExistsException","status":409,"detail":"User with username 'username_two' already exists!"}
                                """)
                );
    }

    @Test
    void updateCurrentUser_IncorrectNewUsername_ReturnProblemDetail() throws Exception {
        //given
        registerAndLoginToAccount("username", "password", "url");

        var updateProfileRequest = patch("/weather/api/user/profile")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"newUsername": "222", "newAvatar": null}
                        """);
        //when
        mockMvc.perform(updateProfileRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"MethodArgumentNotValidException","status":400}
                                """)
                );
    }


    @Test
    void updateCurrentUserPassword_EditDtoIsCorrect_ReturnResponseEntity() throws Exception {
        //given
        registerAndLoginToAccount("username", "password", "url");

        var updateProfileRequest = patch("/weather/api/user/password")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"oldPassword": "password", "newPassword": "new_password"}
                        """);

        //when
        mockMvc.perform(updateProfileRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id":1,"username":"username","avatarUrl":"url"}
                                """)
                );
    }

    @Test
    void updateCurrentUserPassword_IncorrectOldPassword_ReturnProblemDetail() throws Exception {
        //given
        registerAndLoginToAccount("username", "password", "url");

        var updateProfileRequest = patch("/weather/api/user/password")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"oldPassword": "wrong_pass", "newPassword": "new_password"}
                        """);
        //when
        mockMvc.perform(updateProfileRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"IncorrectPasswordException","status":401,"detail":"Incorrect password!"}
                                """)
                );
    }

    @Test
    void updateCurrentUserPassword_IncorrectNewPassword_ReturnProblemDetail() throws Exception {
        //given
        registerAndLoginToAccount("username", "password", "url");

        var updateProfileRequest = patch("/weather/api/user/password")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"oldPassword": "password", "newPassword": "wrong pass word"}
                        """);
        //when
        mockMvc.perform(updateProfileRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"MethodArgumentNotValidException","status":400,"detail":"Incorrect symbols in password!"}
                                """)
                );
    }

    @Test
    void deleteUser_UserAuthorized_ReturnNoContentAndCleanCookie() throws Exception {
        //given
        registerAndLoginToAccount("username", "password", "url");

        //when
        mockMvc.perform(delete("/weather/api/user").cookie(cookie))
                //then
                .andDo(print())
                .andExpectAll(
                        status().isNoContent(),
                        cookie().value(sessionCookieName, Matchers.nullValue())
                );
    }

}
