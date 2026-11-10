package com.laundry.LaundryManagement.auth;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Signed, self-contained session tokens (HMAC-SHA256), so sessions survive backend restarts and
 * the browser can call the API directly without cookies.
 */
@Component
public class AuthTokenService {

	private static final Duration LIFETIME = Duration.ofHours(12);
	private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

	private final byte[] secret;

	public AuthTokenService(@Value("${laundry.auth.secret}") String secret) {
		if (secret == null || secret.length() < 32) {
			throw new IllegalStateException("laundry.auth.secret must be at least 32 characters");
		}
		this.secret = secret.getBytes(StandardCharsets.UTF_8);
	}

	public String issue(RequestScope.Scope scope) {
		long expires = Instant.now().plus(LIFETIME).getEpochSecond();
		String payload = String.join("|",
				String.valueOf(scope.userDbId()),
				String.valueOf(scope.loginId()),
				String.valueOf(scope.tenantId()),
				scope.outletId() == null ? "" : String.valueOf(scope.outletId()),
				encode(scope.role() == null ? "" : scope.role()),
				encode(scope.userName() == null ? "" : scope.userName()),
				String.valueOf(expires));
		String body = encode(payload);
		return body + "." + sign(body);
	}

	/** Returns the scope in a valid, unexpired token, or null. */
	public RequestScope.Scope verify(String token) {
		if (token == null) {
			return null;
		}
		int dot = token.indexOf('.');
		if (dot <= 0) {
			return null;
		}
		String body = token.substring(0, dot);
		byte[] expected = sign(body).getBytes(StandardCharsets.US_ASCII);
		byte[] given = token.substring(dot + 1).getBytes(StandardCharsets.US_ASCII);
		if (!MessageDigest.isEqual(expected, given)) {
			return null;
		}
		try {
			String[] parts = new String(DECODER.decode(body), StandardCharsets.UTF_8).split("\\|", -1);
			if (parts.length != 7 || Long.parseLong(parts[6]) < Instant.now().getEpochSecond()) {
				return null;
			}
			return new RequestScope.Scope(
					Integer.valueOf(parts[0]),
					parts[1],
					decode(parts[5]),
					decode(parts[4]),
					Integer.valueOf(parts[2]),
					parts[3].isEmpty() ? null : Integer.valueOf(parts[3]),
					false);
		} catch (RuntimeException ex) {
			return null;
		}
	}

	private String sign(String body) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secret, "HmacSHA256"));
			return ENCODER.encodeToString(mac.doFinal(body.getBytes(StandardCharsets.US_ASCII)));
		} catch (GeneralSecurityException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static String encode(String value) {
		return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}

	private static String decode(String value) {
		return new String(DECODER.decode(value), StandardCharsets.UTF_8);
	}
}
