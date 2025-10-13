package com.vantu.weather_api_dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.weather_api_dashboard.response.ApiResponse;
import com.vantu.weather_api_dashboard.service.WeatherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
public class WeatherApiController {

	private final WeatherService weatherService;

	@GetMapping("/current")
	public ApiResponse<?> getCurrent(@RequestParam String city) {
		return weatherService.getCurrentWeather(city);
	}

	@GetMapping("/minutely")
	public ApiResponse<?> getMinutely(@RequestParam String city) {
		return weatherService.getMinutelyForecast(city);
	}

	@GetMapping("/hourly")
	public ApiResponse<?> getHourly(@RequestParam String city) {
		return weatherService.getHourlyForecast(city);
	}

	@GetMapping("/daily")
	public ApiResponse<?> getDaily(@RequestParam String city) {
		return weatherService.getDailyForecast(city);
	}

	@GetMapping("/alerts")
	public ApiResponse<?> getAlerts(@RequestParam String city) {
		return weatherService.getAlerts(city);
	}
}
