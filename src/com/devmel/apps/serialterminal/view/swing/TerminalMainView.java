package com.devmel.apps.serialterminal.view.swing;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.JRadioButton;

import com.devmel.apps.serialterminal.controller.TerminalController;
import com.devmel.apps.serialterminal.view.ITerminalMainView;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class TerminalMainView extends JFrame implements ITerminalMainView {
	private static final long serialVersionUID = -7179983133387624247L;
	private TerminalController controller;
	private JComboBox deviceSelect;
	private JCheckBox chckbxLock;
	private JButton btnAdd;
	private JButton btnDelete;
	private JPanel modePanel;
	private JRadioButton rdbtnAsynchronous;
	private JPanel syncMasterPanel;
	private JPanel syncSlavePanel;
	private JRadioButton rdbtnSyncMasterRising;
	private JRadioButton rdbtnSyncMasterFalling;
	private JRadioButton rdbtnSyncSlaveRising;
	private JRadioButton rdbtnSyncSlaveFalling;
	private JTextField baudrate;
	private JLabel statusLabel;
	private JRadioButton databit5;
	private JRadioButton databit6;
	private JRadioButton databit7;
	private JRadioButton databit8;
	private JRadioButton stopbit1;
	private JRadioButton stopbit2;
	private JRadioButton parityNone;
	private JRadioButton parityOdd;
	private JRadioButton parityEven;
	private JTextArea rxTextPane;
	private JTextArea txTextPane;
	private JFormattedTextField transmitText;
	private JCheckBox chckbxcr;
	private JCheckBox chckbxlf;
	private JButton connectButton;

	/**
	 * Create the panel.
	 */
	public TerminalMainView(){
		this.setTitle("Serial Terminal");
		this.setBounds(100, 100, 700, 600);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel menuPanel = new JPanel();
		getContentPane().add(menuPanel, BorderLayout.NORTH);
		menuPanel.setPreferredSize(new Dimension(this.getWidth(), 205));
		GridBagLayout gbl_menuPanel = new GridBagLayout();
		gbl_menuPanel.columnWidths = new int[]{100, 0, 0};
		gbl_menuPanel.rowHeights = new int[]{100, 0};
		gbl_menuPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_menuPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		menuPanel.setLayout(gbl_menuPanel);
		
		JPanel btnPanel = new JPanel();
		GridBagConstraints gbc_btnPanel = new GridBagConstraints();
		gbc_btnPanel.insets = new Insets(0, 0, 0, 5);
		gbc_btnPanel.fill = GridBagConstraints.BOTH;
		gbc_btnPanel.gridx = 0;
		gbc_btnPanel.gridy = 0;
		menuPanel.add(btnPanel, gbc_btnPanel);
		btnPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.quitClick();
				}
			}
		});
		btnPanel.add(btnQuit);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.clearClick();
				}
			}
		});
		btnPanel.add(btnClear);
		
		connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.connectClick();
				}
			}
		});
		btnPanel.add(connectButton);
		
		JPanel comPanel = new JPanel();
		GridBagConstraints gbc_comPanel = new GridBagConstraints();
		gbc_comPanel.fill = GridBagConstraints.BOTH;
		gbc_comPanel.gridx = 1;
		gbc_comPanel.gridy = 0;
		menuPanel.add(comPanel, gbc_comPanel);
		comPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel portPanel = new JPanel();
		portPanel.setPreferredSize(new Dimension(this.getWidth(), 55));
		portPanel.setBorder(new TitledBorder("Port COM"));
		comPanel.add(portPanel, BorderLayout.NORTH);
		
		deviceSelect = new JComboBox();
		deviceSelect.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		deviceSelect.setToolTipText((String) null);
		deviceSelect.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				final String itemName = e.getItem().toString();
				if(deviceSelect.getSelectedItem()!=null && (deviceSelect.getSelectedItem().toString()).equals(itemName)){
					if(controller!=null){
						controller.deviceSelect(itemName);
					}
				}
			}
		});

		portPanel.add(deviceSelect);

		chckbxLock = new JCheckBox("Lock");
		chckbxLock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.configLockChanged(chckbxLock.isSelected());
				}
			}
		});
		portPanel.add(chckbxLock);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.addDeviceClick();
				}
			}
		});
		portPanel.add(btnAdd);
		
		btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.deleteDeviceClick(deviceSelect.getSelectedItem().toString(), false);
				}
			}
		});
		portPanel.add(btnDelete);
		
		
		
		final ButtonGroup modeGroup = new ButtonGroup();
	    class ModeActionListener implements ActionListener {
			public void actionPerformed(ActionEvent ev) {
				String choice = modeGroup.getSelection().getActionCommand();
				if(controller!=null){
					controller.configModeChanged(Integer.parseInt(choice));
				}
			}
	    }
	    ActionListener modeListen = new ModeActionListener();

		modePanel = new JPanel();

		modePanel.setBorder(new TitledBorder("Mode"));
		modePanel.setLayout(new GridLayout(1, 3));
		
		rdbtnAsynchronous = new JRadioButton("Asynchronous");
		rdbtnAsynchronous.setActionCommand("0");
		rdbtnAsynchronous.addActionListener(modeListen);
		modeGroup.add(rdbtnAsynchronous);
		modePanel.add(rdbtnAsynchronous);
		
		syncMasterPanel = new JPanel();
		syncMasterPanel.setBorder(new TitledBorder("Synchronous Master"));
		
		rdbtnSyncMasterRising = new JRadioButton("Rising");
		rdbtnSyncMasterRising.setActionCommand("130");
		rdbtnSyncMasterRising.addActionListener(modeListen);
		modeGroup.add(rdbtnSyncMasterRising);
		syncMasterPanel.add(rdbtnSyncMasterRising);

		rdbtnSyncMasterFalling = new JRadioButton("Falling");
		rdbtnSyncMasterFalling.setActionCommand("131");
		rdbtnSyncMasterFalling.addActionListener(modeListen);
		modeGroup.add(rdbtnSyncMasterFalling);
		syncMasterPanel.add(rdbtnSyncMasterFalling);
		modePanel.add(syncMasterPanel);

		syncSlavePanel = new JPanel();
		syncSlavePanel.setBorder(new TitledBorder("Synchronous Slave"));
		rdbtnSyncSlaveRising = new JRadioButton("Rising");
		rdbtnSyncSlaveRising.setActionCommand("128");
		rdbtnSyncSlaveRising.addActionListener(modeListen);
		modeGroup.add(rdbtnSyncSlaveRising);
		syncSlavePanel.add(rdbtnSyncSlaveRising);

		rdbtnSyncSlaveFalling = new JRadioButton("Falling");
		rdbtnSyncSlaveFalling.setActionCommand("129");
		rdbtnSyncSlaveFalling.addActionListener(modeListen);
		modeGroup.add(rdbtnSyncSlaveFalling);
		syncSlavePanel.add(rdbtnSyncSlaveFalling);
		modePanel.add(syncSlavePanel);
		
		comPanel.add(modePanel, BorderLayout.CENTER);
		
		
		
		JPanel paramPanel = new JPanel();
		paramPanel.setPreferredSize(new Dimension(this.getWidth(), 80));
		comPanel.add(paramPanel, BorderLayout.SOUTH);
		paramPanel.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel baudratePanel = new JPanel();
		baudrate = new JTextField();
		baudrate.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(controller!=null){
					controller.configBaudrateChanged(baudrate.getText());
				}
			}
		});
		baudratePanel.add(baudrate);
		baudrate.setColumns(10);
		baudratePanel.setBorder(new TitledBorder("Baud Rate"));
		paramPanel.add(baudratePanel);
		
		
		
		JPanel databitsPanel = new JPanel();
		final ButtonGroup databitsGroup = new ButtonGroup();
	    class DatabitsActionListener implements ActionListener {
			public void actionPerformed(ActionEvent ev) {
				String choice = databitsGroup.getSelection().getActionCommand();
				if(controller!=null){
					controller.configDatabitsChanged(Integer.parseInt(choice));
				}
			}
	    }
	    ActionListener databitsListen = new DatabitsActionListener();
	    
		databitsPanel.setBorder(new TitledBorder("Data bits"));
		paramPanel.add(databitsPanel);
		databit5 = new JRadioButton("5");
		databit5.setActionCommand("5");
		databit5.addActionListener(databitsListen);
		databitsGroup.add(databit5);
		databitsPanel.add(databit5);
		
		databit6 = new JRadioButton("6");
		databit6.setActionCommand("6");
		databit6.addActionListener(databitsListen);
		databitsGroup.add(databit6);
		databitsPanel.add(databit6);

		databit7 = new JRadioButton("7");
		databit7.setActionCommand("7");
		databit7.addActionListener(databitsListen);
		databitsGroup.add(databit7);
		databitsPanel.add(databit7);
		
		databit8 = new JRadioButton("8");
		databit8.setActionCommand("8");
		databit8.addActionListener(databitsListen);
		databitsGroup.add(databit8);
		databitsPanel.add(databit8);
		
		JPanel stopbitsPanel = new JPanel();
		final ButtonGroup stopbitsGroup = new ButtonGroup();
		stopbitsPanel.setBorder(new TitledBorder("Stop bits"));
		paramPanel.add(stopbitsPanel);
	    class StopbitsActionListener implements ActionListener {
			public void actionPerformed(ActionEvent ev) {
				String choice = stopbitsGroup.getSelection().getActionCommand();
				if(controller!=null){
					controller.configStopbitsChanged(Integer.parseInt(choice));
				}
			}
	    }
	    ActionListener stopbitsListen = new StopbitsActionListener();
		
		stopbit1 = new JRadioButton("1");
		stopbit1.setActionCommand("1");
		stopbit1.addActionListener(stopbitsListen);
		stopbitsGroup.add(stopbit1);
		stopbitsPanel.add(stopbit1);
		
		stopbit2 = new JRadioButton("2");
		stopbit2.setActionCommand("2");
		stopbit2.addActionListener(stopbitsListen);
		stopbitsGroup.add(stopbit2);
		stopbitsPanel.add(stopbit2);
		
		JPanel parityPanel = new JPanel();
		final ButtonGroup parityGroup = new ButtonGroup();
		parityPanel.setBorder(new TitledBorder("Parity"));
		paramPanel.add(parityPanel);
		
	    class ParityActionListener implements ActionListener {
			public void actionPerformed(ActionEvent ev) {
				String choice = parityGroup.getSelection().getActionCommand();
				if(controller!=null){
					controller.configParityChanged(Integer.parseInt(choice));
				}
			}
	    }
	    ActionListener parityListen = new ParityActionListener();
		
		parityNone = new JRadioButton("none");
		parityNone.setActionCommand("0");
		parityNone.addActionListener(parityListen);
		parityGroup.add(parityNone);
		parityPanel.add(parityNone);
		
		parityOdd = new JRadioButton("odd");
		parityOdd.setActionCommand("1");
		parityOdd.addActionListener(parityListen);
		parityGroup.add(parityOdd);
		parityPanel.add(parityOdd);
		
		parityEven = new JRadioButton("even");
		parityEven.setActionCommand("2");
		parityEven.addActionListener(parityListen);
		parityGroup.add(parityEven);
		parityPanel.add(parityEven);
		
		
		
		JPanel dataPanel = new JPanel();
		getContentPane().add(dataPanel, BorderLayout.CENTER);
		dataPanel.setLayout(new GridLayout(2, 0, 0, 0));
		
		JPanel receivePanel = new JPanel();
	    receivePanel.setBorder(new TitledBorder("Receive"));
		dataPanel.add(receivePanel);
		receivePanel.setLayout(new BorderLayout(0, 0));
		rxTextPane=new JTextArea();
		rxTextPane.setBackground(Color.LIGHT_GRAY);
		rxTextPane.setEditable(false);
		JScrollPane rxScrollPane=new JScrollPane();
		rxScrollPane.setViewportView(rxTextPane);
		DefaultCaret caret = (DefaultCaret)rxTextPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		receivePanel.add(rxScrollPane, BorderLayout.CENTER);

		
		JPanel transmitPanel = new JPanel();
		transmitPanel.setBorder(new TitledBorder("Transmit"));
		dataPanel.add(transmitPanel);
		transmitPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel sendPanel = new JPanel();
		transmitPanel.add(sendPanel, BorderLayout.NORTH);
		sendPanel.setLayout(new BorderLayout(0, 0));
		
		transmitText = new JFormattedTextField();
		transmitText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.sendClick();
				}
			}
		});
		sendPanel.add(transmitText);
		
		JPanel sendOptPanel = new JPanel();
		sendPanel.add(sendOptPanel, BorderLayout.EAST);
		
		chckbxcr = new JCheckBox("+CR");
		chckbxcr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.configCR(chckbxcr.isSelected());
				}
			}
		});
		sendOptPanel.add(chckbxcr);
		
		chckbxlf = new JCheckBox("+LF");
		chckbxlf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.configLF(chckbxlf.isSelected());
				}
			}
		});
		sendOptPanel.add(chckbxlf);
		
		JButton sendButton = new JButton("Send");
		sendOptPanel.add(sendButton);
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.sendClick();
				}
			}
		});
		
		
		txTextPane=new JTextArea();
		txTextPane.setBackground(Color.LIGHT_GRAY);
		txTextPane.setEditable(false);
		JScrollPane txScrollPane=new JScrollPane();
		txScrollPane.setViewportView(txTextPane);
		transmitPanel.add(txScrollPane, BorderLayout.CENTER);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		getContentPane().add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(this.getWidth(), 20));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel = new JLabel();
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
		
	}

	public void setController(TerminalController controller) {
		this.controller = controller;
	}
	
	public void setRX(String data){
		rxTextPane.setText(data);
	}
	public String getRX(){
		return rxTextPane.getText();
	}
	public void setTransmitText(String text){
		transmitText.setText(text);
	}
	public String getTransmitText(){
		return transmitText.getText();
	}
	public void setTX(String data){
		txTextPane.setText(data);
	}
	public String getTX(){
		return txTextPane.getText();
	}

	public void setStatus(String status){
		this.statusLabel.setText(status);
	}
	public void setConnectButtonStatus(String name, boolean enabled){
		connectButton.setText(name);
		connectButton.setEnabled(enabled);
	}
	
	@Override
	public void setListDevices(String[] list) {
		deviceSelect.removeAllItems();
//		deviceSelect.addItem("");
		if(list!=null){
			for (String item : list) {
				deviceSelect.addItem(item);
			}
		}
		this.repaint();
	}
	
	@Override
	public void setDeleteDeviceEnabled(boolean enabled){
		btnDelete.setEnabled(enabled);
	}

	@Override
	public void setConfigLock(boolean lock){
		this.chckbxLock.setSelected(lock);
	}
	@Override
	public void setConfigMode(int mode) {
		if(mode==130){
			rdbtnSyncMasterRising.setSelected(true);
		}else if(mode==131){
			rdbtnSyncMasterFalling.setSelected(true);
		}else if(mode==128){
			rdbtnSyncSlaveRising.setSelected(true);
		}else if(mode==129){
			rdbtnSyncSlaveFalling.setSelected(true);
		}else{
			rdbtnAsynchronous.setSelected(true);
		}
	}
	@Override
	public void setConfigBaudrate(String baudrate){
		this.baudrate.setText(baudrate);
	}
	@Override
	public void setConfigBaudrate(int baudrate){
		this.baudrate.setText(""+baudrate);
	}
	@Override
	public void setConfigDatabits(int databits){
		if(databits==5){
			databit5.setSelected(true);
		}else if(databits==6){
			databit6.setSelected(true);
		}else if(databits==7){
			databit7.setSelected(true);
		}else if(databits==8){
			databit8.setSelected(true);
		}
	}
	@Override
	public void setConfigStopbits(int stopbits){
		if(stopbits==1){
			stopbit1.setSelected(true);
		}else if(stopbits==2){
			stopbit2.setSelected(true);
		}
	}
	@Override
	public void setConfigParity(int parity){
		if(parity==0){
			parityNone.setSelected(true);
		}else if(parity==1){
			parityOdd.setSelected(true);
		}else if(parity==2){
			parityEven.setSelected(true);
		}
	}
	@Override
	public void setCR(boolean checked) {
		chckbxcr.setSelected(checked);
	}
	@Override
	public void setLF(boolean checked) {
		chckbxlf.setSelected(checked);
	}

	@Override
	public void addIPDeviceDialog() {
		addIPDeviceDialog(0 , "LinkBus_", "fe80::dcf6:e5ff:fe", null);
	}

	@Override
	public void addIPDeviceDialog(int error, String name, String ip, String password) {
		String err = null;
		if(error==-1){
			err = "This name already exist...";
		}else if(error==-2){
			err = "This name is invalid...";
		}else if(error==-3){
			err = "This IP address is invalid...";
		}else if(error==-4){
			err = "This password is invalid...";
		}else if(error==-5){
			err = "An unknown error occurred...";
		}
		IPDeviceAddPanel panel = new IPDeviceAddPanel(err,name,ip,password);
        int ret = JOptionPane.showConfirmDialog(null, panel, "Add LinkBus Connection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ret == JOptionPane.OK_OPTION) {
			if(controller!=null){
				controller.addDeviceClick(panel.getName(), panel.getIP(), panel.getPassword());
			}
        }
	}
	
	@Override
	public void removeDeviceConfirm(final String name) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int ret = JOptionPane.showConfirmDialog (null, "Are you sure you want to delete "+name+" ?","Confirm",dialogButton);
		if(ret==JOptionPane.OK_OPTION){
			controller.deleteDeviceClick(name, true);
		}
	}

}
