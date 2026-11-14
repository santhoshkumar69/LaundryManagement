package com.laundry.LaundryManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laundry.LaundryManagement.dto.ReturnResponse;
import com.laundry.LaundryManagement.service.WeatherService;

/** Current weather for the dashboard corner, for the signed-in outlet's city. */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("weather")
public class WeatherController {

	@Autowired
	private WeatherService weatherService;

	@GetMapping("/current")
	public ResponseEntity<Object> current(@RequestParam(value = "lat", required = false) Double latitude,
			@RequestParam(value = "lon", required = false) Double longitude) {
		return ResponseEntity.ok(weatherService.current(latitude, longitude));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> invalid(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ReturnResponse(ex.getMessage(), 400));
	}
}
