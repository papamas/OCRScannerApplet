package com.proyekOCR.applet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class Util {
	private static final Logger log = Logger.getLogger(Util.class.getName());
    
	/**
	 * Upload scanned document to OpenKM
	 * 
     * @param token
     * @param path
     * @param fileName
     * @param images
     * @param url
     * @throws java.net.MalformedURLException
     * @throws java.io.IOException
	 */
	public static String createDocument(String token,String fileType,
			String url, List<BufferedImage> images) throws MalformedURLException, IOException {
		
            log.info("createDocument(" + token  +   fileType + url + ", " + images + ")");
		String response = null;
		
                if (null != fileType) switch (fileType) {
                    case "pdf":
                        log.info("create pdf file");
                        //ImageUtils.writePdf(images, ios);
                        break;
                    case "tif":
                        log.info("create tiff file");
                        response = ImageUtils.writeTiff(images,fileType,url,token);
                        break;
                    default:
                        log.info("create jpg or png file");
                        response = ImageUtils.writeJpeg(images,fileType,url,token);
                        break;
                }                
                return response;
	}
	
	/**
	 * Creates a temporal and unique directory
	 * 
	 * @throws IOException If something fails.
	 */
	public static File createTempDir() throws IOException {
		File tmpFile = File.createTempFile("okm", null);		
                log.info("create temp dir "  + tmpFile.getName());
		if (!tmpFile.delete())
            throw new IOException();
        if (!tmpFile.mkdir())
            throw new IOException();
        
        return tmpFile;       
	}
	
	/**
	 * 
	 */
	public static Locale parseLocaleString(String localeString) {
		if (localeString == null) {
			localeString = "en-GB";
		}
		 
		String[] parts = localeString.split("-");
		String language = (parts.length > 0 ? parts[0] : "");
		String country = (parts.length > 1 ? parts[1] : "");
		return (language.length() > 0 ? new Locale(language, country) : null);
	 }
	
	/**
	 * Copy file
	 */
	private static void copyFile(File fromFile, File toFile) throws IOException {
		FileInputStream from = null;
	    FileOutputStream to = null;
	    
	    try {
	    	from = new FileInputStream(fromFile);
	    	to = new FileOutputStream(toFile);
	    	byte[] buffer = new byte[4096];
	    	int bytesRead;
	    	
	    	while ((bytesRead = from.read(buffer)) != -1) {
	            to.write(buffer, 0, bytesRead);
	    	}
	    } finally {
	    	if (from != null) {
	    		try {
	    			from.close();
	    		} catch (IOException e) {}
	    	}
	    	
	    	if (to != null) {
	    		try {
	    			to.close();
	    		} catch (IOException e) {}
	    	}
	    }
	}
}
