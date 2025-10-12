package com.vantu.weather_api_dashboard.util;

public class GeocodingUrl {
	public static final String DIRECT_URL = "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s";
	public static final String REVERSE_URL = "https://api.openweathermap.org/geo/1.0/reverse?lat=%f&lon=%f&limit=1&appid=%s";
}
