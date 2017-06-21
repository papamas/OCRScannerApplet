/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyekOCR.jnlp.scanner;

import com.proyekOCR.applet.Messages;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import netscape.javascript.JSObject;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import com.proyekOCR.jnlp.scanner.ScannerManager;
/**
 *
 * @author Nur Muhamad
 */
public class MainFrame extends JFrame implements ActionListener, WindowListener {
	private static Logger log = Logger.getLogger(MainFrame.class.getName());
	private static final long serialVersionUID = 1L;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JButton bScan,bInterface,bSelect;
	private JComboBox cbFileType;
	private JTextField tfFileName;
	private JCheckBox cbUI;
	private final ScannerManager scanner;
	private final JSObject win;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Messages.init(Locale.getDefault());
				MainFrame inst = new MainFrame(null, null);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
				inst.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
	}

	/**
	 *
	 */
	public MainFrame(ScannerManager scanner, JSObject win) {
		super("Scan & Upload");
		initGUI();
		addWindowListener(this);

		// Set instances
		this.scanner = new ScannerManager();
		this.win = win;

		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// Determine the new location of the window
		int w = this.getSize().width;
		int h = this.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		// Move the window
		this.setLocation(x, y);
                
	}

	/**
	 * 
	 */
	private void initGUI() {
		try {
			getContentPane().setLayout(null);

			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1.setText(Messages.get("file.tmt"));
			jLabel1.setBounds(19, 19, 105, 15);
			
			
			tfFileName = new JTextField();
			getContentPane().add(tfFileName);
			tfFileName.setBounds(125, 15, 275, 22);
			
                        bScan = new JButton();
			getContentPane().add(bScan);
			bScan.setText(Messages.get("scan.upload"));
			bScan.setBounds(20, 84, 125, 22);
			bScan.addActionListener(this);
                        
                        bInterface = new JButton();
			getContentPane().add(bInterface);
			bInterface.setText(Messages.get("show.interface"));
			bInterface.setBounds(150, 84, 125, 22);
			bInterface.addActionListener(this);
			
                        bSelect = new JButton();
			getContentPane().add(bSelect);
			bSelect.setText(Messages.get("select.scan"));
			bSelect.setBounds(280, 84, 125, 22);
			bSelect.addActionListener(this);
			
			
			pack();
			this.setSize(440, 159);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
            
            String fileName = "test";
            String fileType = "jpg";
            
            try{
                if(evt.getSource()==bScan){
                  scanner.acquire(fileName, fileType.toLowerCase(), false, bScan,bInterface,bSelect, tfFileName);
                }else if(evt.getSource()==bSelect){
                  scanner.getDevice().select();
                }else if(evt.getSource()==bInterface){
                    scanner.acquire(fileName, fileType.toLowerCase(), true, bScan,bInterface,bSelect, tfFileName);
                }
              }catch(ScannerIOException se){
                  
                se.printStackTrace();
            }               
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	
        }

	@Override
	public void windowClosed(WindowEvent arg0) {
		log.info("windowClosed: calling 'destroyScannerApplet'");
		if (win != null) {
			win.call("destroyScannerAppletFix", new Object[] {});
		} else {
			JOptionPane.showMessageDialog(null, "destroyScannerApplet", "JavaScript call",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}

