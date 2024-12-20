package org.mrshoffen.weather.repository;

import org.mrshoffen.weather.model.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<UserSession, String> {


    @Query("""
             SELECT s FROM UserSession s
             JOIN FETCH s.user u
             WHERE s.id = :sessionId
            """)
    Optional<UserSession> findUserSessionById(@Param("sessionId") UUID sessionId);

    @Modifying
    @Query("""
             DELETE FROM UserSession s
             WHERE s.user.id = :userId
            """)
    void deleteAllByUserId(@Param("userId") Integer userId);

}
