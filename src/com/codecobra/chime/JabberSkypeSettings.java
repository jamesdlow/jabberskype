package com.codecobra.chime;

import javax.swing.JFrame;

import com.jameslow.*;

public class JabberSkypeSettings extends Settings {
	private boolean launchskype = true;
	private boolean startserver = true;
	private boolean debug = false;
	private String hostname = "localhost";
	private String password = "";
	private int port = 5222;
	private WindowSettings ws;
	private static final String MAIN_WINDOW = JabberSkypeWindow.class.getName();
	
	public JabberSkypeSettings() {}
	public void loadSettings() {
		ws = getWindowSettings(MAIN_WINDOW, 310,295,0,0,true,JFrame.NORMAL);
		//setShowWindow(ws.getVisible());
		//setLaunchSkype(getSetting("LaunchSkype",launchskype));
		launchskype = getSetting("LaunchSkype",launchskype);
		//setStartServer(getSetting("StartServer",startserver));
		startserver = getSetting("StartServer",startserver);
		//setDebug(getSetting("Debug",debug));
		debug = getSetting("Debug",debug);
		//setHostname(getSetting("Hostname",hostname));
		hostname = getSetting("Hostname",hostname);
		//setPassword(getSetting("Password",password));
		password = getSetting("Password",password);
		port = getSetting("Port",port);
	}
	public WindowSettings getMainWindowSettings() {
		return ws;
	}
	public boolean getShowWindow() {
		return ws.getVisible();
	}
	public boolean getLaunchSkype() {
		return launchskype;
	}
	public boolean getStartServer() {
		return startserver;
	}
	public boolean getDebug() {
		return debug;
	}
	public String getHostname() {
		return hostname;
	}
	public String getPassword() {
		return password;
	}
	public int getPort() {
		return port;
	}
	
	public void setShowWindow(boolean showwindow) {
		ws = getWindowSettings(MAIN_WINDOW);
		ws.setVisible(showwindow);
        saveSettings();
	}
	public void setLaunchSkype(boolean launchskype) {
		this.launchskype = launchskype;
		setSetting("LaunchSkype",launchskype);
        saveSettings();
	}
	public void setStartServer(boolean startserver) {
		this.startserver = startserver;
		setSetting("StartServer",startserver);
        saveSettings();
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
		setSetting("Debug",debug);
        saveSettings();
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
		setSetting("Hostname",hostname);
        saveSettings();
	}
	public void setPassword(String password) {
		this.password = password;
		setSetting("Password",password);
        saveSettings();
	}
	public void setPort(int port) {
		this.port = port;
		setSetting("Port",port);
        saveSettings();
	}
}