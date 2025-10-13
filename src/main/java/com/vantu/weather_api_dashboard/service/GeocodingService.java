package com.vantu.weather_api_dashboard.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vantu.weather_api_dashboard.client.GeocodingClient;
import com.vantu.weather_api_dashboard.exception.GeocodingException;
import com.vantu.weather_api_dashboard.response.GeocodingResponse;
import com.vantu.weather_api_dashboard.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeocodingService {
	private final CacheService cacheService;
	private final GeocodingClient openWeatherClient;

	public ApiResponse<GeocodingResponse> getByCity(String city) {
		long start = System.nanoTime();

		// chuẩn hóa city theo snake_case
		String normCity = normalize(city);

		// chuẩn hóa key để lưu redis
		String cacheKey = "geo:" + normCity;

		boolean cacheHit = false;
		GeocodingResponse result;

		Optional<GeocodingResponse> cached = cacheService.get(cacheKey, GeocodingResponse.class);
		if (cached.isPresent()) {
			cacheHit = true;
			result = cached.get();

			log.info("[CACHE HIT] Geo data for city '{}' fetched from cache.", city);
		} else {
			List<GeocodingResponse> results = openWeatherClient.direct(city);
			if (results.isEmpty()) {
				throw new GeocodingException("City not found: " + city);
			}

			result = results.get(0);

			// set time lưu redis là 24h
			cacheService.set(cacheKey, result, Duration.ofHours(24));

			log.info("[API FETCH] Geo data for city '{}' fetched from OpenWeather API.", city);
		}

		// convert từ nano về ms
		long durationMs = (System.nanoTime() - start) / 1_000_000;

		return ApiResponse.<GeocodingResponse>builder().data(result).cacheHit(cacheHit)
				.source(cacheHit ? "cache" : "api").respondedInMs(durationMs).timestamp(Instant.now().toString())
				.build();
	}

	public ApiResponse<GeocodingResponse> getByCoordinates(double lat, double lon) {
		long start = System.nanoTime();
		String cacheKey = String.format("geo:%.4f_%.4f", lat, lon);

		boolean cacheHit = false;
		GeocodingResponse result;

		Optional<GeocodingResponse> cached = cacheService.get(cacheKey, GeocodingResponse.class);
		if (cached.isPresent()) {
			cacheHit = true;
			result = cached.get();

			log.info("[CACHE HIT] Geo data for ({}, {}) fetched from cache.", lat, lon);
		} else {
			List<GeocodingResponse> results = openWeatherClient.reverse(lat, lon);
			if (results.isEmpty()) {
				throw new GeocodingException("Coordinates not found");
			}

			result = results.get(0);
			cacheService.set(cacheKey, result, Duration.ofHours(24));

			log.info("[API FETCH] Geo data for ({}, {}) fetched from OpenWeather API.", lat, lon);
		}

		long durationMs = (System.nanoTime() - start) / 1_000_000;

		return ApiResponse.<GeocodingResponse>builder().data(result).cacheHit(cacheHit)
				.source(cacheHit ? "cache" : "api").respondedInMs(durationMs).timestamp(Instant.now().toString())
				.build();
	}

	private String normalize(String city) {
		return city.trim().toLowerCase().replaceAll("\\s+", "_");
	}

	public void selfCheck() {
		try {
			List<GeocodingResponse> result = openWeatherClient.direct("London");
			if (result == null || result.isEmpty()) {
				throw new RuntimeException("Geocoding API returned empty");
			}
		} catch (Exception e) {
			throw new RuntimeException("Geocoding API check failed", e);
		}
	}

}
