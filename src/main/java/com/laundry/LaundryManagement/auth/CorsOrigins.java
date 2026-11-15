package com.laundry.LaundryManagement.auth;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Browser origins allowed to call the API (from laundry.cors.origins). */
@Component
public class CorsOrigins {

	private final List<String> allowed;

	public CorsOrigins(@Value("${laundry.cors.origins:http://localhost:4200}") String origins) {
		this.allowed = Arrays.stream(origins.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
	}

	public List<String> allowed() {
		return allowed;
	}

	public boolean allows(String origin) {
		return origin != null && allowed.contains(origin);
	}
}
