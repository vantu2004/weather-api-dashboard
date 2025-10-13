package com.vantu.weather_api_dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.weather_api_dashboard.service.HealthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/health")
public class HealthController {
	private final HealthService healthService;

	@GetMapping
	public ResponseEntity<?> checkHealth() {
		return ResponseEntity.ok(healthService.checkSystemHealth());
	}
}
