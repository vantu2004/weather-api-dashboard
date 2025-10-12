package com.vantu.weather_api_dashboard.geolocation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.vantu.weather_api_dashboard.controller.GeolocationApiController;
import com.vantu.weather_api_dashboard.exception.GeolocationException;
import com.vantu.weather_api_dashboard.response.ApiResponse;
import com.vantu.weather_api_dashboard.response.GeolocationResponse;
import com.vantu.weather_api_dashboard.service.GeolocationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

@WebMvcTest(GeolocationApiController.class)
class GeolocationApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GeolocationService geolocationService;

	private ApiResponse<GeolocationResponse> mockResponse;

	@BeforeEach
	void setUp() {
		GeolocationResponse location = GeolocationResponse.builder().cityName("Hanoi").regionName("Hanoi").countryCode("VN")
				.countryName("Vietnam").build();

		mockResponse = ApiResponse.<GeolocationResponse>builder().data(location).cacheHit(false).source("local-db")
				.respondedInMs(10).timestamp(Instant.now().toString()).build();
	}

	@Test
	@DisplayName("Should return location successfully for valid IP")
	void testGetLocationByIpAddress_Success() throws Exception {
		when(geolocationService.getLocationByIp2Location(anyString())).thenReturn(mockResponse);

		mockMvc.perform(
				get("/api/v1/geolocation").header("X-FORWARDED-FOR", "8.8.8.8").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data.cityName").value("Hanoi"))
				.andExpect(jsonPath("$.data.countryCode").value("VN")).andExpect(jsonPath("$.source").value("local-db"))
				.andExpect(jsonPath("$.cacheHit").value(false));
	}

	@Test
	@DisplayName("Should throw GeolocationException when service fails")
	void testGetLocationByIpAddress_Fail() throws Exception {
		when(geolocationService.getLocationByIp2Location(anyString()))
				.thenThrow(new GeolocationException("Invalid IP"));

		mockMvc.perform(get("/api/v1/geolocation").header("X-FORWARDED-FOR", "999.999.999.999")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
}
