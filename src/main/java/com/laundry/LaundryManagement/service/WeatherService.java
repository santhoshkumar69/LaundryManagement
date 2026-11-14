package com.laundry.LaundryManagement.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laundry.LaundryManagement.auth.RequestScope;
import com.laundry.LaundryManagement.model.OutletDetails;
import com.laundry.LaundryManagement.repository.OutletDetailsRepo;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Current weather for the dashboard, from Open-Meteo (no API key). The outlet's city and country are
 * looked up once; readings are cached per place for 15 minutes so dashboard refreshes stay cheap.
 */
@Service
public class WeatherService {

	public static final String OK = "OK";
	public static final String NO_CITY = "NO_CITY";
	public static final String CITY_NOT_FOUND = "CITY_NOT_FOUND";
	public static final String UNAVAILABLE = "UNAVAILABLE";

	private static final Duration READING_TTL = Duration.ofMinutes(15);
	private static final Duration LOOKUP_MISS_TTL = Duration.ofHours(1);

	public record WeatherView(String status, String place, Double temperature, Double feelsLike, Integer humidity,
			Double windSpeed, Integer weatherCode, String description, boolean day, Double todayMax,
			Double todayMin, Integer rainChance, String observedAt) {

		static WeatherView unavailable(String status, String place) {
			return new WeatherView(status, place, null, null, null, null, null, null, true, null, null, null, null);
		}
	}

	private record Place(String label, double latitude, double longitude) {
	}

	private record CachedPlace(Optional<Place> place, Instant expires) {
	}

	private record CachedReading(WeatherView view, Instant expires) {
	}

	@Autowired
	private OutletDetailsRepo outletDetailsRepo;

	private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Map<String, CachedPlace> places = new ConcurrentHashMap<>();
	private final Map<String, CachedReading> readings = new ConcurrentHashMap<>();

	/** Weather at the current outlet's city, or at the given coordinates when the device shares them. */
	public WeatherView current(Double latitude, Double longitude) {
		if (latitude != null && longitude != null) {
			if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
				throw new IllegalArgumentException("Location is out of range");
			}
			return reading(new Place("Your location", latitude, longitude));
		}
		OutletDetails outlet = outletDetailsRepo.findById(RequestScope.requireOutletId())
				.orElseThrow(() -> new IllegalArgumentException("Choose an outlet first"));
		String city = trim(outlet.getCity());
		if (city.isEmpty()) {
			return WeatherView.unavailable(NO_CITY, outlet.getOutletName());
		}
		Optional<Place> place;
		try {
			place = lookup(city, trim(outlet.getCountry()));
		} catch (Exception e) {
			return WeatherView.unavailable(UNAVAILABLE, city);
		}
		return place.map(this::reading).orElseGet(() -> WeatherView.unavailable(CITY_NOT_FOUND, city));
	}

	private Optional<Place> lookup(String city, String country) throws Exception {
		String key = (city + "|" + country).toLowerCase(Locale.ROOT);
		CachedPlace cached = places.get(key);
		if (cached != null && (cached.place().isPresent() || Instant.now().isBefore(cached.expires()))) {
			return cached.place();
		}
		JsonNode results = get("https://geocoding-api.open-meteo.com/v1/search?count=10&language=en&format=json&name="
				+ URLEncoder.encode(city, StandardCharsets.UTF_8)).path("results");
		JsonNode match = null;
		for (JsonNode result : results) {
			if (country.isEmpty() || country.equalsIgnoreCase(result.path("country").asString(""))
					|| country.equalsIgnoreCase(result.path("country_code").asString(""))) {
				match = result;
				break;
			}
		}
		Optional<Place> place = Optional.ofNullable(match).map(m -> new Place(city,
				m.path("latitude").asDouble(), m.path("longitude").asDouble()));
		places.put(key, new CachedPlace(place, Instant.now().plus(LOOKUP_MISS_TTL)));
		return place;
	}

	private WeatherView reading(Place place) {
		String key = String.format(Locale.ROOT, "%s|%.2f|%.2f", place.label(), place.latitude(), place.longitude());
		CachedReading cached = readings.get(key);
		if (cached != null && Instant.now().isBefore(cached.expires())) {
			return cached.view();
		}
		try {
			JsonNode body = get(String.format(Locale.ROOT, "https://api.open-meteo.com/v1/forecast?latitude=%.4f"
					+ "&longitude=%.4f&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,"
					+ "weather_code,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min,"
					+ "precipitation_probability_max&timezone=auto&forecast_days=1",
					place.latitude(), place.longitude()));
			JsonNode now = body.path("current");
			JsonNode daily = body.path("daily");
			int code = now.path("weather_code").asInt();
			WeatherView view = new WeatherView(OK, place.label(), number(now, "temperature_2m"),
					number(now, "apparent_temperature"), integer(now, "relative_humidity_2m"),
					number(now, "wind_speed_10m"), code, describe(code), now.path("is_day").asInt(1) == 1,
					number(daily.path("temperature_2m_max"), 0), number(daily.path("temperature_2m_min"), 0),
					integer(daily.path("precipitation_probability_max"), 0), now.path("time").asString(null));
			readings.put(key, new CachedReading(view, Instant.now().plus(READING_TTL)));
			return view;
		} catch (Exception e) {
			return cached != null ? cached.view() : WeatherView.unavailable(UNAVAILABLE, place.label());
		}
	}

	private JsonNode get(String url) throws Exception {
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(6)).GET().build();
		HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new IllegalStateException("Weather service returned " + response.statusCode());
		}
		return objectMapper.readTree(response.body());
	}

	private static Double number(JsonNode node, String field) {
		JsonNode value = node.path(field);
		return value.isNumber() ? value.asDouble() : null;
	}

	private static Double number(JsonNode array, int index) {
		JsonNode value = array.path(index);
		return value.isNumber() ? value.asDouble() : null;
	}

	private static Integer integer(JsonNode node, String field) {
		JsonNode value = node.path(field);
		return value.isNumber() ? value.asInt() : null;
	}

	private static Integer integer(JsonNode array, int index) {
		JsonNode value = array.path(index);
		return value.isNumber() ? value.asInt() : null;
	}

	private static String trim(String value) {
		return value == null ? "" : value.trim();
	}

	/** WMO weather interpretation codes, as used by Open-Meteo. */
	static String describe(int code) {
		return switch (code) {
		case 0 -> "Clear sky";
		case 1 -> "Mainly clear";
		case 2 -> "Partly cloudy";
		case 3 -> "Overcast";
		case 45, 48 -> "Fog";
		case 51, 53, 55 -> "Drizzle";
		case 56, 57 -> "Freezing drizzle";
		case 61, 63 -> "Rain";
		case 65 -> "Heavy rain";
		case 66, 67 -> "Freezing rain";
		case 71, 73, 75, 77 -> "Snow";
		case 80, 81 -> "Rain showers";
		case 82 -> "Heavy showers";
		case 85, 86 -> "Snow showers";
		case 95 -> "Thunderstorm";
		case 96, 99 -> "Thunderstorm with hail";
		default -> "Unknown";
		};
	}
}
