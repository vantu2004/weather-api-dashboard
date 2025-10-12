package com.vantu.weather_api_dashboard.service;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheService {
	private final RedisTemplate<String, Object> redisTemplate;

	public <T> void set(String key, T value, Duration ttl) {
		redisTemplate.opsForValue().set(key, value, ttl);
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(String key, Class<T> clazz) {
		Object value = redisTemplate.opsForValue().get(key);
		if (clazz.isInstance(value)) {
			return Optional.of((T) value);
		}
		return Optional.empty();
	}
}
