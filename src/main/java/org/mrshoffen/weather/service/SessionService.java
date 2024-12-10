package org.mrshoffen.weather.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.entity.User;
import org.mrshoffen.weather.entity.UserSession;
import org.mrshoffen.weather.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    @Getter
    @Value("${session.expires-after}")
    private int minutesForExpiration;

    private final SessionRepository sessionRepository;

    public UserSession createSession(User user){
        UserSession session = UserSession.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(minutesForExpiration))
                .build();

        return sessionRepository.save(session);
    }
}
