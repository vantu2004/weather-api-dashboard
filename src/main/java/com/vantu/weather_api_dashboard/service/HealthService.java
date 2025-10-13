package com.vantu.weather_api_dashboard.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.vantu.weather_api_dashboard.client.OpenWeatherClient;
import com.vantu.weather_api_dashboard.response.HealthResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final GeolocationService geolocationService;
	private final GeocodingService geocodingService;
	private final OpenWeatherClient openWeatherClient;

	public HealthResponse checkSystemHealth() {
		HealthResponse response = new HealthResponse();
		long start = System.nanoTime();

		// Redis check
		boolean redisUp = false;
		try {
			redisTemplate.opsForValue().set("health:ping", "pong");

			Object val = redisTemplate.opsForValue().get("health:ping");

			redisUp = "pong".equals(val);
		} catch (Exception e) {
			log.error("[HEALTH] Redis check failed: {}", e.getMessage());
		}

		// OpenWeather API check
		boolean openWeatherUp = false;
		long apiLatencyMs = -1;
		try {
			long apiStart = System.nanoTime();

			openWeatherClient.healthCheck(); // method mới, gọi đơn giản OpenWeather endpoint

			apiLatencyMs = (System.nanoTime() - apiStart) / 1_000_000;

			openWeatherUp = true;
		} catch (Exception e) {
			log.warn("[HEALTH] OpenWeather API check failed: {}", e.getMessage());
		}

		// GeolocationService check (IP2Location DB)
		boolean ip2LocationUp = false;
		try {
			geolocationService.testDatabase(); // gọi hàm test
			ip2LocationUp = true;
		} catch (Exception e) {
			log.warn("[HEALTH] IP2Location DB check failed: {}", e.getMessage());
		}

		// GeocodingService check (city → lat/lon)
		boolean geocodingUp = false;
		try {
			geocodingService.selfCheck(); // test city đơn giản
			geocodingUp = true;
		} catch (Exception e) {
			log.warn("[HEALTH] Geocoding check failed: {}", e.getMessage());
		}

		long totalMs = (System.nanoTime() - start) / 1_000_000;

		response.setStatus(redisUp && openWeatherUp && ip2LocationUp && geocodingUp ? "UP" : "DEGRADED");
		response.setRedisStatus(redisUp ? "UP" : "DOWN");
		response.setOpenWeatherStatus(openWeatherUp ? "UP" : "DOWN");
		response.setIp2LocationStatus(ip2LocationUp ? "UP" : "DOWN");
		response.setGeocodingStatus(geocodingUp ? "UP" : "DOWN");
		response.setApiLatencyMs(apiLatencyMs);
		response.setTotalCheckMs(totalMs);
		response.setTimestamp(System.currentTimeMillis());

		return response;
	}
}
