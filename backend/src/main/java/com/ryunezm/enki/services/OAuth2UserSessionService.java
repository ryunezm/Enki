package com.ryunezm.enki.services;

import com.ryunezm.enki.models.OAuth2UserSession;
import com.ryunezm.enki.repositories.OAuth2UserSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class OAuth2UserSessionService {
    private final OAuth2UserSessionRepository oAuth2UserSessionRepository;

    @Transactional
    public void recordOAuth2UserSession(String oauth2Id) {
        OAuth2UserSession session = new OAuth2UserSession();
        session.setOauth2Id(oauth2Id);
        session.setLoginTimestamp(LocalDateTime.now());
        oAuth2UserSessionRepository.save(session);
    }

}
