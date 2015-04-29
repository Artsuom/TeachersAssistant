package com.artsuo.teachersassistant.backend;

public class Utils {

	public static String multiLineToSingleLine(String multiLine) {
		String lines[] = multiLine.split("\\r?\\n");
		if (lines.length > 1) {
			String returnString = "";
			for (int i = 0; i < lines.length; i++) {
				if (i < lines.length - 1) {
					returnString += lines[i];
					returnString +=	Constants.NEWLINE_REPLACEMENT;
				} else {
					returnString += lines[i];
				}
			}
			return returnString;
		}
		return multiLine;
	}
	
	public static String singleLineToMultiLine(String singleLine) {
		String lines[] = singleLine.split(Constants.NEWLINE_REPLACEMENT);
		if (lines.length > 1) {
			String returnString = "";
			for (int i = 0; i < lines.length; i++) {
				if (i < lines.length - 1) {
					returnString += lines[i];
					returnString +=	System.getProperty("line.separator");
				} else {
					returnString += lines[i];
				}
			}
			return returnString;
		}
		return singleLine;
	}
}
