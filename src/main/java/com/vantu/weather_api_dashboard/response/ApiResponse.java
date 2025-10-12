package com.vantu.weather_api_dashboard.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiResponse<T> {
	private T data; // dữ liệu chính (GeoResponse, WeatherResponse,...)
	private boolean cacheHit; // true nếu lấy từ cache
	private String source; // "cache" hoặc "api"
	private long respondedInMs; // thời gian phản hồi
	private String timestamp; // ISO timestamp (UTC)
}
