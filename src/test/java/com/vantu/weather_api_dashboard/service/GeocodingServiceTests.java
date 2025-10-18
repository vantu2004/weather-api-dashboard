package com.vantu.weather_api_dashboard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vantu.weather_api_dashboard.client.GeocodingClient;
import com.vantu.weather_api_dashboard.exception.GeocodingException;
import com.vantu.weather_api_dashboard.response.ApiResponse;
import com.vantu.weather_api_dashboard.response.GeocodingResponse;

@ExtendWith(MockitoExtension.class)
public class GeocodingServiceTests {

    @Mock
    private CacheService cacheService;

    @Mock
    private GeocodingClient geocodingClient;

    @InjectMocks
    private GeocodingService geocodingService;

    private GeocodingResponse sampleGeo;

    @BeforeEach
    void init() {
        sampleGeo = new GeocodingResponse();
        sampleGeo.setLat(10.0);
        sampleGeo.setLon(106.0);
        sampleGeo.setName("Hanoi");
        sampleGeo.setCountry("VN");
    }

    @Test
    @DisplayName("getByCity() should return cached when present")
    void testGetByCityCacheHit() {
        when(cacheService.get(anyString(), eq(GeocodingResponse.class))).thenReturn(Optional.of(sampleGeo));

        ApiResponse<GeocodingResponse> res = geocodingService.getByCity("Ha Noi");

        assertNotNull(res.getData());
        assertEquals("cache", res.getSource());
        verify(cacheService).get("geo:ha_noi", GeocodingResponse.class);
        verifyNoInteractions(geocodingClient);
    }

    @Test
    @DisplayName("getByCity() should call API and cache on miss")
    void testGetByCityCacheMiss() {
        when(cacheService.get(anyString(), eq(GeocodingResponse.class))).thenReturn(Optional.empty());
        when(geocodingClient.direct("Ha Noi")).thenReturn(List.of(sampleGeo));

        ApiResponse<GeocodingResponse> res = geocodingService.getByCity("Ha Noi");

        assertEquals("api", res.getSource());
        verify(cacheService).set(eq("geo:ha_noi"), eq(sampleGeo), eq(Duration.ofHours(24)));
    }

    @Test
    @DisplayName("getByCity() should throw when API returns empty list")
    void testGetByCityNotFound() {
        when(cacheService.get("geo:unknown_city", GeocodingResponse.class)).thenReturn(Optional.empty());
        when(geocodingClient.direct("unknown city")).thenReturn(List.of());

        assertThrows(GeocodingException.class, () -> geocodingService.getByCity("unknown city"));
    }

    @Test
    @DisplayName("getByCoordinates() cache hit")
    void testGetByCoordinatesCacheHit() {
        when(cacheService.get("geo:10.0000_106.0000", GeocodingResponse.class)).thenReturn(Optional.of(sampleGeo));

        ApiResponse<GeocodingResponse> res = geocodingService.getByCoordinates(10.0, 106.0);
        assertEquals("cache", res.getSource());
        verifyNoInteractions(geocodingClient);
    }

    @Test
    @DisplayName("getByCoordinates() API miss then cache set")
    void testGetByCoordinatesCacheMiss() {
        when(cacheService.get("geo:10.0000_106.0000", GeocodingResponse.class)).thenReturn(Optional.empty());
        when(geocodingClient.reverse(10.0, 106.0)).thenReturn(List.of(sampleGeo));

        ApiResponse<GeocodingResponse> res = geocodingService.getByCoordinates(10.0, 106.0);
        assertEquals("api", res.getSource());
        verify(cacheService).set(eq("geo:10.0000_106.0000"), eq(sampleGeo), eq(Duration.ofHours(24)));
    }
}


