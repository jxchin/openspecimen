package com.krishagni.catissueplus.core.common.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.krishagni.catissueplus.core.administrative.domain.Institute;
import com.krishagni.catissueplus.core.administrative.domain.User;
import com.krishagni.catissueplus.core.common.domain.ConfigErrorCode;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;

public class AuthUtil {
	private static final Log logger = LogFactory.getLog(AuthUtil.class);

	private static final String OS_AUTH_TOKEN_HDR = "X-OS-API-TOKEN";

	private static final String OS_IMP_USER_HDR = "X-OS-IMPERSONATE-USER";

	public static Authentication getAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static boolean isSignedIn() {
		return getCurrentUser() != null;
	}
	
	public static User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return null;
		}
		
		return (User)auth.getPrincipal();
	}

	public static Institute getCurrentUserInstitute() {
		User user = AuthUtil.getCurrentUser();
		return (user != null) ? user.getInstitute() : null;
	}
	
	public static String getRemoteAddr() {
		UserAuthToken token = (UserAuthToken) SecurityContextHolder.getContext().getAuthentication();
		if (token == null) {
			return null;
		}

		return token.getIpAddress();
	}

	public static TimeZone getUserTimeZone() {
		UserAuthToken token = (UserAuthToken) SecurityContextHolder.getContext().getAuthentication();
		if (token == null) {
			return null;
		}

		User currUser = getCurrentUser();
		String timeZone = currUser != null && currUser.getTimeZone() != null ? currUser.getTimeZone() : token.getTimeZone();
		return toTimeZone(timeZone);
	}

	public static void setCurrentUser(User user) {
		setCurrentUser(user, null, null);
	}

	public static void setCurrentUser(User user, String authToken, HttpServletRequest httpReq) {
		UserAuthToken token = new UserAuthToken(user, authToken, user.getAuthorities());
		if (httpReq != null) {
			token.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpReq));
			token.setTimeZone(httpReq.getHeader("X-OS-CLIENT-TZ"));
			token.setIpAddress(Utility.getRemoteAddress(httpReq));
		}

		SecurityContextHolder.getContext().setAuthentication(token);
	}

	public static void clearCurrentUser() {
		SecurityContextHolder.clearContext();
	}
	
	public static boolean isAdmin() {
		return getCurrentUser() != null && getCurrentUser().isAdmin();
	}
	
	public static boolean isInstituteAdmin() {
		return getCurrentUser() != null && getCurrentUser().isInstituteAdmin();
	}
	
	public static String encodeToken(String token) {
		if (token == null) {
			return null;
		}

		return new String(Base64.encode(token.getBytes()));
	}
	
	public static String decodeToken(String token) {
		return new String(Base64.decode(token.getBytes()));
	}
	
	public static String getTokenFromCookie(HttpServletRequest httpReq) {
		return getCookieValue(httpReq, "osAuthToken");
	}

	public static void setTokenCookie(HttpServletRequest httpReq, HttpServletResponse httpResp, String authToken) {
		String cookieValue = "osAuthToken=" + (authToken != null ? authToken : "") + ";";
		cookieValue += "Path=" + getContextPath(httpReq) + ";";
		cookieValue += "HttpOnly;";
		cookieValue += "SameSite=Strict;";
		if (httpReq.isSecure()) {
			cookieValue += "secure;";
		}

		if (authToken == null) {
			cookieValue += "Max-Age=0;";
		}

		httpResp.setHeader("Set-Cookie", cookieValue);
	}

	public static void clearTokenCookie(HttpServletRequest httpReq, HttpServletResponse httpResp) {
		setTokenCookie(httpReq, httpResp, null);
	}

	public static void resetTokenCookie(HttpServletRequest httpReq, HttpServletResponse httpResp) {
		setTokenCookie(httpReq, httpResp, getAuthTokenFromHeader(httpReq));
	}

	public static String getAuthTokenFromHeader(HttpServletRequest httpReq) {
		return httpReq.getHeader(OS_AUTH_TOKEN_HDR);
	}

	public static String getImpersonateUser(HttpServletRequest httpReq) {
		String impUserString = httpReq.getHeader(OS_IMP_USER_HDR);
		if (StringUtils.isNotBlank(impUserString)) {
			return impUserString;
		}

		return getCookieValue(httpReq, "osImpersonateUser");
	}

	private static String getContextPath(HttpServletRequest httpReq) {
		String path = ConfigUtil.getInstance().getAppUrl();
		if (StringUtils.isBlank(path)) {
			path = httpReq.getContextPath();
		} else {
			try {
				path = new URL(path).getPath();
			} catch (MalformedURLException url) {
				throw OpenSpecimenException.userError(ConfigErrorCode.INVALID_SETTING_VALUE, path);
			}
		}

		if (StringUtils.isBlank(path)) {
			path = "/";
		}

		return path;
	}

	private static String getCookieValue(HttpServletRequest httpReq, String cookieName) {
		String cookieHdr = httpReq.getHeader("Cookie");
		if (StringUtils.isBlank(cookieHdr)) {
			return null;
		}

		String value = null;
		String[] cookies = cookieHdr.split(";");
		for (String cookie : cookies) {
			if (!cookie.trim().startsWith(cookieName)) {
				continue;
			}

			String[] parts = cookie.trim().split("=");
			if (parts.length == 2) {
				try {
					value = URLDecoder.decode(parts[1], "utf-8");
					if (value.startsWith("%") || Utility.isQuoted(value)) {
						value = value.substring(1, value.length() - 1);
					}
				} catch (Exception e) {
					logger.error("Error obtaining " + cookieName + " from cookie", e);
				}
				break;
			}
		}

		return value;
	}

	private static TimeZone toTimeZone(String timeZone) {
		if (StringUtils.isBlank(timeZone)) {
			return null;
		}

		try {
			return TimeZone.getTimeZone(timeZone);
		} catch (Exception e) {
			logger.error("Error obtaining time zone information for: " + timeZone, e);
		}

		return null;
	}

	private static class UserAuthToken extends UsernamePasswordAuthenticationToken {
		private String timeZone;

		private String ipAddress;

		public UserAuthToken(Object principal, Object credentials) {
			super(principal, credentials);
		}

		public UserAuthToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
			super(principal, credentials, authorities);
		}

		public String getTimeZone() {
			return timeZone;
		}

		public void setTimeZone(String timeZone) {
			this.timeZone = timeZone;
		}

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}
	}
}