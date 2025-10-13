package com.vantu.weather_api_dashboard.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vantu.weather_api_dashboard.client.OpenWeatherClient;
import com.vantu.weather_api_dashboard.exception.WeatherException;
import com.vantu.weather_api_dashboard.response.ApiResponse;
import com.vantu.weather_api_dashboard.response.GeocodingResponse;
import com.vantu.weather_api_dashboard.response.WeatherResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
	private final CacheService cacheService;
	private final GeocodingService geocodingService;
	private final OpenWeatherClient openWeatherClient;

	public ApiResponse<WeatherResponse> getCurrentWeather(String city) {
		return getWeather(city, "current", Duration.ofSeconds(60),
				(lat, lon, key) -> openWeatherClient.getCurrent(lat, lon));
	}

	public ApiResponse<WeatherResponse> getMinutelyForecast(String city) {
		return getWeather(city, "minutely", Duration.ofSeconds(600),
				(lat, lon, key) -> openWeatherClient.getMinutely(lat, lon));
	}

	public ApiResponse<WeatherResponse> getHourlyForecast(String city) {
		return getWeather(city, "hourly", Duration.ofSeconds(600),
				(lat, lon, key) -> openWeatherClient.getHourly(lat, lon));
	}

	public ApiResponse<WeatherResponse> getDailyForecast(String city) {
		return getWeather(city, "daily", Duration.ofSeconds(600),
				(lat, lon, key) -> openWeatherClient.getDaily(lat, lon));
	}

	public ApiResponse<WeatherResponse> getAlerts(String city) {
		return getWeather(city, "alerts", Duration.ofSeconds(600),
				(lat, lon, key) -> openWeatherClient.getAlerts(lat, lon));
	}

	private ApiResponse<WeatherResponse> getWeather(String city, String type, Duration ttl, WeatherFetcher fetcher) {
		long start = System.nanoTime();
		String normCity = normalize(city);
		String cacheKey = String.format("weather:%s:%s", type, normCity);

		boolean cacheHit = false;
		WeatherResponse data;

		try {
			Optional<WeatherResponse> cached = cacheService.get(cacheKey, WeatherResponse.class);
			if (cached.isPresent()) {
				cacheHit = true;
				data = cached.get();

				log.info("[CACHE HIT] {} weather for city '{}'", type, city);
			} else {
				GeocodingResponse geo = geocodingService.getByCity(city).getData();

				data = fetcher.fetch(geo.getLat(), geo.getLon(), cacheKey);
				cacheService.set(cacheKey, data, ttl);

				log.info("[API FETCH] {} weather for city '{}' cached (TTL={}s)", type, city, ttl.toSeconds());
			}
		} catch (Exception e) {
			// ❗ fallback stale data nếu API lỗi
			log.warn("[FALLBACK] {} weather API failed for '{}', using stale cache.", type, city);

			data = cacheService.get(cacheKey, WeatherResponse.class)
					.orElseThrow(() -> new WeatherException("No cached data available for " + city));
		}

		long durationMs = (System.nanoTime() - start) / 1_000_000;

		return ApiResponse.<WeatherResponse>builder().data(data).cacheHit(cacheHit).source(cacheHit ? "cache" : "api")
				.respondedInMs(durationMs).timestamp(Instant.now().toString()).build();
	}

	private String normalize(String city) {
		return city.trim().toLowerCase().replaceAll("\\s+", "_");
	}

	@FunctionalInterface
	private interface WeatherFetcher {
		WeatherResponse fetch(double lat, double lon, String cacheKey);
	}
}
