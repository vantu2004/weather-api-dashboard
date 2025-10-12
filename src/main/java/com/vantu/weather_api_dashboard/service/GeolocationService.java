package com.vantu.weather_api_dashboard.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.vantu.weather_api_dashboard.exception.GeolocationException;
import com.vantu.weather_api_dashboard.response.ApiResponse;
import com.vantu.weather_api_dashboard.response.GeolocationResponse;

@Service
public class GeolocationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
	private static final String DB_PATH = "/ip2_location_db/IP2LOCATION-LITE-DB3.BIN";

	private IP2Location ip2Location;

	public GeolocationService() {
		try (InputStream inputStream = getClass().getResourceAsStream(DB_PATH)) {
			if (inputStream == null) {
				throw new RuntimeException("IP2Location database not found at " + DB_PATH);
			}

			byte[] data = inputStream.readAllBytes();
			ip2Location = new IP2Location();
			ip2Location.Open(data);

			LOGGER.info("IP2Location database loaded successfully.");
		} catch (Exception e) {
			LOGGER.error("Failed to initialize IP2Location: {}", e.getMessage(), e);
		}
	}

	public ApiResponse<GeolocationResponse> getLocationByIp2Location(String ipAddress) throws GeolocationException {
		long start = System.nanoTime();

		try {
			IPResult ipResult = ip2Location.IPQuery(ipAddress);

			if (!"OK".equals(ipResult.getStatus())) {
				throw new GeolocationException("Geolocation Failed: " + ipResult.getStatus());
			}

			GeolocationResponse location = GeolocationResponse.builder().cityName(ipResult.getCity()).regionName(ipResult.getRegion())
					.countryName(ipResult.getCountryLong()).countryCode(ipResult.getCountryShort()).build();

			long durationMs = (System.nanoTime() - start) / 1_000_000;

			LOGGER.info("[IP2LOCATION LOOKUP] {} -> {}, {}, {} ({} ms)", ipAddress, location.getCityName(),
					location.getRegionName(), location.getCountryCode(), durationMs);

			// không dùng cache, lấy trực tiếp từ local DB
			return ApiResponse.<GeolocationResponse>builder().data(location).cacheHit(false).source("local-db")
					.respondedInMs(durationMs).timestamp(Instant.now().toString()).build();

		} catch (IOException e) {
			throw new GeolocationException("Error querying IP2Location database", e);
		}
	}
}
