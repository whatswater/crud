package com.zandero.utils.extra;

import com.zandero.utils.StringUtils;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.net.URI;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class ValidatingUtils {

	// if any of these are present ... check ... if it is a regular expression
	private static final String REG_EX_CHARS = "'[{'\\^$.|?*+(";

	private static final Pattern IP_V4_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");


	private ValidatingUtils() {
		// hiding constructor
	}

	/**
	 * Checks if given email is a valid email address
	 *
	 * @param email to check
	 * @return true if email, false if not
	 */
	public static boolean isEmail(String email) {

		return !StringUtils.isNullOrEmptyTrimmed(email) &&
		       EmailValidator.getInstance().isValid(email.trim().toLowerCase());
	}

	/**
	 * Checks if given string is a URL
	 *
	 * @param value to check
	 * @return true if URL, false otherwise
	 */
	public static boolean isUrl(String value) {

		return !StringUtils.isNullOrEmptyTrimmed(value) &&
		       UrlValidator.getInstance().isValid(value);
	}

	/**
	 * Checks valus is a regular IPv4 address
	 * @param value to check
	 * @return true if IPv4, false otherwise
	 */
	public static boolean isIPv4(String value) {

		return !StringUtils.isNullOrEmptyTrimmed(value) &&
		       IP_V4_PATTERN.matcher(value.trim()).matches();
	}

	public static boolean isDomain(String value) {

		value = StringUtils.trimToNull(value);
		if (value == null) {
			return false;
		}

		value = value.toLowerCase();
		if (isUrl(value)) {
			return true;
		}

		return !StringUtils.isNullOrEmptyTrimmed(value) &&
			   DomainValidator.getInstance().isValid(value);
	}

	/**
	 * Utility to find out if given string is a regular expression
	 * Strings without special regular expression characters are not considered regular expressions,
	 * but they technically are
	 *
	 * @param value to check if regular expression
	 * @return true if regular expression, false otherwise
	 */
	public static boolean isRegEx(String value) {

		if (StringUtils.isNullOrEmptyTrimmed(value)) {
			return false;
		}

		if (value.chars().noneMatch(ch -> REG_EX_CHARS.indexOf(ch) > 0)) {
			return false;
		}

		try {
			Pattern.compile(value);
			return true;
		}
		catch (PatternSyntaxException e) {
			return false;
		}
	}
}