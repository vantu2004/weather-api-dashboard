package com.vantu.weather_api_dashboard.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherResponse {
	private double lat;
	private double lon;
	private String timezone;

	// offset từ UTC, đơn vị là giây
	@JsonProperty("timezone_offset")
	private int timezoneOffset;

	private Current current;
	private List<Minutely> minutely;
	private List<Hourly> hourly;
	private List<Daily> daily;
	private List<Alert> alerts;

	@Getter
	@Setter
	public static class Current {
		private long dt;
		private long sunrise;
		private long sunset;
		private double temp;

		@JsonProperty("feels_like")
		private double feelsLike;

		private int pressure;
		private int humidity;

		@JsonProperty("dew_point")
		private double dewPoint;

		private double uvi;
		private int clouds;
		private int visibility;

		@JsonProperty("wind_speed")
		private double windSpeed;

		@JsonProperty("wind_deg")
		private double windDeg;

		@JsonProperty("wind_gust")
		private Double windGust;

		private List<Weather> weather;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Rain rain;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Snow snow;
	}

	@Getter
	@Setter
	public static class Minutely {
		private long dt;
		private double precipitation;
	}

	@Getter
	@Setter
	public static class Hourly {
		private long dt;
		private double temp;

		@JsonProperty("feels_like")
		private double feelsLike;

		private int pressure;
		private int humidity;

		@JsonProperty("dew_point")
		private double dewPoint;

		private double uvi;
		private int clouds;
		private int visibility;

		@JsonProperty("wind_speed")
		private double windSpeed;

		@JsonProperty("wind_deg")
		private double windDeg;

		@JsonProperty("wind_gust")
		private Double windGust;

		private double pop; // xác suất precipitation
		private List<Weather> weather;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Rain rain;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Snow snow;
	}

	@Getter
	@Setter
	public static class Daily {
		private long dt;
		private long sunrise;
		private long sunset;

		@JsonProperty("moonrise")
		private long moonrise;

		@JsonProperty("moonset")
		private long moonset;

		@JsonProperty("moon_phase")
		private double moonPhase;

		private String summary;
		private Temp temp;

		@JsonProperty("feels_like")
		private FeelsLike feelsLike;

		private int pressure;
		private int humidity;

		@JsonProperty("dew_point")
		private double dewPoint;

		@JsonProperty("wind_speed")
		private double windSpeed;

		@JsonProperty("wind_deg")
		private double windDeg;

		@JsonProperty("wind_gust")
		private Double windGust;

		private List<Weather> weather;
		private int clouds;
		private double pop;

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Double rain; // trong daily, "rain" thường là số (mm) nếu có

		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Double snow; // cũng có thể có snow nếu có tuyết

		private double uvi;
	}

	@Getter
	@Setter
	public static class Alert {
		@JsonProperty("sender_name")
		private String senderName;

		private String event;
		private long start;
		private long end;
		private String description;
		private List<String> tags;
	}

	@Getter
	@Setter
	public static class Temp {
		private double day;
		private double min;
		private double max;
		private double night;
		private double eve;
		private double morn;
	}

	@Getter
	@Setter
	public static class FeelsLike {
		private double day;
		private double night;
		private double eve;
		private double morn;
	}

	@Getter
	@Setter
	public static class Weather {
		private int id;
		private String main;
		private String description;
		private String icon;
	}

	@Getter
	@Setter
	public static class Rain {
		// JSON dùng “1h”
		@JsonProperty("1h")
		private Double _1h;
	}

	@Getter
	@Setter
	public static class Snow {
		@JsonProperty("1h")
		private Double _1h;
	}
}
