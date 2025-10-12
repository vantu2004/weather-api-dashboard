package com.vantu.weather_api_dashboard.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.vantu.weather_api_dashboard.response.GeocodingResponse;
import com.vantu.weather_api_dashboard.util.GeocodingUrl;

@Component
public class OpenWeatherClient {

	@Value("${openweather.api-key}")
	private String apiKey;

	private final RestTemplate restTemplate = new RestTemplate();

	public List<GeocodingResponse> direct(String city) {
		String url = String.format(GeocodingUrl.DIRECT_URL, city, apiKey);
		GeocodingResponse[] response = restTemplate.getForObject(url, GeocodingResponse[].class);
		
		return Arrays.asList(response);
	}

	public List<GeocodingResponse> reverse(double lat, double lon) {
		String url = String.format(GeocodingUrl.REVERSE_URL, lat, lon, apiKey);
		GeocodingResponse[] response = restTemplate.getForObject(url, GeocodingResponse[].class);
		
		return Arrays.asList(response);
	}
}
