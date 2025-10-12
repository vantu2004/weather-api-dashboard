package com.vantu.weather_api_dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.weather_api_dashboard.service.GeocodingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/geocoding")
public class GeocodingApiController {
	private final GeocodingService geoService;

	@GetMapping("/direct")
	public ResponseEntity<?> getByCity(@RequestParam String city) {
		return ResponseEntity.ok(geoService.getByCity(city));
	}

	@GetMapping("/reverse")
	public ResponseEntity<?> getByCoordinates(@RequestParam double lat, @RequestParam double lon) {
		return ResponseEntity.ok(geoService.getByCoordinates(lat, lon));
	}
}
