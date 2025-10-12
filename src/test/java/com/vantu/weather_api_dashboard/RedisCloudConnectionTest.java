package com.vantu.weather_api_dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisCloudConnectionTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Test
	void testConnection() {
		redisTemplate.opsForValue().set("test:key", "hello-redis-cloud");
		String value = (String) redisTemplate.opsForValue().get("test:key");
		assertEquals("hello-redis-cloud", value);
	}
}
