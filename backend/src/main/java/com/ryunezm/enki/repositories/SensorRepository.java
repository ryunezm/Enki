package com.ryunezm.enki.repositories;

import com.ryunezm.enki.models.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findByName(String name);
}
