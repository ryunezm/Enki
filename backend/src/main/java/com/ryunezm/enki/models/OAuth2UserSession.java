package com.ryunezm.enki.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Entity
@NoArgsConstructor
public class OAuth2UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String oauth2Id; // The 'sub' (subject) claim from the OAuth2 JWT

    @Column(nullable = false)
    private LocalDateTime loginTimestamp;
}
