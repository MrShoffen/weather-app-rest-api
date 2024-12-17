package org.mrshoffen.weather.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.model.entity.UserSession;
import org.mrshoffen.weather.mapper.UserMapper;
import org.mrshoffen.weather.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    @Getter
    @Value("${app.session.minutes-before-expire}")
    private int minutesForExpiration;

    private final SessionRepository sessionRepository;

    private final UserMapper userMapper;

    public UserSession createSession(User user) {
        UserSession session = UserSession.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(minutesForExpiration))
                .build();

        return sessionRepository.save(session);
    }

    public UserSession getSessionById(UUID sessionId) {

        //todo add exception
        return sessionRepository.findUserSessionById(sessionId)
                .orElseThrow();
    }

    public void removeSession(UserSession session) {
        sessionRepository.delete(session);
    }

}
