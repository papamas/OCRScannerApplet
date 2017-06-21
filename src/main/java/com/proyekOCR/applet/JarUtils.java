package com.proyekOCR.applet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class JarUtils {
	private static String appVersion = null;
	
	/**
	 * @throws IOException 
	 * 
	 */
	public static synchronized String getAppVersion() {
		if (appVersion == null) {
			try {
				appVersion = new JarUtils().readAppVersion();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return appVersion;
	}
		
	/**
	 * 
	 */
	private String readAppVersion() throws IOException {
		URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
		String ret = new String();
		
		try {
			URL url = cl.findResource("META-INF/MANIFEST.MF");
			Manifest mf = new Manifest(url.openStream());
			Attributes atts = mf.getMainAttributes();
			String impVersion = atts.getValue("Implementation-Version");
			String impBuild = atts.getValue("Implementation-Build");
			
			if (impVersion != null)	ret = impVersion;
			if (impBuild != null) ret += " (build: " + impBuild + ")";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
}
