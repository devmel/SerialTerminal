package com.devmel.apps.serialterminal.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import com.devmel.apps.serialterminal.view.ITerminalMainView;

import com.devmel.communication.IUart;
import com.devmel.communication.linkbus.Usart;
import com.devmel.communication.nativesystem.Uart;
import com.devmel.storage.IBase;
import com.devmel.storage.Node;
import com.devmel.storage.SimpleIPConfig;
import com.devmel.tools.IPAddress;

public class TerminalController {
	private final IBase baseStorage;
	private final Node devices;
	private final ITerminalMainView view;
	private String portName = null;
	private Class<?> portClass = null;

	private IUart uart = null;
	private InputStream inStream = null;

	private Thread thread;
	private int rxBytes = 0;
	private int txBytes = 0;


	private final static int defaultMode = 0;
	private final static int defaultBaudrate = 9600;
	private final static int defaultDatabits = 8;
	private final static int defaultStopbits = 1;
	private final static int defaultParity = 0;
	
	public TerminalController(IBase baseStorage, ITerminalMainView view) {
		this.baseStorage = baseStorage;
		this.devices = new Node(this.baseStorage, "Linkbus");
		this.view = view;
	}
	
	public void initialize(){
		if("LinkBus".equals(baseStorage.getString("configStart"))){
			addDeviceClick();
			baseStorage.saveString("configStart", "LB");
		}
		initializeDeviceList();

		configLockChanged(baseStorage.getInt("lock")==1 ? true : false);
		configModeChanged(baseStorage.getInt("configMode"));
		configBaudrateChanged(""+baseStorage.getInt("configBaudrate"));
		configDatabitsChanged(baseStorage.getInt("configDatabits"));
		configStopbitsChanged(baseStorage.getInt("configStopbits"));
		configParityChanged(baseStorage.getInt("configParity"));
		configCR(baseStorage.getInt("cr")==1 ? true : false);
		configLF(baseStorage.getInt("lf")==1 ? true : false);
		
		view.setTX("");
		view.setRX("");
		view.setStatus("Disconnected");
	}
	
	public void initializeDeviceList(){
		Vector<String> list = new Vector<String>();
		String[] sysDeviceList = Uart.list();
		if(sysDeviceList!=null){
		for(String devStr:sysDeviceList){
			list.add(devStr);
		}
		}
		String[] ipDeviceList = this.devices.getChildNames();
		if(ipDeviceList!=null){
		for(String devStr:ipDeviceList){
			SimpleIPConfig dev = SimpleIPConfig.createFromNode(devices, devStr);
			if(dev!=null){
				devStr = devStr+" - "+dev.getIpAsText();
				list.add(devStr);
			}
		}
		}
		String[] bList = new String[list.size()];
		list.toArray(bList);
		view.setListDevices(bList);
	}


	public void quitClick(){
		deviceUnselect();
		System.exit(0);
	}
	public void clearClick(){
		view.setTX("");
		view.setRX("");
	}
	public void connectClick(){
		if(uart==null){
			deviceSelect();
		}
		//Search config and build connection
		if(uart!=null){
			if(uart.isOpen()==false){
				try {
					uart.setParameters(baseStorage.getInt("configBaudrate"), (byte)baseStorage.getInt("configDatabits"), (byte)baseStorage.getInt("configStopbits"), (byte)baseStorage.getInt("configParity"));
					uart.open();
				} catch (IOException e) {
					view.setStatus(e.getMessage());
					deviceUnselect();
					return ;
				}
				if(uart.isOpen()==true){
					try {
						inStream = uart.getInputStream();
					} catch (Exception e) {
						e.printStackTrace();
					}
					//Start read loop
					Runnable r = new Runnable() {
						public void run() {
							try {
								while(inStream!=null && uart.isOpen()){
									try {
										int available = inStream.available();
										if(available>0){
											byte[] buffer = new byte[1024];
											int toRead = inStream.read(buffer,0,available);
											if(toRead>0){
												rxBytes += toRead;
												view.setRX(view.getRX()+new String(buffer,0,toRead));
												updateStatusBytes();
											}
										}else{
									//		Thread.sleep(100);
										}
									} catch (IOException e) {
									}
								}
							} catch (Exception e) {
	//							e.printStackTrace();
							}
						}
					};

					rxBytes=0;
					txBytes=0;
					updateStatusBytes();
					view.setConnectButtonStatus("Disconnect", true);
					execute(r);
				}
			}else{
				disconnect();
				return;
			}
		}
		if(uart==null || uart.isOpen()==false){
			view.setStatus("Device not found, please check your connection...");
			uart=null;
		}
	}
	public void sendClick(){
		if(uart!=null && uart.isOpen()==true){
			try {
				String textInput = view.getTransmitText();
				if(baseStorage.getInt("cr")==1){
					textInput += "\r";
				}
				if(baseStorage.getInt("lf")==1){
					textInput += "\n";
				}
				OutputStream out = uart.getOutputStream();
				out.write(textInput.getBytes());
				out.flush();
				txBytes += textInput.length();
				String text = view.getTX();
				if(text.length()>0){
					view.setTX(text+textInput);
				}else{
					view.setTX(textInput);
				}
				updateStatusBytes();
			} catch (Exception e) {
				disconnect();
			//	e.printStackTrace();
			}
		}else{
			disconnect();
		}
	}
	
