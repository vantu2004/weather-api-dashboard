package com.vantu.weather_api_dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Đại diện kết quả geolocation từ IP2Location. Tái sử dụng để truyền cho các
 * service khác (như WeatherService).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeolocationResponse {
	private String cityName;
	private String regionName;
	private String countryName;
	private String countryCode;
}
