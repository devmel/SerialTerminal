package com.devmel.apps;


import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.devmel.apps.serialterminal.controller.TerminalController;
import com.devmel.apps.serialterminal.view.swing.TerminalMainView;
import com.devmel.storage.java.UserPrefs;

public class SerialTerminal {
	private TerminalMainView mainView;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
//		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				//Load Ressources
				java.net.URL icon = null;
				try {
					icon = getClass().getResource("/res/icon_app_32x32.png");
				} catch (Exception e) {
				}
				try {
					SerialTerminal window = new SerialTerminal();
					if(icon!=null){
						ImageIcon devmelIcon = new ImageIcon(icon);
						window.mainView.setIconImage(devmelIcon.getImage());
					}
					window.mainView.setLocationRelativeTo(null);
					window.mainView.setVisible(true);
				} catch (Throwable e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SerialTerminal() {
		//Build Model
		UserPrefs userPrefs = new UserPrefs(null);
//		UserPrefs userPrefs = new UserPrefs(Preferences.userRoot().node(SerialTerminal.class.getName()));
//		userPrefs.clearAll();
		//Splash screen
		if(userPrefs.getString("configStart") == null){
			String[] options = {"Computer", "LinkBus"};
			int ret = JOptionPane.showOptionDialog(null, "Please select a device Port.", "Port select", JOptionPane.NO_OPTION, JOptionPane.DEFAULT_OPTION, null, options , options[0]);
			if(ret < options.length){
				userPrefs.saveString("configStart", options[ret]);
			}
		}
		//Start controller
		mainView = new TerminalMainView();
		final TerminalController controller = new TerminalController(userPrefs, mainView);
		mainView.setController(controller);
		controller.initialize();
		mainView.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				controller.quitClick();
			}
		});
		
	}
}
