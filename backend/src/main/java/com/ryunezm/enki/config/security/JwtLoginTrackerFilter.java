package com.ryunezm.enki.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Component
public class JwtLoginTrackerFilter extends OncePerRequestFilter {

    private final OAuth2UserSessionService sessionService;
    private final Set<String> seenTokenIds = ConcurrentHashMap.newKeySet();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Jwt jwt) {
            String subject = jwt.getSubject();
            String tokenId = jwt.getId(); // jti claim (null if not present)

            // Consider the JWT seen by ID or by sub (fallback)
            String seenKey = tokenId != null ? tokenId : "sub:" + subject;

            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
                    && seenTokenIds.add(seenKey)) {
                sessionService.recordOAuth2UserSession(subject);
                System.out.println("OAuth2 login recorded: " + subject);
            }
        }

        filterChain.doFilter(request, response);

    }
}
