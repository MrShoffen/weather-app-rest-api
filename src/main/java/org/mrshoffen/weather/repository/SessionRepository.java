package org.mrshoffen.weather.repository;

import org.mrshoffen.weather.entity.User;
import org.mrshoffen.weather.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<UserSession, String> {


    @Query("""
            SELECT u FROM UserSession s
            JOIN s.user u
            WHERE s.id = :sessionId
                        """)
    Optional<User> findUserBySessionId(@Param("sessionId") UUID sessionId);

    @Query("""
            SELECT s FROM UserSession s
            JOIN FETCH s.user u
            WHERE s.id = :sessionId
                        """)
    Optional<UserSession> findUserSessionById(@Param("sessionId") UUID sessionId);
}
