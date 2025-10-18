package com.vantu.weather_api_dashboard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.vantu.weather_api_dashboard.client.OpenWeatherClient;
import com.vantu.weather_api_dashboard.response.HealthResponse;

@ExtendWith(MockitoExtension.class)
public class HealthServiceTests {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOps;

    @Mock
    private GeolocationService geolocationService;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private OpenWeatherClient openWeatherClient;

    @InjectMocks
    private HealthService healthService;

    @Test
    @DisplayName("checkSystemHealth() aggregates component statuses and timings")
    void testCheckSystemHealth() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        // simulate Redis ok
        // set/get interactions checked by behavior
        doNothing().when(valueOps).set(eq("health:ping"), any());
        when(valueOps.get("health:ping")).thenReturn("pong");

        // simulate OpenWeather ok
        doNothing().when(openWeatherClient).healthCheck();

        // simulate IP2Location ok
        doNothing().when(geolocationService).testDatabase();

        // simulate Geocoding ok
        doNothing().when(geocodingService).selfCheck();

        HealthResponse res = healthService.checkSystemHealth();
        assertNotNull(res);
        assertEquals("UP", res.getStatus());
        assertEquals("UP", res.getRedisStatus());
        assertEquals("UP", res.getOpenWeatherStatus());
        assertEquals("UP", res.getIp2LocationStatus());
        assertEquals("UP", res.getGeocodingStatus());
        assertTrue(res.getTotalCheckMs() >= 0);
    }
}


