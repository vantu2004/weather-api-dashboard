package com.vantu.weather_api_dashboard.geocoding;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.vantu.weather_api_dashboard.controller.GeocodingApiController;
import com.vantu.weather_api_dashboard.exception.GeocodingException;
import com.vantu.weather_api_dashboard.response.ApiResponse;
import com.vantu.weather_api_dashboard.response.GeocodingResponse;
import com.vantu.weather_api_dashboard.service.GeocodingService;

@WebMvcTest(GeocodingApiController.class)
class GeocodingApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GeocodingService geocodingService;

	private ApiResponse<GeocodingResponse> mockResponse;

	@BeforeEach
	void setUp() {
		GeocodingResponse geo = new GeocodingResponse();
		geo.setName("Hanoi");
		geo.setState("Hanoi");
		geo.setCountry("VN");
		geo.setLat(21.0285);
		geo.setLon(105.8542);

		mockResponse = ApiResponse.<GeocodingResponse>builder().data(geo).cacheHit(false).source("api")
				.respondedInMs(120).timestamp(Instant.now().toString()).build();
	}

	// ========== /direct ==========
	@Nested
	@DisplayName("GET /api/v1/geocoding/direct")
	class DirectApiTests {

		@Test
		@DisplayName("Should return geocoding result successfully for valid city")
		void testGetByCity_Success() throws Exception {
			when(geocodingService.getByCity(anyString())).thenReturn(mockResponse);

			mockMvc.perform(get("/api/v1/geocoding/direct").param("city", "Hanoi").accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value("Hanoi"))
					.andExpect(jsonPath("$.source").value("api")).andExpect(jsonPath("$.cacheHit").value(false));
		}

		@Test
		@DisplayName("Should return 400 when city param missing")
		void testGetByCity_MissingParam() throws Exception {
			mockMvc.perform(get("/api/v1/geocoding/direct").accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Should return 400 when service throws GeocodingException")
		void testGetByCity_Fail() throws Exception {
			when(geocodingService.getByCity(anyString())).thenThrow(new GeocodingException("City not found: test"));

			mockMvc.perform(
					get("/api/v1/geocoding/direct").param("city", "UnknownCity").accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}
	}

	// ========== /reverse ==========
	@Nested
	@DisplayName("GET /api/v1/geocoding/reverse")
	class ReverseApiTests {

		@Test
		@DisplayName("Should return geocoding result successfully for valid coordinates")
		void testGetByCoordinates_Success() throws Exception {
			when(geocodingService.getByCoordinates(anyDouble(), anyDouble())).thenReturn(mockResponse);

			mockMvc.perform(get("/api/v1/geocoding/reverse").param("lat", "21.0285").param("lon", "105.8542")
					.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
					.andExpect(jsonPath("$.data.name").value("Hanoi")).andExpect(jsonPath("$.data.country").value("VN"))
					.andExpect(jsonPath("$.source").value("api"));
		}

		@Test
		@DisplayName("Should return 400 when coordinates params missing")
		void testGetByCoordinates_MissingParams() throws Exception {
			mockMvc.perform(get("/api/v1/geocoding/reverse").accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Should return 400 when service throws GeocodingException")
		void testGetByCoordinates_Fail() throws Exception {
			when(geocodingService.getByCoordinates(anyDouble(), anyDouble()))
					.thenThrow(new GeocodingException("Coordinates not found"));

			mockMvc.perform(get("/api/v1/geocoding/reverse").param("lat", "0").param("lon", "0")
					.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
		}
	}
}
