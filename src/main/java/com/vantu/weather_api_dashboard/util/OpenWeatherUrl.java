package com.vantu.weather_api_dashboard.util;

public class OpenWeatherUrl {
	public static final String CURENT_URL = "https://api.openweathermap.org/data/3.0/onecall?lat=%f&lon=%f&exclude=minutely,hourly,daily,alerts&units=metric&lang=vi&appid=%s";
	public static final String MINUTELY_URL = "https://api.openweathermap.org/data/3.0/onecall?lat=%f&lon=%f&exclude=current,hourly,daily,alerts&units=metric&lang=vi&appid=%s";
	public static final String HOURLY_URL = "https://api.openweathermap.org/data/3.0/onecall?lat=%f&lon=%f&exclude=current,minutely,daily,alerts&units=metric&lang=vi&appid=%s";
	public static final String DAILY_URL = "https://api.openweathermap.org/data/3.0/onecall?lat=%f&lon=%f&exclude=current,minutely,hourly,alerts&units=metric&lang=vi&appid=%s";
	public static final String ALERTS_URL = "https://api.openweathermap.org/data/3.0/onecall?lat=%f&lon=%f&units=metric&lang=vi&appid=%s";
}
