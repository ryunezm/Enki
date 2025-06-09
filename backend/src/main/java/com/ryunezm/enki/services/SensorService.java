package com.ryunezm.enki.services;

import com.ryunezm.enki.dto.SensorDTO;
import com.ryunezm.enki.exception.ResourceNotFoundException;
import com.ryunezm.enki.models.Sensor;
import com.ryunezm.enki.repositories.SensorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SensorService {
    private final SensorRepository sensorRepository;

    @Transactional
    public SensorDTO createSensor(SensorDTO sensorDTO) {
        Sensor sensor = new Sensor();
        sensor.setName(sensorDTO.getName());
        sensor.setType(sensorDTO.getType());
        sensor.setUnit(sensorDTO.getUnit());
        sensor.setLocation(sensorDTO.getLocation());

        Sensor savedSensor = sensorRepository.save(sensor);
        sensorDTO.setId(savedSensor.getId());
        return sensorDTO;
    }

    @Transactional(readOnly = true)
    public List<SensorDTO> getAllSensors() {
        return sensorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SensorDTO getSensorById(Long id) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found with ID: " + id));
        return convertToDto(sensor);
    }

    @Transactional
    public SensorDTO updateSensor(Long id, SensorDTO sensorDTO) {
        Sensor existingSensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found with ID: " + id));

        existingSensor.setName(sensorDTO.getName());
        existingSensor.setType(sensorDTO.getType());
        existingSensor.setUnit(sensorDTO.getUnit());
        existingSensor.setLocation(sensorDTO.getLocation());
        Sensor updatedSensor = sensorRepository.save(existingSensor);
        return convertToDto(updatedSensor);
    }

    @Transactional
    public void deleteSensor(Long id) {
        if (!sensorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sensor not found with ID: " + id);
        }
        sensorRepository.deleteById(id);
    }

    private SensorDTO convertToDto(Sensor sensor) {
        return new SensorDTO(
                sensor.getId(),
                sensor.getName(),
                sensor.getType(),
                sensor.getUnit(),
                sensor.getLocation());
    }

    // Helper method for other services to get Sensor entity
    @Transactional(readOnly = true)
    public Sensor getSensorEntityById(Long id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found with ID: " + id));
    }
}
