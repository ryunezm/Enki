package com.ryunezm.enki.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryunezm.enki.dto.SensorDTO;
import com.ryunezm.enki.exception.ResourceNotFoundException;
import com.ryunezm.enki.services.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(SensorController.class)
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SensorService sensorService;

    @Autowired
    private ObjectMapper objectMapper;

    private SensorDTO sensorDTO;
    private List<SensorDTO> sensorList;

    @BeforeEach
    void setUp() {
        sensorDTO = new SensorDTO(1L, "Temperature Sensor", "Temperature", "°C", "Room A");

        SensorDTO sensor2 = new SensorDTO(2L, "Humidity Sensor", "Humidity", "%", "Room B");
        sensorList = Arrays.asList(sensorDTO, sensor2);
    }

    @Test
    @DisplayName("Should create sensor successfully when user has ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateSensorSuccessfully() throws Exception {
        // Given
        SensorDTO inputSensor = new SensorDTO(null, "Temperature Sensor", "Temperature", "°C", "Room A");
        when(sensorService.createSensor(any(SensorDTO.class))).thenReturn(sensorDTO);

        // When & Then
        mockMvc.perform(post("/api/sensors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputSensor)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Temperature Sensor")))
                .andExpect(jsonPath("$.type", is("Temperature")))
                .andExpect(jsonPath("$.unit", is("°C")))
                .andExpect(jsonPath("$.location", is("Room A")));

        verify(sensorService, times(1)).createSensor(any(SensorDTO.class));
    }

    @Test
    @DisplayName("Should return 403 when user without ADMIN role tries to create sensor")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUserTriesToCreateSensor() throws Exception {
        // Given
        SensorDTO inputSensor = new SensorDTO(null, "Temperature Sensor", "Temperature", "°C", "Room A");

        // When & Then
        mockMvc.perform(post("/api/sensors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputSensor)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(sensorService, never()).createSensor(any(SensorDTO.class));
    }

    @Test
    @DisplayName("Should return validation error when creating sensor with blank name")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnValidationErrorWhenNameIsBlank() throws Exception {
        // Given
        SensorDTO invalidSensor = new SensorDTO(null, "", "Temperature", "°C", "Room A");

        // When & Then
        mockMvc.perform(post("/api/sensors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSensor)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(sensorService, never()).createSensor(any(SensorDTO.class));
    }

    @Test
    @DisplayName("Should get all sensors successfully when user has ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllSensorsSuccessfullyWithAdminRole() throws Exception {
        // Given
        when(sensorService.getAllSensors()).thenReturn(sensorList);

        // When & Then
        mockMvc.perform(get("/api/sensors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Temperature Sensor")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Humidity Sensor")));

        verify(sensorService, times(1)).getAllSensors();
    }

    @Test
    @DisplayName("Should get all sensors successfully when user has USER role")
    @WithMockUser(roles = "USER")
    void shouldGetAllSensorsSuccessfullyWithUserRole() throws Exception {
        // Given
        when(sensorService.getAllSensors()).thenReturn(sensorList);

        // When & Then
        mockMvc.perform(get("/api/sensors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(sensorService, times(1)).getAllSensors();
    }

    @Test
    @DisplayName("Should get sensor by id successfully when user has ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldGetSensorByIdSuccessfullyWithAdminRole() throws Exception {
        // Given
        Long sensorId = 1L;
        when(sensorService.getSensorById(sensorId)).thenReturn(sensorDTO);

        // When & Then
        mockMvc.perform(get("/api/sensors/{id}", sensorId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Temperature Sensor")))
                .andExpect(jsonPath("$.type", is("Temperature")))
                .andExpect(jsonPath("$.unit", is("°C")))
                .andExpect(jsonPath("$.location", is("Room A")));

        verify(sensorService, times(1)).getSensorById(sensorId);
    }

    @Test
    @DisplayName("Should get sensor by id successfully when user has USER role")
    @WithMockUser(roles = "USER")
    void shouldGetSensorByIdSuccessfullyWithUserRole() throws Exception {
        // Given
        Long sensorId = 1L;
        when(sensorService.getSensorById(sensorId)).thenReturn(sensorDTO);

        // When & Then
        mockMvc.perform(get("/api/sensors/{id}", sensorId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(sensorService, times(1)).getSensorById(sensorId);
    }

    @Test
    @DisplayName("Should return 404 when sensor not found")
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenSensorNotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;
        when(sensorService.getSensorById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Sensor not found with ID: " + nonExistentId));

        // When & Then
        mockMvc.perform(get("/api/sensors/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(sensorService, times(1)).getSensorById(nonExistentId);
    }

    @Test
    @DisplayName("Should update sensor successfully when user has ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateSensorSuccessfully() throws Exception {
        // Given
        Long sensorId = 1L;
        SensorDTO updatedSensor = new SensorDTO(1L, "Updated Temperature Sensor", "Temperature", "°F", "Room C");
        when(sensorService.updateSensor(eq(sensorId), any(SensorDTO.class))).thenReturn(updatedSensor);

        // When & Then
        mockMvc.perform(put("/api/sensors/{id}", sensorId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSensor)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Temperature Sensor")))
                .andExpect(jsonPath("$.unit", is("°F")))
                .andExpect(jsonPath("$.location", is("Room C")));

        verify(sensorService, times(1)).updateSensor(eq(sensorId), any(SensorDTO.class));
    }

    @Test
    @DisplayName("Should return 403 when user without ADMIN role tries to update sensor")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUserTriesToUpdateSensor() throws Exception {
        // Given
        Long sensorId = 1L;
        SensorDTO updatedSensor = new SensorDTO(1L, "Updated Sensor", "Temperature", "°C", "Room A");

        // When & Then
        mockMvc.perform(put("/api/sensors/{id}", sensorId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSensor)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(sensorService, never()).updateSensor(any(Long.class), any(SensorDTO.class));
    }

    @Test
    @DisplayName("Should delete sensor successfully when user has ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteSensorSuccessfully() throws Exception {
        // Given
        Long sensorId = 1L;
        doNothing().when(sensorService).deleteSensor(sensorId);

        // When & Then
        mockMvc.perform(delete("/api/sensors/{id}", sensorId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(sensorService, times(1)).deleteSensor(sensorId);
    }

    @Test
    @DisplayName("Should return 403 when user without ADMIN role tries to delete sensor")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUserTriesToDeleteSensor() throws Exception {
        // Given
        Long sensorId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/sensors/{id}", sensorId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(sensorService, never()).deleteSensor(any(Long.class));
    }

    @Test
    @DisplayName("Should return 404 when trying to delete non-existent sensor")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenDeletingNonExistentSensor() throws Exception {
        // Given
        Long nonExistentId = 999L;
        doThrow(new ResourceNotFoundException("Sensor not found with ID: " + nonExistentId))
                .when(sensorService).deleteSensor(nonExistentId);

        // When & Then
        mockMvc.perform(delete("/api/sensors/{id}", nonExistentId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(sensorService, times(1)).deleteSensor(nonExistentId);
    }

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void shouldReturn401WhenUserNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/sensors"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(sensorService, never()).getAllSensors();
    }
}