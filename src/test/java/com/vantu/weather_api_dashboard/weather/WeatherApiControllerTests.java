package com.vantu.weather_api_dashboard.weather;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import com.vantu.weather_api_dashboard.controller.WeatherApiController;
import com.vantu.weather_api_dashboard.exception.WeatherException;
import com.vantu.weather_api_dashboard.response.ApiResponse;
import com.vantu.weather_api_dashboard.response.WeatherResponse;
import com.vantu.weather_api_dashboard.service.WeatherService;

@WebMvcTest(WeatherApiController.class)
public class WeatherApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private WeatherService weatherService;

	private WeatherResponse mockWeather;
	private ApiResponse<WeatherResponse> apiResponse;

	@BeforeEach
	void setUp() {
		mockWeather = new WeatherResponse();
		mockWeather.setLat(10.0);
		mockWeather.setLon(106.0);
		mockWeather.setTimezone("Asia/Ho_Chi_Minh");

		apiResponse = ApiResponse.<WeatherResponse>builder().data(mockWeather).cacheHit(false).source("api")
				.respondedInMs(123).timestamp("2025-10-13T00:00:00Z").build();
	}

	// -------------------------------------------------------------------------
	// BASIC ROUTE TESTS (controller → service)
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("/current → should return current weather successfully")
	void testGetCurrentWeatherSuccess() throws Exception {
		when(weatherService.getCurrentWeather("hanoi")).thenReturn(apiResponse);

		mockMvc.perform(get("/api/v1/weather/current").param("city", "hanoi")).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.timezone").value("Asia/Ho_Chi_Minh"))
				.andExpect(jsonPath("$.cacheHit").value(false)).andExpect(jsonPath("$.source").value("api"));

		verify(weatherService).getCurrentWeather("hanoi");
	}

	@Test
	@DisplayName("/minutely → should return minutely forecast successfully")
	void testGetMinutelyForecastSuccess() throws Exception {
		when(weatherService.getMinutelyForecast("hanoi")).thenReturn(apiResponse);

		mockMvc.perform(get("/api/v1/weather/minutely").param("city", "hanoi")).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.lat").value(10.0));
	}

	@Test
	@DisplayName("/hourly → should return hourly forecast successfully")
	void testGetHourlyForecastSuccess() throws Exception {
		when(weatherService.getHourlyForecast("hanoi")).thenReturn(apiResponse);

		mockMvc.perform(get("/api/v1/weather/hourly").param("city", "hanoi")).andExpect(status().isOk())
				.andExpect(jsonPath("$.source").value("api"));
	}

	@Test
	@DisplayName("/daily → should return daily forecast successfully")
	void testGetDailyForecastSuccess() throws Exception {
		when(weatherService.getDailyForecast("hanoi")).thenReturn(apiResponse);

		mockMvc.perform(get("/api/v1/weather/daily").param("city", "hanoi")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("/alerts → should return weather alerts successfully")
	void testGetAlertsSuccess() throws Exception {
		when(weatherService.getAlerts("hanoi")).thenReturn(apiResponse);

		mockMvc.perform(get("/api/v1/weather/alerts").param("city", "hanoi")).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.timezone").value("Asia/Ho_Chi_Minh"));
	}

	// -------------------------------------------------------------------------
	// ERROR CASES (simulate WeatherException, fallback, invalid input)
	// -------------------------------------------------------------------------

	@Test
	@DisplayName("Missing city parameter → should return 400")
	void testMissingCityParam() throws Exception {
		mockMvc.perform(get("/api/v1/weather/current")).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("WeatherService throws exception → should propagate error")
	void testWeatherServiceThrowsException() throws Exception {
		when(weatherService.getCurrentWeather("hanoi")).thenThrow(new WeatherException("City not found"));

		mockMvc.perform(get("/api/v1/weather/current").param("city", "hanoi")).andExpect(status().isBadRequest());
	}
}
