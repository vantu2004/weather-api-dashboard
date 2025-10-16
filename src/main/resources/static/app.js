(function () {
	const baseUrl = "/api/v1/weather";
	const cityInput = document.getElementById("cityInput");
	const searchBtn = document.getElementById("searchBtn");
	const daysInput = document.getElementById("daysInput");
	const statusText = document.getElementById("statusText");
	const card = document.getElementById("weatherCard");
	const cityName = document.getElementById("cityName");
	const localDate = document.getElementById("localDate");
	const tempNow = document.getElementById("tempNow");
	const conditionNow = document.getElementById("conditionNow");
	const iconNow = document.getElementById("iconNow");
	const humidityNow = document.getElementById("humidityNow");
	const windNow = document.getElementById("windNow");
	const forecastRow = document.getElementById("forecastRow");

	function setStatus(message, type = "info") {
		statusText.textContent = message || "";
		statusText.style.color = type === "error" ? "#f87171" : "#94a3b8";
	}

	async function fetchWeather(endpoint, city) {
		const url = `${baseUrl}/${endpoint}?city=${encodeURIComponent(city)}`;
		const response = await fetch(url, { headers: { "Accept": "application/json" } });
		const raw = await response.text();
		let parsed;
		try { parsed = raw ? JSON.parse(raw) : undefined; } catch (_) { parsed = undefined; }
		if (!response.ok) {
			const err = new Error(`HTTP ${response.status}`);
			err.status = response.status;
			err.body = parsed || raw;
			throw err;
		}
		return parsed;
	}

	function pickIcon(id, main) {
		if (main === "Clear") return "☀️";
		if (main === "Clouds") return "⛅";
		if (main === "Rain") return "🌧️";
		if (main === "Drizzle") return "🌦️";
		if (main === "Thunderstorm") return "⛈️";
		if (main === "Snow") return "❄️";
		return "🌡️";
	}

	function formatDate(ts, tzOffsetSeconds) {
		const ms = (ts + tzOffsetSeconds) * 1000;
		const d = new Date(ms);
		return d.toUTCString().slice(0, 16);
	}

	function renderCurrent(api) {
		const data = api.data; // ApiResponse<WeatherResponse>
		const { current, timezone_offset: tzOff } = normalizeKeys(data);
		if (!current) return;
		cityName.textContent = cityInput.value.trim();
		localDate.textContent = formatDate(current.dt, tzOff || data.timezoneOffset || 0);
		tempNow.textContent = `${Math.round(current.temp)}°C`;
		const w = (current.weather && current.weather[0]) || {};
		conditionNow.textContent = w.description || w.main || "";
		iconNow.textContent = pickIcon(w.id, w.main);
		humidityNow.textContent = `${current.humidity}%`;
		windNow.textContent = `${current.wind_speed ?? current.windSpeed} m/s`;
		card.hidden = false;
	}

function renderDaily(api) {
		const data = api.data;
		const { daily, timezone_offset: tzOff } = normalizeKeys(data);
		if (!Array.isArray(daily)) return;
		forecastRow.innerHTML = "";
		const n = clamp(parseInt(daysInput.value, 10) || 3, 1, 7);
		daily.slice(1, 1 + n).forEach((d) => {
			const day = document.createElement("div");
			day.className = "day";
			const w = (d.weather && d.weather[0]) || {};
			day.innerHTML = `
				<div class="d">${formatDate(d.dt, tzOff || data.timezoneOffset || 0)}</div>
				<div>${pickIcon(w.id, w.main)}</div>
				<div class="t">${Math.round(d.temp?.max ?? d.temp?.day ?? 0)}°C</div>
			`;
			forecastRow.appendChild(day);
		});
	}

	function normalizeKeys(obj) {
		// Convert camel to snake fallback handling when needed
		return new Proxy(obj, {
			get(target, prop) {
				if (prop in target) return target[prop];
				const alt = camelSnake(prop);
				return target[alt];
			}
		});
	}

	function camelSnake(key) {
		// timezone_offset <-> timezoneOffset, wind_speed <-> windSpeed
		if (typeof key !== "string") return key;
		return key.replace(/[A-Z]/g, (m) => "_" + m.toLowerCase());
	}

function clamp(v, min, max) { return Math.max(min, Math.min(max, v)); }

	async function search() {
		const city = (cityInput.value || "").trim();
		if (!city) {
			setStatus("Vui lòng nhập tên thành phố.", "error");
			cityInput.focus();
			return;
		}

		const n = parseInt(daysInput.value, 10);
		if (!Number.isFinite(n) || n < 1 || n > 7) {
			setStatus("Số ngày nhập không hợp lệ (1–7).", "error");
			daysInput.focus();
			return;
		}
		setStatus("Đang tải...");
		try {
			const [currentResp, dailyResp] = await Promise.all([
				fetchWeather("current", city),
				fetchWeather("daily", city)
			]);
			renderCurrent(currentResp);
			renderDaily(dailyResp);
			setStatus("Hoàn tất.");
		} catch (err) {
			console.error(err);
			if (err && err.status === 400) {
				setStatus("Thành phố không hợp lệ.", "error");
			} else {
				setStatus(err.message || "Đã xảy ra lỗi.", "error");
			}
		}
	}

	function wireEvents() {
		searchBtn.addEventListener("click", search);
		daysInput.addEventListener("change", search);
		daysInput.addEventListener("keydown", (e) => {
			if (e.key === "Enter") {
				e.preventDefault();
				search();
			}
		});
		cityInput.addEventListener("keydown", (e) => {
			if (e.key === "Enter") {
				e.preventDefault();
				search();
			}
		});
	}

	window.addEventListener("DOMContentLoaded", wireEvents);
})();


