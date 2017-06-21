package com.gds.extractor.utils;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Normalizza il numero di cellulare
 * 
 * @author Giuseppe Di Stasi
 *
 */
public class PhoneNumberNormalizeFormat extends Format {
	private static final long serialVersionUID = -1992618451294971341L;

	// private static final String REGEX = "[^\\d.]";
	// private static final String REGEX = "[^0-9]";

	private static final String REGEX_SPACES = "[^A-Za-z0-9\\s]";
	private static final String REGEX_CHARACTER = "[^[()/-]]";

	private static final String PREFIX_ITALY = "+39";

	// private static final String[] CHARACTER_TO_REMOVE = { "(", ")", "/", "-" };

	private final List<String> prefixes;

	public PhoneNumberNormalizeFormat() {
		prefixes = new ArrayList<>();
		prefixes.add(PREFIX_ITALY);
	}

	/**
	 * 
	 * @param obj
	 * @param toAppendTo
	 * @param pos
	 * @return
	 * @throws NotNumericException
	 *             If after the normalization the value is not numeric
	 * @throws IllegalStateException
	 */
	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) throws NotNumericException, IllegalStateException {
		if (toAppendTo == null) {
			toAppendTo = new StringBuffer();
		}

		try {

			if (obj == null) {
				throw new IllegalStateException("The object value must be not null");
			}

			String number = (String) obj;

			for (String prefix : prefixes) {
				number = number.replace(prefix, "");
			}

			// for (String character : CHARACTER_TO_REMOVE) {
			// number = number.replace(character, "");
			// }

//			number = number.replaceAll(REGEX_CHARACTER, "");
//			number = number.replaceAll(REGEX_SPACES, "");
			number = number.replace(" ", "");
			number = number.replaceAll("\\s+", "");
			number = number.replaceAll("[^-\\d]","").replaceAll("(?<!^)-","");

//			if (!StringUtils.isNumeric(number)) {
//				throw new NotNumericException("The input value is not numeric '" + number + "'");
//			}

			// String number = number.replaceAll(REGEX, "");
			toAppendTo.append(number);

		} catch (ClassCastException e) {
			throw new IllegalStateException("The object type must be String");
		}

		return toAppendTo;
	}

	/**
	 * Parse object method is not supported
	 * 
	 * @return IllegalStateException
	 */
	@Override
	public Object parseObject(String source, ParsePosition pos) {
		throw new IllegalStateException("Parse object method is not supported");
	}

	/**
	 * 
	 * @param number
	 * @return
	 * @throws NotNumericException
	 *             If after the normalization the value is not numeric
	 * @throws IllegalStateException
	 */
	public String format(String number) throws NotNumericException, IllegalStateException {
		return format(number, null, null).toString();
	}

	/**
	 * The international italian prefix is already included
	 * 
	 * @param prefixes
	 *            the prefixes
	 */
	public void loadPrefixes(List<String> prefixes) {
		addPrefixes(prefixes);
	}

	/**
	 * The international italian prefix is already included
	 * 
	 * @param prefixes
	 *            the prefixes
	 */
	public void loadPrefixes(String... prefixes) {
		addPrefixes(Arrays.asList(prefixes));
	}

	private void addPrefixes(List<String> prefixes) {
		for (String prefix : prefixes) {
			this.prefixes.add(prefix.startsWith("+") ? prefix : "+" + prefix);
		}
	}

	public class NotNumericException extends RuntimeException {
		private static final long serialVersionUID = 6620502805563637189L;

		public NotNumericException() {
			super();
		}

		public NotNumericException(String message) {
			super(message);
		}

	}

	// public static void main(String[] args) {
	// final String number = "+39 348/512 0174";
	//
	// PhoneNumberNormalizeFormat format = new PhoneNumberNormalizeFormat();
	//
	// String format2 = format.format(number);
	// System.out.println(format2);
	// }

	// public static void main(String[] args) {
	// String s = "(555) 564- 8583";
	// System.out.println(s);
	// String format = new PhoneNumberNormalizeFormat().format(s);
	// System.out.println(format);
	//
	// }
}
