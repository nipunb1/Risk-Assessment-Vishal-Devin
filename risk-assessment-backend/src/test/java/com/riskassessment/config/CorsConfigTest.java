package com.riskassessment.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    void corsConfigurationSource_ShouldReturnValidConfiguration() {
        CorsConfig corsConfig = new CorsConfig();
        
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        
        assertNotNull(source);
        assertTrue(source instanceof UrlBasedCorsConfigurationSource);
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/risks");
        
        CorsConfiguration configuration = source.getCorsConfiguration(request);
        
        assertNotNull(configuration);
        assertTrue(configuration.getAllowedOriginPatterns().contains("*"));
        assertTrue(configuration.getAllowedMethods().contains("GET"));
        assertTrue(configuration.getAllowedMethods().contains("POST"));
        assertTrue(configuration.getAllowedMethods().contains("PUT"));
        assertTrue(configuration.getAllowedMethods().contains("DELETE"));
        assertTrue(configuration.getAllowedMethods().contains("OPTIONS"));
        assertTrue(configuration.getAllowedHeaders().contains("*"));
        assertTrue(configuration.getAllowCredentials());
    }
}
