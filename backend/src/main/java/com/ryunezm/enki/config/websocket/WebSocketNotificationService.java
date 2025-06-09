package com.ryunezm.enki.config.websocket;

import com.ryunezm.enki.dto.SensorDataDTO;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class WebSocketNotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void notifySensorDataUpdate(SensorDataDTO sensorData) {
        // Send to a generic topic for all new sensor data
        simpMessagingTemplate.convertAndSend("/topic/sensor-data", sensorData);
        // Send to a specific topic for a particular sensor (e.g., for dashboards showing only one sensor)
        simpMessagingTemplate.convertAndSend("/topic/sensor-data/" + sensorData.getSensorId(), sensorData);
    }
}
