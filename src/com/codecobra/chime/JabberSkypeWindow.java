package com.codecobra.chime;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.jameslow.*;

public class JabberSkypeWindow extends MainWindow {
	private JabberSkypeSettings settings;
	private JTabbedPane tabs = new JTabbedPane();
	private JPanel settingspanel, debugpanel;
	private JCheckBox showwindow = new JCheckBox("Show Window");
	private JCheckBox launchskype = new JCheckBox("Launch Skype Automatically");
	private JCheckBox startserver = new JCheckBox("Start Server On Launch");
	private JCheckBox showdebug = new JCheckBox("Show Debug");
	private JLabel hostnamelabel = new JLabel("Allowed Hostname");
	private JLabel portlabel = new JLabel("Local Port");
	private JLabel passwordlabel = new JLabel("Password");
	private JTextField hostname = new JTextField();
	private JTextField port = new JTextField();
	private JPasswordField password = new JPasswordField();
	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");
	private JScrollPane debugscroll;
	private JTextConsole debug;
	private static final int MAX_PORT = 65534;
	
	public JabberSkypeWindow() {
		super();
		settings = (JabberSkypeSettings) JabberSkype.Settings();
		Dimension size;
		Insets insets = this.getInsets();
			this.add(tabs);
			settingspanel = new JPanel(null);
			settingspanel.setName("Settings");
			tabs.add(settingspanel);
					size = showwindow.getPreferredSize();
					showwindow.setBounds(25 + insets.left, 5 + insets.top,
							size.width, size.height);
					showwindow.setSelected(settings.getShowWindow());
					showwindow.addActionListener(new ActionListener() {
						 public void actionPerformed(ActionEvent e) {
							 settings.setShowWindow(showwindow.isSelected());
						 }
					});
				settingspanel.add(showwindow);
					size = launchskype.getPreferredSize();
					launchskype.setBounds(25 + insets.left, 30 + insets.top,
							size.width, size.height);
					launchskype.setSelected(settings.getLaunchSkype());
					launchskype.addActionListener(new ActionListener() {
						 public void actionPerformed(ActionEvent e) {
							 settings.setShowWindow(launchskype.isSelected());
						 }
					});
				settingspanel.add(launchskype);
					size = startserver.getPreferredSize();
					startserver.setBounds(25 + insets.left, 55 + insets.top,
						size.width, size.height);
					startserver.setSelected(settings.getStartServer());
					startserver.addActionListener(new ActionListener() {
						 public void actionPerformed(ActionEvent e) {
							 settings.setShowWindow(startserver.isSelected());
						 }
					});
				settingspanel.add(startserver);
					size = showdebug.getPreferredSize();
					showdebug.setBounds(25 + insets.left, 80 + insets.top,
						size.width, size.height);
					showdebug.setSelected(settings.getDebug());
					showdebug.addActionListener(new ActionListener() {
						 public void actionPerformed(ActionEvent e) {
							 settings.setDebug(showdebug.isSelected());
							 toggleDebug();
						 }
					});
				settingspanel.add(showdebug);
					size = hostnamelabel.getPreferredSize();
					hostnamelabel.setBounds(150 + insets.left - size.width, 120 + insets.top,
						size.width, size.height);
				settingspanel.add(hostnamelabel);
					size = hostname.getPreferredSize();
					hostname.setBounds(160 + insets.left, 120 + insets.top,
						100, size.height);
					hostname.setText(settings.getHostname());
					hostname.addKeyListener(new KeyListener() {
						public void keyPressed(KeyEvent e) {}
			          	public void keyReleased(KeyEvent e) {}
			          	public void keyTyped(KeyEvent e) {
			          		settings.setHostname(hostname.getText() + e.getKeyChar() );
			          	}
					});
				settingspanel.add(hostname);
					size = portlabel.getPreferredSize();
					portlabel.setBounds(150 + insets.left - size.width, 145 + insets.top,
						size.width, size.height);
				settingspanel.add(portlabel);
					size = port.getPreferredSize();
					port.setBounds(160 + insets.left, 145 + insets.top,
						100, size.height);
					port.setText(""+settings.getPort());
					port.addKeyListener(new KeyListener() {
						private String lastport = port.getText();
						private boolean accept = false;
						public void keyPressed(KeyEvent e) {
							if (e.getKeyCode() >= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9) {
								if (Integer.parseInt(port.getText() + e.getKeyChar()) <= MAX_PORT) {
									accept = true;
								} else {
									accept = false;
								}
							} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
								accept = true;
							} else {
								accept = false;
							}
						}
			          	public void keyReleased(KeyEvent e) {}
			          	public void keyTyped(KeyEvent e) {
			          		if (accept) {
			          			lastport = port.getText();
			          			settings.setHostname(port.getText());
			          		} else {
			          			port.setText(lastport);
			          		}
			          	}
					});
				settingspanel.add(port);
					size = passwordlabel.getPreferredSize();
					passwordlabel.setBounds(150 + insets.left - size.width, 170 + insets.top,
						size.width, size.height);
				settingspanel.add(passwordlabel);
					size = password.getPreferredSize();
					password.setBounds(160 + insets.left, 170 + insets.top,
						100, size.height);
				password.setText(settings.getPassword());
					password.addKeyListener(new KeyListener() {
						public void keyPressed(KeyEvent e) {}
			          	public void keyReleased(KeyEvent e) {}
			          	public void keyTyped(KeyEvent e) {
			          		settings.setPassword(password.getPassword().toString() + e.getKeyChar());
			          	}
					});
				settingspanel.add(password);
					size = start.getPreferredSize();
					start.setBounds(50 + insets.left, 195 + insets.top,
						100, size.height);
					start.addActionListener(new ActionListener() {
						 public void actionPerformed(ActionEvent e) {
							 JabberSkype.start();
						 }
					});
				settingspanel.add(start);
					size = stop.getPreferredSize();
					stop.setBounds(150 + insets.left, 195 + insets.top,
						100, size.height);
					stop.addActionListener(new ActionListener() {
						 public void actionPerformed(ActionEvent e) {
							 JabberSkype.stop();
						 }
					});
				settingspanel.add(stop);
	}
	public WindowSettings getDefaultWindowSettings() {
		/*
		if (!((JabberSkypeSettings) settings).getShowWindow() && !os.addQuit()) {
			window.setVisible(false);
		}
		*/
		return ((JabberSkypeSettings) Main.Settings()).getMainWindowSettings();
	}
	public void toggleDebug() {
		if(settings.getDebug()) {
			if(debugpanel == null) {
				createDebug();
			}
			tabs.add(debugpanel);
			debug.direct();
		} else {
			tabs.remove(debugpanel);
			debug.restore();
		}
	}
	private void createDebug() {
		debugpanel = new JPanel(new BorderLayout());
		debugpanel.setName("Debug");		
		try {
			debug = new JTextConsole();
			debugscroll = new JScrollPane(debug);
			debugpanel.add(debugscroll);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}