package com.ryunezm.enki.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SensorDataDTO {
    private Long id;

    @NotNull(message = "The sensor ID cannot be null")
    private Long sensorId;

    @NotNull(message = "The value cannot be null")
    private Double value;

    private LocalDateTime timestamp; // Will be set by the backend if not provided
}
