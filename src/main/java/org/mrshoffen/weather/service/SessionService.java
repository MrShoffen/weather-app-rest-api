package org.mrshoffen.weather.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.exception.authorization.SessionNotFoundException;
import org.mrshoffen.weather.mapper.SessionMapper;
import org.mrshoffen.weather.model.dto.out.SessionResponseDto;
import org.mrshoffen.weather.model.entity.User;
import org.mrshoffen.weather.model.entity.UserSession;
import org.mrshoffen.weather.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    @Value("${app.session.minutes-before-expire}")
    private int minutesForExpiration;

    private final SessionRepository sessionRepository;

    private final SessionMapper sessionMapper;

    @Transactional
    public SessionResponseDto createSession(User user) {
        UserSession session = UserSession.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(minutesForExpiration))
                .build();

        sessionRepository.save(session);

        return sessionMapper.toResponseDto(session);
    }

    public UserSession getSessionById(UUID sessionId) {
        return sessionRepository.findUserSessionById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session with id " + sessionId + " not found"));
    }

    public void removeSession(UserSession session) {
        sessionRepository.delete(session);
    }

}
