package org.mrshoffen.weather.repository;

import org.mrshoffen.weather.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    List<Location> findAllByUserIdOrderByIdDesc(Integer userId);

    Optional<Location> findByIdAndUserId(Integer id, Integer userId);


}
