/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyekOCR.applet;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 *
 * @author Nur Muhamad
 */
public class HttpBasicAuth {
    
    public static void main(String[] args) {
          
        try {
           
            String encoding = Base64.getEncoder().encodeToString(("nurmuhamadh:nurmuhamadh").getBytes());
            String home = System.getProperty("user.home");
            
            File file = new File(home,"skpengalihan.jpg");
            HttpClient client = new DefaultHttpClient();
            MultipartEntity form = new MultipartEntity();
            
            HttpPost post = new HttpPost("http://localhost:8080/proyekOCR/api/fileupload");
	    post.addHeader("accept", "application/json");
            post.addHeader("Authorization", "Basic " + encoding);
            form.addPart("file", new FileBody(file));  
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
            String responseBody = responseHandler.handleResponse(response);
            System.out.println(statusCode);
            System.out.println(responseBody);
                       
            client.getConnectionManager().shutdown();

	} catch (ClientProtocolException e) {
	    e.printStackTrace();
        } catch (IOException e) {
	    e.printStackTrace();
        }
         
            
    }
    
    
}
