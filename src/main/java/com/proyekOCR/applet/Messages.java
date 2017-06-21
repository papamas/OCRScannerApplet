package com.proyekOCR.applet;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "com.openkm.applet.lang.Resources";
	private static ResourceBundle resource = null;
	
	private Messages() {
	}
	
	public static void init(Locale locale) {
		if (resource == null) {
			resource = ResourceBundle.getBundle(BUNDLE_NAME, locale);
			System.out.println("init: "+resource.toString()+" --- "+locale);
		}
	}
	
	public static String get(String key) {
		return resource.getString(key);
	}
}
