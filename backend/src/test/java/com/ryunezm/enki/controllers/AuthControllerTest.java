package com.ryunezm.enki.controllers;

import com.ryunezm.enki.config.security.JwtService;
import com.ryunezm.enki.dto.AuthRequest;
import com.ryunezm.enki.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private final String expectedToken = "admin-jwt-token";
    private final String expectedRole = "ROLE_ADMIN";
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private AuthController authController;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest("admin", "adminPass");
    }

    @Test
    void authenticateUser_withValidAdminCredentials_returnsTokenAndRole() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of(() -> expectedRole);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn((List) authorities);
        when(jwtService.generateToken(userDetails)).thenReturn(expectedToken);

        // Act
        AuthResponse response = authController.authenticateUser(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        assertEquals(expectedRole, response.getRoles());

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void authenticateUser_withInvalidCredentials_throwsBadCredentialsException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authController.authenticateUser(authRequest);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService);
    }

    @Test
    void authenticateUser_verifiesCorrectAuthenticationToken() {
        // Arrange
        Collection<? extends GrantedAuthority> authorities = List.of(() -> expectedRole);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn((List) authorities);
        when(jwtService.generateToken(userDetails)).thenReturn(expectedToken);

        // Act
        authController.authenticateUser(authRequest);

        // Assert - Verify that the correct username and password are used
        verify(authenticationManager).authenticate(
                argThat(token ->
                        token instanceof UsernamePasswordAuthenticationToken &&
                                "admin".equals(token.getPrincipal()) &&
                                "adminPass".equals(token.getCredentials())
                )
        );
    }

}
