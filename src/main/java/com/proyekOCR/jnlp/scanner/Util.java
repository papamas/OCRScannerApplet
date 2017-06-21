package com.proyekOCR.jnlp.scanner;

import com.proyekOCR.applet.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class Util {
	private static Logger log = Logger.getLogger(Util.class.getName());
    
	/**
	 * Upload scanned document to OpenKM
	 * 
	 */
	public static String createDocument(String token, String path, String fileName, String fileType,
			String url, List<BufferedImage> images) throws MalformedURLException, IOException {
		log.info("createDocument(" + token + ", " + path + ", " + fileName + ", " + fileType +
				", " + url + ", " + images + ")");
		
                File tmpDir = createTempDir();
		File tmpFile = new File(tmpDir, fileName + "." + fileType);
		ImageOutputStream ios = ImageIO.createImageOutputStream(tmpFile);
		String responseBody = null;
		
		try {
			if ("pdf".equals(fileType)) {
                            log.info("create pdf file");
                            
			    ImageUtils.writePdf(images, ios);
			} else if ("tif".equals(fileType)) {
			    log.info("create tiff file");
                           // ImageUtils.writeTiff(images, ios);
			} else {
                            log.info("create jpg or png file");
                            
			    if (!ImageIO.write(images.get(0), fileType, ios)) {
				throw new IOException("Not appropiated writer found!");
			    }
			}
			
			ios.flush();
			ios.close();
                        
                        log.info("stating sending file via http client");
                        HttpClient client = new DefaultHttpClient();     
                        MultipartEntity form = new MultipartEntity();
            
                        HttpPost post = new HttpPost("http://localhost:8080/proyekOCR/api/fileupload");
                        post.addHeader("accept", "application/json");
                        form.addPart("file", new FileBody(tmpFile));  
                        post.setEntity(form);

                        HttpResponse response = client.execute(post);
                        // Status Code
                        int statusCode = response.getStatusLine().getStatusCode();

                        if (statusCode != 200) {
                            throw new RuntimeException("Failed : HTTP error code : "
                            + statusCode);
                        }

                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        // Response Body
                        responseBody = responseHandler.handleResponse(response);
                        log.info("Server Response Status Code " + statusCode);
                        log.info("Response body message " + responseBody.toString());
                        client.getConnectionManager().shutdown();

		} finally {
			FileUtils.deleteQuietly(tmpDir);
		}
                
                
		log.info("finish createDocument: "+responseBody.toString());
		return responseBody;
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
