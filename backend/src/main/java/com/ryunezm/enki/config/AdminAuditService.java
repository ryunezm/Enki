package com.ryunezm.enki.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AdminAuditService {
    public void recordAdminLogin(String username, LocalDateTime loginTime) {
        // Here you connect to the database or logs; for now, just a log:
        log.info("Admin user '{}' logged in at {}", username, loginTime);
    }
}