	public void deviceSelect(final String name) {
		disconnect();
		boolean selected = false;
		if(name.contains(" - ")){
			String[] names = name.split(" - ");
			if(names!=null && names.length>0){
				String[] ipDeviceList = this.devices.getChildNames();
				for(String devStr:ipDeviceList){
					if(devStr.equals(names[0])){
						portName = devStr;
						portClass = Usart.class;
						selected=true;
						view.setDeleteDeviceEnabled(true);
						break;
					}
				}
			}
		}else{
			String[] sysDeviceList = Uart.list();
			for(String devStr:sysDeviceList){
				if(devStr.equals(name)){
					portName = devStr;
					portClass = Uart.class;
					selected=true;
					view.setDeleteDeviceEnabled(false);
					break;
				}
			}
		}
		if(selected==false){
			portName = "";
			portClass = null;
		}
	}
	public void deleteDeviceClick(final String name, final boolean confirm) {
		if (confirm == true) {
			String[] names = name.split(" - ");
			if(names!=null && names.length>0){
				String[] ipDeviceList = this.devices.getChildNames();
				for(String devStr:ipDeviceList){
					if(devStr.equals(names[0])){
						devices.removeChild(devStr);
						break;
					}
				}
			}
			initializeDeviceList();
		} else {
			view.removeDeviceConfirm(name);
		}
	}
	
	public void addDeviceClick() {
		view.addIPDeviceDialog();
	}

	public void addDeviceClick(final String name, final String localIP, final String password) {
		int err = 0;
		if (this.devices.isChildExist(name)) {
			err = -1;
		} else {
			try {
				byte[] ip = IPAddress.toBytes(localIP);
				if(name==null || name.length()==0){
					err = -2;
				}else if(ip==null){
					err = -3;
				}else if(password==null || password.length()==0){
					err = -4;
				}else{
					SimpleIPConfig device = new SimpleIPConfig(name);
					device.setIp(ip);
					device.setPassword(password);
					device.save(devices);
				}
				initializeDeviceList();
			} catch (Exception e1) {
				e1.printStackTrace();
				err = -5;
			}
		}
		if (err < 0) {
			this.view.addIPDeviceDialog(err, name, localIP, password);
		}
	}

	public void configCR(boolean selected) {
		int val = 0;
		if(selected==true){val=1;}
		view.setCR(selected);
		baseStorage.saveInt("cr", val);
	}
	public void configLF(boolean selected) {
		int val = 0;
		if(selected==true){val=1;}
		view.setLF(selected);
		baseStorage.saveInt("lf", val);
	}

	public void configLockChanged(boolean selected) {
		int lock = 0;
		if(selected==true){lock=1;}
		view.setConfigLock(selected);
		baseStorage.saveInt("lock", lock);
	}

	public void configBaudrateChanged(String text) {
		int baudrate = defaultBaudrate;
		try{baudrate = Integer.parseInt(text);}catch(Exception e){}
		if(baudrate<600 || baudrate>4000000){
			baudrate=defaultBaudrate;
		}
		view.setConfigBaudrate(baudrate);
		baseStorage.saveInt("configBaudrate", baudrate);
	}

	public void configModeChanged(int mode) {
		if(mode<128 || mode>131){
			mode = defaultMode;
		}
		view.setConfigMode(mode);
		baseStorage.saveInt("configMode", mode);
	}
	
	public void configDatabitsChanged(int databit) {
		if(databit<5 || databit>8){
			databit = defaultDatabits;
		}
		view.setConfigDatabits(databit);
		baseStorage.saveInt("configDatabits", databit);
	}

	public void configStopbitsChanged(int stopbit) {
		if(stopbit<1 || stopbit>2){
			stopbit = defaultStopbits;
		}
		view.setConfigStopbits(stopbit);
		baseStorage.saveInt("configStopbits", stopbit);
	}

	public void configParityChanged(int parity) {
		if(parity<0 || parity>2){
			parity = defaultParity;
		}
		view.setConfigParity(parity);
		baseStorage.saveInt("configParity", parity);
	}

	private void updateStatusBytes(){
		view.setStatus("RX : "+rxBytes+"  ;  TX : "+txBytes);
	}
	
	private void disconnect(){
		view.setStatus("Disconnected");
		view.setConnectButtonStatus("Connect", true);
		deviceUnselect();
	}
	
	private void deviceSelect(){
		if(portClass!=null){
			if(portClass.equals(Uart.class)){
				try {
					Uart uart = new Uart(portName);
					this.uart = uart;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(portClass.equals(Usart.class)){
				SimpleIPConfig device = SimpleIPConfig.createFromNode(devices, portName);
				if(device!=null){
					Usart uart = new Usart(device);
					uart.setLock(baseStorage.getInt("lock")==1 ? true : false);
					uart.setMode(baseStorage.getInt("configMode"));
					uart.setInterruptMode(true, 1000);
					this.uart = uart;
				}
			}
		}
	}
	private void deviceUnselect(){
		cancel();
		try {
			uart.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		uart=null;
	}
	
	private void execute(Runnable r){
		cancel();
		thread = new Thread(r);
		thread.start();
	}
	private void cancel(){
		if(thread!=null){
			thread.interrupt();
			thread = null;
		}
	}
}
