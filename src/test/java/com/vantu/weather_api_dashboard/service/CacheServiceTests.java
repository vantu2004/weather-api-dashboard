package com.vantu.weather_api_dashboard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
public class CacheServiceTests {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOps;

    @InjectMocks
    private CacheService cacheService;

    @Test
    @DisplayName("set() should delegate to Redis with TTL")
    void testSet() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        cacheService.set("k", "v", Duration.ofSeconds(10));

        verify(valueOps).set(eq("k"), eq("v"), eq(Duration.ofSeconds(10)));
    }

    @Test
    @DisplayName("get() should return present Optional when type matches")
    void testGetHit() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("k1")).thenReturn("hello");

        Optional<String> got = cacheService.get("k1", String.class);
        assertTrue(got.isPresent());
        assertEquals("hello", got.get());
    }

    @Test
    @DisplayName("get() should return empty when stored type mismatches")
    void testGetTypeMismatch() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("k2")).thenReturn(123);

        Optional<String> got = cacheService.get("k2", String.class);
        assertTrue(got.isEmpty());
    }
}


