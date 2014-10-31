package com.codecobra.chime;

import com.jameslow.*;

public class JabberSkype extends Main {
	private static Server server;
	
	public JabberSkype(String args[]) {
		super(args,null,null,JabberSkypeSettings.class.getName(),JabberSkypeWindow.class.getName(),null,null,null);
		pref = window;

		((JabberSkypeSettings) settings).setDebug(cmd.getDebug());
		try {
			server=new Server(this);
			server.setServerName(os.appName());
			if (((JabberSkypeSettings) settings).getStartServer()) {
				Thread thread = new Thread() {
					public void run() {
						server.start();
			        }
				};
				thread.start();
			}
		} catch (Exception e) {
			Main.Logger().severe("Could not start server.");
		}
	}
	public static void start() {
		server.start();
	}
	public static void stop() {
		server.stop();
	}
	public static void main(String args[]) {
		instance = new JabberSkype(args);
	}
}