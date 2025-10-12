package com.vantu.weather_api_dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.weather_api_dashboard.model.Location;
import com.vantu.weather_api_dashboard.service.GeolocationService;
import com.vantu.weather_api_dashboard.util.IpAddress;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/geolocation")
public class GeolocationApiController {
	private final GeolocationService geolocationService;

	@GetMapping
	public ResponseEntity<?> getLocationByIpAddress(HttpServletRequest request) {
		String ipAddress = IpAddress.getIpAddress(request);
		Location location = this.geolocationService.getLocationByIp2Location(ipAddress);

		return ResponseEntity.ok(location);
	}
}
