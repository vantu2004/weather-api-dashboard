package com.vantu.weather_api_dashboard.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.vantu.weather_api_dashboard.response.WeatherResponse;
import com.vantu.weather_api_dashboard.util.OpenWeatherUrl;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenWeatherClient {
	@Value("${openweather.api-key}")
	private String apiKey;

	private final RestTemplate restTemplate = new RestTemplate();

	public WeatherResponse getCurrent(double lat, double lon) {
		String url = String.format(OpenWeatherUrl.CURENT_URL, lat, lon, apiKey);
		return restTemplate.getForObject(url, WeatherResponse.class);
	}

	public WeatherResponse getMinutely(double lat, double lon) {
		String url = String.format(OpenWeatherUrl.MINUTELY_URL, lat, lon, apiKey);
		return restTemplate.getForObject(url, WeatherResponse.class);
	}

	public WeatherResponse getHourly(double lat, double lon) {
		String url = String.format(OpenWeatherUrl.HOURLY_URL, lat, lon, apiKey);
		return restTemplate.getForObject(url, WeatherResponse.class);
	}

	public WeatherResponse getDaily(double lat, double lon) {
		String url = String.format(OpenWeatherUrl.DAILY_URL, lat, lon, apiKey);
		return restTemplate.getForObject(url, WeatherResponse.class);
	}

	public WeatherResponse getAlerts(double lat, double lon) {
		String url = String.format(OpenWeatherUrl.ALERTS_URL, lat, lon, apiKey);
		return restTemplate.getForObject(url, WeatherResponse.class);
	}
}
