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
	const hourlyRow = document.getElementById("hourlyRow");

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

	function formatHourLabel(ts, tzOffsetSeconds) {
		const ms = (ts + tzOffsetSeconds) * 1000;
		const d = new Date(ms);
		const h = d.getUTCHours();
		if (h === 0) return "12h đêm";
		if (h < 12) return `${h}h sáng`;
		if (h === 12) return "12h trưa";
		if (h < 18) return `${h - 12}h chiều`;
		return `${h - 12}h tối`;
	}

	function formatHour24(ts, tzOffsetSeconds) {
		const ms = (ts + tzOffsetSeconds) * 1000;
		return new Date(ms).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', hour12: false, timeZone: 'UTC' });
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

	function renderHourly(api) {
		const data = api.data;
		const { hourly, timezone_offset: tzOff } = normalizeKeys(data);
		if (!Array.isArray(hourly)) return;
		hourlyRow.innerHTML = "";
		// Show next 12 hours
		hourly.slice(0, 12).forEach((h) => {
			const item = document.createElement("div");
			item.className = "hour";
			const w = (h.weather && h.weather[0]) || {};
			item.innerHTML = `
				<div class="h">${formatHour24(h.dt, tzOff || data.timezoneOffset || 0)}</div>
				<div>${pickIcon(w.id, w.main)}</div>
				<div class="t">${Math.round(h.temp)}°C</div>
			`;
			hourlyRow.appendChild(item);
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

	function validateCityName(city) {
		const errors = [];
		
		// Debug log để kiểm tra
		console.log("Validating city:", city);
		
		// Kiểm tra số trước
		const numberRegex = /[0-9]/;
		if (numberRegex.test(city)) {
			console.log("Found numbers in city name");
			errors.push("Thành phố không chứa số.");
		}
		
		// Kiểm tra ký tự đặc biệt (chỉ cho phép chữ cái, dấu cách, dấu gạch ngang, dấu nháy đơn)
		// Loại trừ số khỏi kiểm tra ký tự đặc biệt
		const specialCharRegex = /[^a-zA-ZÀ-ỹ\s\-'0-9]/;
		if (specialCharRegex.test(city)) {
			console.log("Found special characters in city name");
			errors.push("Thành phố không chứa ký tự đặc biệt.");
		}
		
		console.log("City validation errors:", errors);
		return errors;
	}

	let isSearching = false;

	async function search() {
		// Tránh gọi API nhiều lần cùng lúc
		if (isSearching) {
			console.log("Đang tìm kiếm, bỏ qua request này");
			return;
		}
		
		const city = (cityInput.value || "").trim();
		const daysValue = daysInput.value.trim();
		const n = parseFloat(daysValue);
		
		// Debug log
		console.log("=== SEARCH START ===");
		console.log("City:", city, "Days value:", daysValue, "Parsed n:", n);
		console.log("Is integer?", Number.isInteger(n));
		console.log("Validation check:", !daysValue || isNaN(n) || !Number.isInteger(n) || n < 1 || n > 7);
		
		// Thu thập tất cả lỗi validation (thành phố + ngày)
		const allValidationErrors = [];
		
		// Kiểm tra thành phố
		if (!city) {
			allValidationErrors.push("Vui lòng nhập tên thành phố.");
		} else {
			// Kiểm tra validation thành phố nếu có nhập
			const cityErrors = validateCityName(city);
			allValidationErrors.push(...cityErrors);
		}
		
		// Kiểm tra ngày
		if (!daysValue || isNaN(n) || !Number.isInteger(n) || n < 1 || n > 7) {
			allValidationErrors.push("Số ngày nhập không hợp lệ (1–7).");
		}
		
		// Nếu có lỗi validation, hiển thị tất cả và dừng lại
		if (allValidationErrors.length > 0) {
			setStatus(allValidationErrors.join(" "), "error");
			if (!city) cityInput.focus();
			else daysInput.focus();
			return;
		}
		
		isSearching = true;
		setStatus("Đang tải...");
		
		try {
			console.log("🚀 Calling API...");
			const [currentResp, hourlyResp, dailyResp] = await Promise.all([
				fetchWeather("current", city),
				fetchWeather("hourly", city),
				fetchWeather("daily", city)
			]);
			console.log("✅ API calls successful");
			
			// Kiểm tra validation ngày ngay cả khi API thành công
			if (!daysValue || isNaN(n) || !Number.isInteger(n) || n < 1 || n > 7) {
				setStatus("Số ngày nhập không hợp lệ (1–7).", "error");
				daysInput.focus();
				return;
			}
			
			renderCurrent(currentResp);
			renderHourly(hourlyResp);
			renderDaily(dailyResp);
			setStatus("Hoàn tất.");
		} catch (err) {
			console.error("API Error:", err);
			
			// Debug log trong catch
			console.log("Catch block - daysValue:", daysValue, "n:", n);
			
			// Thu thập tất cả lỗi (API + validation ngày)
			const allErrors = [];
			
			// Lỗi API
			if (err && err.status === 400) {
				allErrors.push("Thành phố không tồn tại.");
			} else {
				allErrors.push(err.message || "Đã xảy ra lỗi.");
			}
			
			// Lỗi validation ngày - kiểm tra số nguyên từ 1-7
			if (!daysValue || isNaN(n) || !Number.isInteger(n) || n < 1 || n > 7) {
				allErrors.push("Số ngày nhập không hợp lệ (1–7).");
			}
			
			console.log("All errors:", allErrors);
			console.log("Final error message:", allErrors.join(" "));
			setStatus(allErrors.join(" "), "error");
		} finally {
			isSearching = false;
			console.log("=== SEARCH END ===");
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


