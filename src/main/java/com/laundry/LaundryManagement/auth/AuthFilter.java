package com.laundry.LaundryManagement.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Requires a valid session token ("Authorization: Bearer ...") on every API call except login
 * and sign-up, and sets {@link RequestScope} for the request. Most calls also need an outlet to
 * have been chosen.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AuthFilter extends OncePerRequestFilter {

	private static final List<String> PUBLIC_PATHS = List.of("/auth/login", "/registration/", "/error");
	private static final List<String> NO_OUTLET_PATHS = List.of("/auth/outlets", "/auth/selectOutlet", "/auth/me");
	private static final List<String> ADMIN_PATHS = List.of("/admin/", "/userInfo/", "/userRole/",
			"/companyProfile/update", "/outletSettings/update");
	private static final List<String> ALLOWED_ORIGINS = List.of("http://localhost:4200");

	private final AuthTokenService tokens;
	private final DeletedOutlets deletedOutlets;

	public AuthFilter(AuthTokenService tokens, DeletedOutlets deletedOutlets) {
		this.tokens = tokens;
		this.deletedOutlets = deletedOutlets;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || path.equals("/") || startsWithAny(path, PUBLIC_PATHS)) {
			chain.doFilter(request, response);
			return;
		}
		String header = request.getHeader("Authorization");
		RequestScope.Scope scope = header != null && header.startsWith("Bearer ")
				? tokens.verify(header.substring(7).trim())
				: null;
		if (scope == null) {
			reject(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Your session has expired. Please sign in again.");
			return;
		}
		if (scope.outletId() == null && !startsWithAny(path, NO_OUTLET_PATHS)) {
			reject(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Choose an outlet first.");
			return;
		}
		if (deletedOutlets.contains(scope.outletId())) {
			reject(request, response, HttpServletResponse.SC_UNAUTHORIZED,
					"This outlet has been deleted. Please sign in again and choose another outlet.");
			return;
		}
		if (!scope.isAdmin() && startsWithAny(path, ADMIN_PATHS)) {
			reject(request, response, HttpServletResponse.SC_FORBIDDEN, "Only an admin can do this.");
			return;
		}
		RequestScope.set(scope);
		try {
			chain.doFilter(request, response);
		} finally {
			RequestScope.clear();
		}
	}

	private static boolean startsWithAny(String path, List<String> prefixes) {
		return prefixes.stream().anyMatch(path::startsWith);
	}

	/** Error with CORS headers, so the browser lets the app see the status (401 sends the user to login). */
	private static void reject(HttpServletRequest request, HttpServletResponse response, int status, String message)
			throws IOException {
		String origin = request.getHeader("Origin");
		if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
			response.setHeader("Access-Control-Allow-Origin", origin);
			response.setHeader("Vary", "Origin");
		}
		response.setStatus(status);
		response.setContentType("application/json");
		response.getWriter().write("{\"message\":\"" + message.replace("\"", "'") + "\",\"statusCode\":" + status + "}");
	}
}
