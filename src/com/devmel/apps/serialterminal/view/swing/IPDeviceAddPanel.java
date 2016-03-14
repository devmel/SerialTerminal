package com.devmel.apps.serialterminal.view.swing;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import com.devmel.apps.serialterminal.tools.SearchQRCode;
import com.devmel.apps.serialterminal.tools.SpUrlParser;
import com.devmel.tools.Hexadecimal;

public class IPDeviceAddPanel extends JPanel {
	private static final long serialVersionUID = 7591329824389405447L;
	private final String defaultName;
	private final JTextField fieldName;
	private final JTextField fieldIP;
	private final JPasswordField fieldPassword;
	private final JPanel qrCodePanel;
	private final JButton btnScanQrcodeBeside;

	/**
	 * Create the panel.
	 */
	public IPDeviceAddPanel(String error, String name, String ip, String password) {
		this.setLayout(new GridLayout(0, 1));
		defaultName = name;
		fieldName = new JTextField(name);
		fieldIP = new JTextField(ip);
		fieldPassword = new JPasswordField(password);

		if (error != null) {
			JLabel err = new JLabel(error);
			err.setForeground(new Color(255, 0, 0));
			add(err);
		}
		add(new JLabel("Name :"));
		add(fieldName);
		
		qrCodePanel = new JPanel();
		add(qrCodePanel);
		
		btnScanQrcodeBeside = new JButton("Scan QRCode below the LinkBus unit");
		qrCodePanel.add(btnScanQrcodeBeside);
		btnScanQrcodeBeside.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showWebcam();
			}
		});

		add(new JLabel("IP :"));
		add(fieldIP);
		add(new JLabel("Password :"));
		add(fieldPassword);

		add(new JLabel("   "));
		final String lb = "http://devmel.com/linkbus";
		JLabel info = new JLabel("<html><a href=\"" + lb + "\">LinkBus Informations</a></html>", SwingConstants.CENTER);
		info.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(lb));
				} catch (Exception localException) {
				}
			}
		});

		add(info);
		add(new JLabel("   "));
	}

	public String getName() {
		return fieldName.getText();
	}

	public String getIP() {
		return fieldIP.getText();
	}

	public String getPassword() {
		return new String(fieldPassword.getPassword());
	}
	
	public void showWebcam(){
		SearchQRCode search = new SearchQRCode();
		search.newScan();
		Object[] options = {"OK"};
		JOptionPane.showOptionDialog(null, search.getPanel(), "Scan QRCode", JOptionPane.NO_OPTION, JOptionPane.DEFAULT_OPTION, null, options , options[0]);
		//Fill fields
		if(search.getResult()!=null){
			SpUrlParser deviceInfo = new SpUrlParser(search.getResult());
			if(deviceInfo!=null){
				if(getName() == null || getName().equals(defaultName)){
					String ip = Hexadecimal.fromBytes(deviceInfo.getIp());
					if(ip!=null && ip.length()>6)
						fieldName.setText(defaultName+ip.substring(ip.length()-6));
				}
				fieldIP.setText(deviceInfo.getIpAsText());
				fieldPassword.setText(deviceInfo.getPassword());
			}
		}
    	search.close();
	}

}
