package org.mrshoffen.weather.integration;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.mrshoffen.weather.integration.IntegrationTestUtil.loginUser;
import static org.mrshoffen.weather.integration.IntegrationTestUtil.registerUser;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class AuthenticationIT {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MockMvc mockMvc;

    @Value("${app.session.cookie.name}")
    private String sessionCookieName;

    @AfterEach
    void resetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE weather.users ALTER COLUMN id RESTART WITH 1;");
    }

    @Test
    void register_NewUniqueUser_ReturnResponseEntity() throws Exception {
        //given
        var registerRequest = registerUser("username", "password", "avatarUrl.png");

        //when
        mockMvc.perform(registerRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id":1,"username":"username","avatarUrl":"avatarUrl.png"}
                                """)
                );
    }

    @Test
    void register_UserAlreadyExists_ReturnsProblemDetail() throws Exception {
        //given
        MockHttpServletRequestBuilder reqBuilder = registerUser("username", "password", "avatarUrl.png");
        mockMvc.perform(reqBuilder);

        //when
        mockMvc.perform(reqBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isConflict(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"UserAlreadyExistsException","status":409,"detail":"User with username 'username' already exists!"}
                                """)
                );
    }

    @Test
    void register_IncorrectCredentials_ReturnsProblemDetail() throws Exception {
        //given
        MockHttpServletRequestBuilder reqBuilder = registerUser("us", "pass word", "avatarUrl.png");
        mockMvc.perform(reqBuilder);

        //when
        mockMvc.perform(reqBuilder)
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
    void login_UserExists_ReturnsResponseEntity() throws Exception {
        //given
        mockMvc.perform(
                registerUser("username", "password", "avatarUrl.png")
        );

        var loginRequest = loginUser("username", "password");

        //when
        mockMvc.perform(loginRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id":1,"username":"username","avatarUrl":"avatarUrl.png"}
                                """),
                        cookie().exists(sessionCookieName)
                );
    }

    @Test
    void login_UserDoesntExist_ReturnsProblemDetail() throws Exception {
        //given
        var loginRequest = loginUser("username", "password");


        //when
        mockMvc.perform(loginRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"UserNotFoundException","status":404,"detail":"User with username 'username' not found!"}
                                """),
                        cookie().doesNotExist(sessionCookieName)
                );
    }

    @Test
    void login_IncorrectPassword_ReturnsProblemDetail() throws Exception {
        //given
        MockHttpServletRequestBuilder reqBuilder = registerUser("username", "password", "avatarUrl.png");
        mockMvc.perform(reqBuilder);

        var loginRequest = loginUser("username", "wrong_password");

        //when
        mockMvc.perform(loginRequest)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"IncorrectPasswordException","status":401,"detail":"Incorrect password!"}
                                """),
                        cookie().doesNotExist(sessionCookieName)
                );
    }


    @Test
    void login_IncorrectCredentials_ReturnsProblemDetail() throws Exception {
        //given
        MockHttpServletRequestBuilder reqBuilder = loginUser("us name", "pass word");

        //when
        mockMvc.perform(reqBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"MethodArgumentNotValidException","status":400}
                                """),
                        cookie().doesNotExist(sessionCookieName)
                );
    }

    @Test
    void login_AlreadyAuthorized_ReturnsProblemDetail() throws Exception {
        //given
        var registerRequest = registerUser("username", "password", "avatarUrl.png");
        mockMvc.perform(registerRequest);

        var loginRequest = loginUser("username", "password");
        var sessionCookie = mockMvc.perform(loginRequest)
                .andReturn()
                .getResponse()
                .getCookie(sessionCookieName);

        var secondLogin = loginUser("another_username", "another_pass")
                .cookie(sessionCookie);
        //when
        mockMvc.perform(secondLogin)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {"title":"UserAlreadyAuthorizedException","status":403,"detail":"You are already authorized!"}
                                """)
                );
    }

    @Test
    void logout_UserAuthorized_ReturnsSuccessNoContent() throws Exception {
        //given
        var registerRequest = registerUser("username", "password", "avatarUrl.png");
        mockMvc.perform(registerRequest);

        var loginRequest = loginUser("username", "password");
        var sessionCookie = mockMvc.perform(loginRequest)
                .andReturn()
                .getResponse()
                .getCookie(sessionCookieName);

        var logout = MockMvcRequestBuilders.post("/weather/api/auth/logout")
                .cookie(sessionCookie);

        //when
        mockMvc.perform(logout)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isNoContent(),
                        cookie().value(sessionCookieName, Matchers.nullValue())
                );
    }

    @Test
    void logout_UserNotAuthorized_ReturnsProblemDetail() throws Exception {
        //given
        var logout = MockMvcRequestBuilders.post("/weather/api/auth/logout");

        //when
        mockMvc.perform(logout)
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
}