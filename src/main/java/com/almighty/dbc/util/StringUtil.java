package com.almighty.dbc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author trungnt
 *
 */
public class StringUtil {
	/*Escape Sequence	Character Represented by Sequence
	\0	An ASCII NUL (X'00') character
	\'	A single quote (') character
	\"	A double quote (") character
	\b	A backspace character
	\n	A newline (linefeed) character
	\r	A carriage return character
	\t	A tab character
	\Z	ASCII 26 (Control+Z); see note following the table
	\\	A backslash (\) character
	\%	A % character; see note following the table
	\_	A _ character; see note following the table*/
	public static String escapeApostrophe(String txt) {
		if (txt != null) {
			txt = txt.replace("'", "\\'");
		}

		return txt;
	}

	public static List<String> split(String s, String regex) {
		String[] _tmp = s.split(regex);
		return Arrays.asList(_tmp);
	}

	public static List<Integer> split(String s, String regex, int defaultValue) {
		List<String> _tmp = split(s, regex);
		List<Integer> result = new ArrayList<Integer>();
		for (String value : _tmp) {
			try {
				int intValue = Integer.parseInt(value);
				result.add(intValue);
			} catch (Exception e) {
				result.add(defaultValue);
			}
		}

		return result;
	}

}
