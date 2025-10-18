package com.vantu.weather_api_dashboard.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vantu.weather_api_dashboard.response.ApiResponse;
import com.vantu.weather_api_dashboard.response.GeolocationResponse;

public class GeolocationServiceTests {

    @Test
    @DisplayName("IP2Location DB loads and can query a known IP")
    void testIp2LocationLookup() throws Exception {
        GeolocationService svc = new GeolocationService();

        svc.testDatabase();

        ApiResponse<GeolocationResponse> res = svc.getLocationByIp2Location("8.8.8.8");
        assertNotNull(res.getData());
        assertNotNull(res.getData().getCountryCode());
        assertEquals("local-db", res.getSource());
        assertFalse(res.isCacheHit());
    }
}


