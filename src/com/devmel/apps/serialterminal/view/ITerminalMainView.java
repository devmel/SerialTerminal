package com.devmel.apps.serialterminal.view;

public interface ITerminalMainView {

	public void setRX(String data);
	public String getRX();
	public void setTransmitText(String text);
	public String getTransmitText();
	public void setTX(String data);
	public String getTX();
	public void setStatus(String status);
	public void setConnectButtonStatus(String name, boolean enabled);
	
	public void setListDevices(String[] list);
	public void setDeleteDeviceEnabled(boolean enabled);
	
	
	public void setConfigLock(boolean selected);
	public void setConfigBaudrate(String baudrate);
	public void setConfigBaudrate(int baudrate);
	public void setConfigMode(int mode);
	public void setConfigDatabits(int databits);
	public void setConfigStopbits(int stopbits);
	public void setConfigParity(int parity);
	public void setCR(boolean checked);
	public void setLF(boolean checked);

	public void addIPDeviceDialog();
	public void addIPDeviceDialog(int error, String name, String localIP, String password);
	public void removeDeviceConfirm(final String name);

}
