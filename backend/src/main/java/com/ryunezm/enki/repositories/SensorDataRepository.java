package com.ryunezm.enki.repositories;

import com.ryunezm.enki.models.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findBySensorIdOrderByTimestampDesc(Long sensorId);

    List<SensorData> findTop50BySensorIdOrderByTimestampDesc(Long sensorId); // Example for fetching recent data
}
