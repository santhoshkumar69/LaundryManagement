package com.laundry.LaundryManagement.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/** Central CORS for every controller; replaces per-controller @CrossOrigin(localhost:4200). */
@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter(CorsOrigins origins) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(origins.allowed());
		config.setAllowCredentials(true);
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
