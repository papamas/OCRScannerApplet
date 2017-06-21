package com.proyekOCR.applet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
import uk.co.mmscomputing.device.scanner.ScannerListener;

public class ScannerManager implements ScannerListener {
	private static final Logger log = Logger.getLogger(ScannerManager.class.getName());
	private String token;
	private String url;
	private final JSObject win;
	private final Scanner scanner;
	private String fileType;
	private boolean ui;
	private JButton bScan,bInterface,bSelect;
	private JComboBox cbFileType;
	private final List<BufferedImage> images;

	/**
	 * @param token
         * @param url
	 * @param win
	 */
	public ScannerManager(String token, String url, JSObject win) {
		log.info("########## ScannerManager ##########");
		this.token = token;
		this.url = url;
		this.win = win;
		scanner = Scanner.getDevice();                
		scanner.addListener(this);           
		images = new ArrayList<>();
	}

	/**
	 *
     * @return 
	 */
	public Scanner getDevice() {
            return scanner;
	}

	/**
	 *
     * @param fileType
     * @param ui
     * @param bScan
     * @param bSelect
     * @param bInterface
     * @param tfFileName
     * @param cbFileType
     * @throws uk.co.mmscomputing.device.scanner.ScannerIOException
	 */
	public void acquire(String fileType, boolean ui,JButton bScan,
                JButton bInterface,JButton bSelect,JComboBox cbFileType,JTextField tfFileName
	    ) throws ScannerIOException {
		log.log(Level.FINE, "########## acquire ########## {0} -> {1}", new Object[]{fileType});
		
                this.bScan = bScan;
                this.bInterface = bInterface;
                this.bSelect = bSelect;
                this.cbFileType = cbFileType;
		this.fileType = fileType;
		this.ui = ui;
                
		bScan.setEnabled(false);
                bInterface.setEnabled(false);
		bSelect.setEnabled(false);
		cbFileType.setEnabled(false);
                tfFileName.setEditable(false);
                scanner.acquire();
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

	@Override
	public void update(Type type, ScannerIOMetadata metadata) {
		if (type.equals(ScannerIOMetadata.ACQUIRED)) {
			log.info("***** ACQUIRED *****");
                        log.info("ACQUIRED(" + token  + fileType +
				", " + url + ", " + images + ")");               
			images.add(metadata.getImage());
		} else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
			log.log(Level.INFO, "***** STATECHANGE: {0} *****", metadata.getStateStr());
                                       
			if (metadata.getLastState() == 7 && metadata.getState() == 5) {
				try {
                                        String url      = this.getUrl();
                                        String token    = this.getToken();
                                        String fileType = this.fileType;
                  
                                        String response = Util.createDocument(token,fileType.toLowerCase(), url, images);
                                        
                                        log.log(Level.INFO, "Response Create Document : {0}", response);
					images.clear();
				        
				} catch (JSException e) {
					log.log(Level.WARNING, "JSException: " + e.getMessage(), e);
					
					// TODO Investigate why occurs but js method is executed
                                        /*
					if (!"JavaScript error while calling \"refreshFolderFix\"".equals(e.getMessage())) {
						JOptionPane.showMessageDialog(bScan.getParent(), e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
					}*/
				} catch (IOException e) {
					log.log(Level.SEVERE, "IOException: " + e.getMessage(), e);
					JOptionPane.showMessageDialog(bScan.getParent(), "IOException: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Throwable e) { // Catch java.lang.OutOfMemeoryException
					log.log(Level.SEVERE, "Throwable: " + e.getMessage(), e);
					JOptionPane.showMessageDialog(bScan.getParent(), "Throwable: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			if (metadata.isFinished()) {
				bScan.setEnabled(true);
                                bInterface.setEnabled(true);
                                bSelect.setEnabled(true);
				cbFileType.setEnabled(true);
			}
		} else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
			log.info("***** NEGOTIATE *****");
			ScannerDevice device = metadata.getDevice();

			try {
				device.setShowUserInterface(ui);
				device.setShowProgressBar(true);
                                //device.setResolution(100.0);
                                //device.setOption("mode", "Color");
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
			log.log(Level.FINER, "update({0}, {1})", new Object[]{type, metadata});
		}
	}
}
