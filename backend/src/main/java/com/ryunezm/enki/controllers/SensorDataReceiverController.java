package com.ryunezm.enki.controllers;


import com.ryunezm.enki.dto.SensorDataDTO;
import com.ryunezm.enki.services.SensorDataService;
import com.ryunezm.enki.config.websocket.WebSocketNotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/api/sensor-data")
@RestController
public class SensorDataReceiverController {
    private final SensorDataService sensorDataService;
    private final WebSocketNotificationService webSocketNotificationService;

    // This endpoint can be used by the sensor device to POST data
    @PostMapping
    public ResponseEntity<SensorDataDTO> receiveSensorData(@Valid @RequestBody SensorDataDTO sensorDataDTO) {
        SensorDataDTO savedData = sensorDataService.saveSensorData(sensorDataDTO);
        // Broadcast the new data via WebSocket
        webSocketNotificationService.notifySensorDataUpdate(savedData);
        return new ResponseEntity<>(savedData, HttpStatus.CREATED);
    }

    // Optionally, an endpoint to get recent data for a specific sensor via REST (for initial load, etc.)
    @GetMapping("/recent/{sensorId}")
    public ResponseEntity<Iterable<SensorDataDTO>> getRecentSensorData(@PathVariable Long sensorId) {
        return ResponseEntity.ok(sensorDataService.getRecentSensorDataBySensorId(sensorId));
    }
}
