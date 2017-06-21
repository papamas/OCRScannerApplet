package com.proyekOCR.jnlp.scanner;

import com.proyekOCR.applet.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
import uk.co.mmscomputing.device.scanner.ScannerListener;

public class ScannerManager implements ScannerListener {
	private static Logger log = Logger.getLogger(ScannerManager.class.getName());
	private String token;
	private String path;
	private String url;
	private JSObject win;
	private Scanner scanner;
	private String fileName;
	private String fileType;
	private boolean ui;
	private JButton bScan,bInterface,bSelect;
	private JTextField tfFileName;
        private String [] parts ;
        private String nip;
        private String dok;
	/*
        private JComboBox cbFileType;
	private JCheckBox cbUI;*/
	private List<BufferedImage> images;

	/**
	 * @param token
	 * @param win
	 */
	/*
        public ScannerManager(String token, String url, JSObject win) {
		log.info("########## ScannerManager ##########");
		this.token = token;
		this.url = url;
		this.win = win;
		scanner = Scanner.getDevice();                
		scanner.addListener(this);           
		images = new ArrayList<BufferedImage>();
	}
        */

        ScannerManager() {
            scanner = Scanner.getDevice();                
            scanner.addListener(this);           
            images = new ArrayList<BufferedImage>();             
        }

	/**
	 *
	 */
        
	public Scanner getDevice() {
            return scanner;
	}
        
	/**
	 *
	 */
	public void acquire(String fileName, String fileType, boolean ui, JButton bScan, JButton bInterface,JButton bSelect,JTextField tfFileName
			) throws ScannerIOException {
		log.fine("########## acquire ########## " + fileName + " -> " + fileType);
		
                this.bScan = bScan;
                this.bInterface = bInterface;
                this.bSelect = bSelect;
		this.tfFileName = tfFileName;
                
		this.fileName = fileName;
		this.fileType = fileType;
		this.ui = ui;
		bScan.setEnabled(false);
                bInterface.setEnabled(false);
		bSelect.setEnabled(false);
                tfFileName.setEnabled(false);
		
                scanner.acquire();
	}
        
        public  String getFileName(){
            
            return fileName;                                    
        }
        
        public void setFileName(String fileName){
            this.fileName = fileName;
        }

        /*
	public void setPath(String path) {
		this.path = path;
	}
        
        public String getPath() {
		return path;
                
	}
        
        public String getToken(){
            return token;
        }
           
        public void setToken(String token){
            this.token = token;
        }
        
        
             
        public String getUrl(){
            return url;
        }
        
        public void setUrl(String url){
            this.url = url;
        }
        
        */

	@Override
	public void update(Type type, ScannerIOMetadata metadata) {
		if (type.equals(ScannerIOMetadata.ACQUIRED)) {
			log.info("***** ACQUIRED *****");
			images.add(metadata.getImage());
		} else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
			log.info("***** STATECHANGE: " + metadata.getStateStr() + " *****");
                                       
			if (metadata.getLastState() == 7 && metadata.getState() == 5) {
				try {
					String filename = this.getFileName();
                
                                        String response = Util.createDocument(token, path, filename, fileType, url, images);
                                       
                                        Gson gson = new Gson();
                                        JsonObject  jobj = gson.fromJson(response, JsonObject.class);
                                        System.out.println("Filename  Response : " + jobj.get("fileName").getAsString());
                                        
                                        System.out.println("File Size Respone : " + jobj.get("fileSize").getAsLong());
                                        
                                        /*
                                        String result = jobj.getAsString();
                                        
                                        if (result.trim().length() != 0) {
						log.log(Level.SEVERE, "Error: " + response);
						ErrorCode.displayError(result,  filename + "." + fileType);
					}                                    
                                        */
					
					images.clear();
					//win.call("jsWizardFix",new Object[] {path + "/" + filename + "." + fileType,response});
                                        
				} catch (JSException e) {
					log.log(Level.WARNING, "JSException: " + e.getMessage(), e);
					
					// TODO Investigate why occurs but js method is executed
					if (!"JavaScript error while calling \"refreshFolderFix\"".equals(e.getMessage())) {
						JOptionPane.showMessageDialog(bScan.getParent(), e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
					}
				//} catch (IOException e) {
				//	log.log(Level.SEVERE, "IOException: " + e.getMessage(), e);
				//	JOptionPane.showMessageDialog(bScan.getParent(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Throwable e) { // Catch java.lang.OutOfMemeoryException
					log.log(Level.SEVERE, "Throwable: " + e.getMessage(), e);
					JOptionPane.showMessageDialog(bScan.getParent(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			if (metadata.isFinished()) {
				bScan.setEnabled(true);
                                bInterface.setEnabled(true);
                                bSelect.setEnabled(true);
				tfFileName.setEnabled(true);
				//cbFileType.setEnabled(true);
				//cbUI.setEnabled(true);
			}
		} else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
			log.info("***** NEGOTIATE *****");
			ScannerDevice device = metadata.getDevice();

			try {
				device.setShowUserInterface(ui);
				device.setShowProgressBar(true);
                                //device.setResolution(300);
                                
				// 
				// device.setOption("mode", "Color");
				// device.setOption("br-x", 215);
				// device.setOption("br-y", 297.0);

				// SaneDevice sd = (SaneDevice) device;
				// FileOutputStream fos = new FileOutputStream("scanner.txt");
				// OptionDescriptor[] od = sd.getOptionDescriptors();

				// for (int o=0; o<od.length; o++) {
				// Descriptor d = (Descriptor)od[o];
				// System.out.println("- "+d.getName());
				// fos.write(d.toString().getBytes());
				// fos.write("\n\n----------------\n".getBytes());
				// }
				// fos.close();

			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				JOptionPane.showMessageDialog(bScan.getParent(), e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
			log.log(Level.SEVERE, metadata.getException().getMessage(), metadata.getException());
			JOptionPane.showMessageDialog(bScan.getParent(), metadata.getException(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			log.finer("update(" + type + ", " + metadata + ")");
		}
	}
}
