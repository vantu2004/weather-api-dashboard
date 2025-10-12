package com.vantu.weather_api_dashboard.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeocodingResponse {
	private String name;
	private String state;
	private String country;
	private double lat;
	private double lon;
}
