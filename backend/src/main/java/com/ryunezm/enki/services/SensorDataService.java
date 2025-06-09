package com.ryunezm.enki.services;

import com.ryunezm.enki.dto.SensorDataDTO;
import com.ryunezm.enki.models.Sensor;
import com.ryunezm.enki.models.SensorData;
import com.ryunezm.enki.repositories.SensorDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SensorDataService {
    private final SensorDataRepository sensorDataRepository;
    private final SensorService sensorService;

    @Transactional
    public SensorDataDTO saveSensorData(SensorDataDTO sensorDataDTO) {
        Sensor sensor = sensorService.getSensorEntityById(sensorDataDTO.getSensorId());

        SensorData sensorData = new SensorData();
        sensorData.setSensor(sensor);
        sensorData.setValue(sensorDataDTO.getValue());
        sensorData.setTimestamp(LocalDateTime.now()); // Set timestamp on backend
        SensorData savedSensorData = sensorDataRepository.save(sensorData);

        sensorDataDTO.setId(savedSensorData.getId());
        sensorDataDTO.setTimestamp(savedSensorData.getTimestamp());
        return sensorDataDTO;
    }

    @Transactional(readOnly = true)
    public List<SensorDataDTO> getSensorDataBySensorId(Long sensorId) {
        // Ensure the sensor exists before fetching its data
        sensorService
                .getSensorEntityById(sensorId);

        return sensorDataRepository
                .findBySensorIdOrderByTimestampDesc(sensorId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SensorDataDTO> getRecentSensorDataBySensorId(Long sensorId) {
        // Ensure the sensor exists before fetching its data
        sensorService
                .getSensorEntityById(sensorId);

        return sensorDataRepository
                .findTop50BySensorIdOrderByTimestampDesc(sensorId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private SensorDataDTO convertToDto(SensorData sensorData) {
        return new SensorDataDTO(
                sensorData.getId(),
                sensorData.getSensor().getId(),
                sensorData.getValue(),
                sensorData.getTimestamp());
    }
}


