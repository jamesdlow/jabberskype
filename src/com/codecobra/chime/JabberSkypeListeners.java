package com.codecobra.chime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.skype.*;
import com.skype.User.Status;
import com.skype.connector.AbstractConnectorListener;
import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.ConnectorListener;
import com.skype.connector.ConnectorMessageEvent;

public class JabberSkypeListeners {
	public final static String JABBER_AWAY = "away";
	public final static String JABBER_DND = "dnd";
	public final static String JABBER_XA = "xa";
	public final static String JABBER_CHAT = "chat";
	public final static String JABBER_ONLINE = "online";
	
	public final static String SKYPE_UNKNOWN = "UNKNOWN";
	public final static String SKYPE_OFFLINE = "OFFLINE";
	public final static String SKYPE_ONLINE = "ONLINE";
	public final static String SKYPE_AWAY = "AWAY";
	public final static String SKYPE_NA = "NA";
	public final static String SKYPE_DND = "DND";
	public final static String SKYPE_SKYPEOUT = "SKYPEOUT";
	public final static String SKYPE_SKYPEME = "SKYPEME";
	
	private static JabberSkypeListener listener;
	
	public static void enableListeners(Server server) {
		if (listener == null) {
	        try {
	        	//TODO: Not using for now, doesn't work on Mac, and doesn't allow us to ansewr calls and things.
	        	//setProperty("SILENT_MODE","ON");
	        	listener = new JabberSkypeListener(server);
	        	Skype.addChatMessageListener(listener);
	        	addUserStatusListener(listener);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
	}
	public static void disableListeners() {
		if (listener != null) {
	        try {
	        	setProperty("SILENT_MODE","OFF");
	        	Skype.removeChatMessageListener(listener);
	        	removeUserStatusListener(listener);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        //d0f1f452c95458f41b7653c65eda94b81e784d37.gif
	        //901c2d1d32bfd4192f97a7d2158c1446632b166d.jpg
	        //43de6c43bd4f7b5f17ae8b0dc330ea7e9d479f75.png
		}
	}
	
	public static String getStatus(String status) {
		String result = null;
		if (status.compareTo(SKYPE_AWAY) == 0) {
			result = JABBER_AWAY;
		}  else if (status.compareTo(SKYPE_DND) == 0) {
			result = JABBER_DND;
		}  else if (status.compareTo(SKYPE_NA) == 0) {
			result = JABBER_XA;
		}  else if (status.compareTo(SKYPE_SKYPEME) == 0) {
			result = JABBER_CHAT;
		}  else if (status.compareTo(SKYPE_ONLINE) == 0) {
			result = JABBER_ONLINE;
		} else {
			//status.UNKNOWN;
			//status.SKYPEOUT;
			//status.OFFLINE;
			//status.OLINE;
		}
		return result;
	}
	public static String getStatus(Status status) {
		String result = null;
		if (status.compareTo(status.AWAY) == 0) {
			result = JABBER_AWAY;
		}  else if (status.compareTo(status.DND) == 0) {
			result = JABBER_DND;
		}  else if (status.compareTo(status.NA) == 0) {
			result = JABBER_XA;
		}  else if (status.compareTo(status.SKYPEME) == 0) {
			result = JABBER_CHAT;
		}  else if (status.compareTo(status.ONLINE) == 0) {
			result = JABBER_ONLINE;
		} else {
			//status.UNKNOWN;
			//status.SKYPEOUT;
			//status.OFFLINE;
			//status.OLINE;
		}
		return result;
	}
	public static String removeSuffix(String user) {
		int pos = user.lastIndexOf("@");
		if (pos > -1) {
			return user.substring(0, pos);
		} else {
			return user;
		}
	}

    /** chatMessageListener lock. */
    private static Object userStatusListenerMutex = new Object();
    /** CHATMESSAGE listener. */
    private static ConnectorListener userStatusListener;
    /** Collection of listeners. */	
    private static List<UserStatusListener> userStatusListeners = Collections.synchronizedList(new ArrayList<UserStatusListener>());
	
	/**
     * Add a listener for USERSTATUS events received from the Skype API.
     * @see UserStatusListener
     * @param listener the Listener to add.
     * @throws SkypeException when connection has gone bad or ERROR reply.
     */
    public static void addUserStatusListener(UserStatusListener listener) throws SkypeException {
        //Utils.checkNotNull("listener", listener);
        synchronized (userStatusListenerMutex) {
        	userStatusListeners.add(listener);
            if (userStatusListener == null) {
            	userStatusListener = new AbstractConnectorListener() {
                    public void messageReceived(ConnectorMessageEvent event) {
                        String message = event.getMessage();
                        if (message.startsWith("USER ")) {
                            String data = message.substring("USER ".length());
                            String id = data.substring(0, data.indexOf(' '));
                            String propertyNameAndValue = data.substring(data.indexOf(' ') + 1);
                            String propertyName = propertyNameAndValue.substring(0, propertyNameAndValue.indexOf(' '));
                            //DISPLAYNAME
                            if ("ONLINESTATUS".equals(propertyName)) {
                                String propertyValue = propertyNameAndValue.substring(propertyNameAndValue.indexOf(' ') + 1);
                                UserStatusListener[] listeners = userStatusListeners.toArray(new UserStatusListener[0]);
                                try {
	                                for (UserStatusListener listener : listeners) {
	                                    try {
	                                        listener.statusChanged(id, propertyValue);
	                                    } catch (Throwable e) {
	                                        //handleUncaughtException(e);
	                                    	e.printStackTrace();
	                                    }
	                                }
                                } catch (Exception e) {
                                	e.printStackTrace();
                                }
                            }
                        }
                    }
                };
                try {
                    Connector.getInstance().addConnectorListener(userStatusListener);
                } catch (ConnectorException e) {
                    //Utils.convertToSkypeException(e);
                	e.printStackTrace();
                }
            }
        }
    }

    /**
     * Remove a listener for USERSTATUS events.
     * If the listener is already removed nothing happens.
     * @param listener The listener to remove.
     */
    public static void removeUserStatusListener(UserStatusListener listener) {
        //Utils.checkNotNull("listener", listener);
        synchronized (userStatusListenerMutex) {
        	userStatusListeners.remove(listener);
            if (userStatusListeners.isEmpty()) {
                Connector.getInstance().removeConnectorListener(userStatusListener);
                userStatusListener = null;
            }
        }
    }
    
    static void setProperty(String type, String name, String value) {
        try {
            String command = "SET " + type + " " + name + " " + value;
            String responseHeader = type + " " + name + " " + value;
            String response = Connector.getInstance().execute(command, responseHeader);
            //checkError(response);
        } catch (ConnectorException e) {
            //convertToSkypeException(e);
        	e.printStackTrace();
        }
    }
    static void setProperty(String type, String name) {
        try {
            String command = "SET " + type + " " + name;
            String responseHeader = type + " " + name;
            String response = Connector.getInstance().execute(command, responseHeader);
            //checkError(response);
        } catch (ConnectorException e) {
            //convertToSkypeException(e);
        	e.printStackTrace();
        }
    }
}
