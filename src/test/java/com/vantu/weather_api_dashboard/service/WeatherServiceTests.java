package com.vantu.weather_api_dashboard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vantu.weather_api_dashboard.client.OpenWeatherClient;
import com.vantu.weather_api_dashboard.exception.WeatherException;
import com.vantu.weather_api_dashboard.response.ApiResponse;
import com.vantu.weather_api_dashboard.response.GeocodingResponse;
import com.vantu.weather_api_dashboard.response.WeatherResponse;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTests {

    @Mock
    private CacheService cacheService;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private OpenWeatherClient openWeatherClient;

    @InjectMocks
    private WeatherService weatherService;

    private GeocodingResponse geo;
    private WeatherResponse weather;

    @BeforeEach
    void setup() {
        geo = new GeocodingResponse();
        geo.setLat(10.0);
        geo.setLon(106.0);

        weather = new WeatherResponse();
        weather.setTimezone("Asia/Ho_Chi_Minh");
    }

    @Test
    @DisplayName("getCurrentWeather() uses cache when available")
    void testCurrentCacheHit() {
        when(cacheService.get(anyString(), eq(WeatherResponse.class))).thenReturn(Optional.of(weather));

        ApiResponse<WeatherResponse> res = weatherService.getCurrentWeather("Ha Noi");
        assertTrue(res.isCacheHit());
        assertEquals("cache", res.getSource());
        verifyNoInteractions(geocodingService, openWeatherClient);
    }

    @Test
    @DisplayName("getHourlyForecast() fetches API then caches on miss")
    void testHourlyCacheMiss() {
        when(cacheService.get(anyString(), eq(WeatherResponse.class))).thenReturn(Optional.empty());
        when(geocodingService.getByCity("Ha Noi")).thenReturn(ApiResponse.<GeocodingResponse>builder().data(geo).build());
        when(openWeatherClient.getHourly(10.0, 106.0)).thenReturn(weather);

        ApiResponse<WeatherResponse> res = weatherService.getHourlyForecast("Ha Noi");
        assertEquals("api", res.getSource());
        verify(cacheService).set(eq("weather:hourly:ha_noi"), eq(weather), eq(Duration.ofSeconds(600)));
    }

    @Test
    @DisplayName("getDailyForecast() falls back to stale cache when API fails")
    void testDailyFallbackToCache() {
        when(cacheService.get("weather:daily:ha_noi", WeatherResponse.class)).thenReturn(Optional.empty())
            .thenReturn(Optional.of(weather));
        when(geocodingService.getByCity("Ha Noi")).thenReturn(ApiResponse.<GeocodingResponse>builder().data(geo).build());
        when(openWeatherClient.getDaily(10.0, 106.0)).thenThrow(new RuntimeException("API down"));

        ApiResponse<WeatherResponse> res = weatherService.getDailyForecast("Ha Noi");
        assertNotNull(res.getData());
        assertFalse(res.isCacheHit());
        assertEquals("api", res.getSource());
    }

    @Test
    @DisplayName("getAlerts() throws if no cached data for fallback")
    void testAlertsNoCacheForFallback() {
        when(cacheService.get("weather:alerts:ha_noi", WeatherResponse.class)).thenReturn(Optional.empty());
        when(geocodingService.getByCity("Ha Noi")).thenReturn(ApiResponse.<GeocodingResponse>builder().data(geo).build());
        when(openWeatherClient.getAlerts(10.0, 106.0)).thenThrow(new RuntimeException("API down"));

        assertThrows(WeatherException.class, () -> weatherService.getAlerts("Ha Noi"));
    }
}


