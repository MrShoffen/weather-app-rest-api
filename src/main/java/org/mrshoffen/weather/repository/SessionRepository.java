package org.mrshoffen.weather.repository;

import org.mrshoffen.weather.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<UserSession,String> {

}
