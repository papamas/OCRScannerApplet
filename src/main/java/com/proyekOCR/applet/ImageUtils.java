package com.proyekOCR.applet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import java.util.HashMap;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import uk.co.mmscomputing.imageio.pdf.PDFImageWriter;
import uk.co.mmscomputing.imageio.tiff.TIFFImageWriter;
import org.apache.pdfbox.util.ImageIOUtil;

public class ImageUtils {
    private static final Logger log = Logger.getLogger(ImageUtils.class.getName());
    	
	/**
	 * PDF Writer
	 */
	public static void writePdf(List<BufferedImage> images, ImageOutputStream ios) throws IOException {
		PDFImageWriter writer = (PDFImageWriter) ImageIO.getImageWritersByFormatName("pdf").next();
		writer.setOutput(ios);
		writer.prepareWriteSequence(null);
		
		for (int i=0; i < images.size(); i++) {
	             writer.writeToSequence(images.get(i));
		}
		
		writer.endWriteSequence();
	}
	
	/**
	 * TIFF Writer
     * @param images
     * @throws java.io.IOException
	 */
	public static String  writeTiff(List<BufferedImage> images,                
                String fileType,String url, String token) throws IOException {		
            
            HashMap<String,String> ResponseMap= new HashMap<>();
            TIFFImageWriter writer = (TIFFImageWriter) ImageIO.getImageWritersByFormatName("tif").next();
	    String fileName = setfileName();
            File tmpDir = createTempDir();            
            File tmpFile = new File(tmpDir, fileName + "." + fileType);
	        
            OutputStream os = new FileOutputStream(tmpFile);
            writer.setOutput(os);
	    writer.prepareWriteSequence(null);
            
            for (BufferedImage image : images) {
                writer.writeToSequence(new IIOImage(image,null,null),null);            
            }   
                
            writer.endWriteSequence();
            ((ImageOutputStream)writer.getOutput()).close();
            return ResponseMap.toString();
	}
        
        public static String setfileName(){
            
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString();
            return fileName;
        }
        /**
	 * TIFF Writer
        * @param images
        * @param fileType
        * @param url
        * @param token
        * @return 
        * @throws java.io.IOException
	 */
	public static String writeJpeg(List<BufferedImage> images,                
                String fileType,String url, String token) throws IOException {
               
            HashMap<String,String> ResponseMap= new HashMap<>();
                 
            for (BufferedImage image : images) {
	
                String fileName = setfileName();
                File tmpDir = createTempDir();            
                File tmpFile = new File(tmpDir, fileName + "." + fileType);
	        OutputStream os = new FileOutputStream(tmpFile);
                ImageIOUtil.writeImage(image, "jpg", os, 300);
                ResponseMap = filePost(url, token,tmpFile);
	    }
            
            return ResponseMap.toString();
	}
        
        public static File createTempDir() throws IOException {
		File tmpFile = File.createTempFile("okm", null);		
                log.log(Level.INFO, "create temp dir {0}", tmpFile.getName());
		if (!tmpFile.delete())
            throw new IOException();
        if (!tmpFile.mkdir())
            throw new IOException();
        
        return tmpFile;       
	}
        
        public static HashMap<String,String> filePost(String url, String token, File tmpFile) throws IOException{
            
            log.info("stating sending file via http client");
            HashMap<String,String> ResponseMap= new HashMap<>();
            
            CloseableHttpClient client = HttpClientBuilder.create().build();       
            MultipartEntity form = new MultipartEntity();

            HttpPost post = new HttpPost(url + "fileupload;JSESSIONID="+token);
            log.info("url : "+ url + "fileupload;JSESSIONID="+token);
            post.addHeader("Cookie", "JSESSIONID="+ token);
            post.addHeader("accept", "application/json");
            form.addPart("file", new FileBody(tmpFile));  
            post.setEntity(form);

            CloseableHttpResponse response = client.execute(post);
            
            int statusCode = response.getStatusLine().getStatusCode();            
            String  responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
            client.getConnectionManager().shutdown();
            log.info(responseBody);
            
            ResponseMap.put("HTTP Status", String.valueOf(statusCode));
            ResponseMap.put("responseBody",responseBody);
            
            
            return ResponseMap;
        }
}
