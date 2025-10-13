package com.vantu.weather_api_dashboard.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthResponse {
	private String status; // UP / DEGRADED
	private String redisStatus;
	private String openWeatherStatus;
	private String geocodingStatus;
	private String ip2LocationStatus;
	private long apiLatencyMs;
	private long totalCheckMs;
	private long timestamp;
}
