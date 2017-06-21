package com.proyekOCR.applet;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;

/**
 * JSObject documentation:
 * 
 * http://download.oracle.com/javase/6/docs/technotes/guides/plugin/developer_guide/java_js.html
 * http://www.apl.jhu.edu/~hall/java/JavaScript-from-Java.html
 * http://www.rgagnon.com/javadetails/java-0172.html
 */
@SuppressWarnings("serial")
public class Scanner extends JApplet {
	private static final Logger log = Logger.getLogger(Scanner.class.getName());
	private static ScannerManager app;
	private String token;
	private String path;
	private String url;
	private String lang;
	private Locale locale;
	private JSObject win;
	
	public Scanner() {
		super();
		ImageIO.scanForPlugins();
	}

	@Override
	public void init() {
        try {
       		
                url = getCodeBase().toString();
               	token = getParameter("token");
       		/*
                url = url.substring(0, url.length()-1);
       		url = url.substring(0, url.lastIndexOf('/'));                
       		path = getParameter("path");
       		lang = getParameter("lang");
       		*/
                locale = Util.parseLocaleString(lang);
       		Messages.init(locale);
        	
                win = JSObject.getWindow(this);        	
        } catch (JSException e) {
        	log.warning("Can't access JSObject object");
        }
        
        log.log(Level.INFO, "ocr.token => {0}", token);
    	log.log(Level.INFO, "ocr.url => {0}", url);
    	
        /*
        log.info("AppVersion: " + JarUtils.getAppVersion());
    	log.info("openkm.path => "+path);
    	log.info("openkm.lang => " + lang);
    	log.info("applet.locale => "+ locale);
    	*/

        // Create scanner instance
        app = new ScannerManager(token, url, win);
		
		try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            log.severe("createGUI didn't successfully complete");
        }
	}
	
	/**
	 * 
	 */
	private void createGUI() {
		JFrame.setDefaultLookAndFeelDecorated(false);
		JFrame main = new MainFrame(app, win);
		main.setVisible(true);
		main.setResizable(false);
		main.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
