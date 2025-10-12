package com.vantu.weather_api_dashboard.service;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vantu.weather_api_dashboard.client.OpenWeatherClient;
import com.vantu.weather_api_dashboard.exception.GeocodingException;
import com.vantu.weather_api_dashboard.response.GeocodingResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeocodingService {

	private final CacheService cacheService;
	private final OpenWeatherClient openWeatherClient;

	public GeocodingResponse getByCity(String city) {
		String normCity = normalize(city);
		String cacheKey = "geo:" + normCity;

		return cacheService.get(cacheKey, GeocodingResponse.class).orElseGet(() -> {
			List<GeocodingResponse> results = openWeatherClient.direct(city);
			if (results.isEmpty()) {
				throw new GeocodingException("City not found: " + city);
			}
			
			GeocodingResponse res = results.get(0);
			cacheService.set(cacheKey, res, Duration.ofHours(24));
			
			return res;
		});
	}

	public GeocodingResponse getByCoordinates(double lat, double lon) {
		String cacheKey = String.format("geo:%.4f_%.4f", lat, lon);

		List<GeocodingResponse> results = openWeatherClient.reverse(lat, lon);
		if (results.isEmpty()) {
			throw new RuntimeException("Coordinates not found");
		}
		GeocodingResponse res = results.get(0);
		// cacheService.set(cacheKey, res, Duration.ofHours(24));
		return res;

//		return cacheService.get(cacheKey, GeocodingResponse.class).orElseGet(() -> {
//
//		});
	}

	private String normalize(String city) {
		return city.trim().toLowerCase().replaceAll("\\s+", "_");
	}
}
