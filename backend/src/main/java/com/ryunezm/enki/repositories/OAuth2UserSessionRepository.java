package com.ryunezm.enki.repositories;

import com.ryunezm.enki.models.OAuth2UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuth2UserSessionRepository extends JpaRepository<OAuth2UserSession, Long> {
    Optional<OAuth2UserSession> findTopByOauth2IdOrderByLoginTimestampDesc(String oauth2Id);
}
