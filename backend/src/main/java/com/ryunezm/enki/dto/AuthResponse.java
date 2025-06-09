package com.ryunezm.enki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthResponse {
    private String token;
    private String roles; // Optional, for frontend convenience
}
