package com.ryunezm.enki.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SensorDTO {
    private Long id;

    @NotBlank(message = "The sensor name cannot be empty")
    private String name;

    @NotBlank(message = "The sensor type cannot be empty")
    private String type;

    @NotBlank(message = "The sensor unit cannot be empty")
    private String unit;

    private String location;
}
