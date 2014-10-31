/*
  Server.java
  Copyright (c) 2004-2005 by Code Cobra
  

  This file is part of chime.

  chime is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  chime is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with chime; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.codecobra.chime;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Date;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.skype.*;
import com.jameslow.*;

/**
 * Main class for chime.
 * Establishes the main listening socket and starts the various other program threads.
 *
 * @author MW
 * @param <Main>
 */
public class Server<Main> implements Runnable {
    /** The server's version. */
    public static final String version="0.6.2b";

    /** The logger all parts of the chime use. */
    private Logger logger;

    /** The socket the chime server listens on for new client connections. */
    private ServerSocket ssListen;

    /** The queue for JabberPacket objects that get processed by the QueueProcessor. */
    private PacketQueue queue;

    /** The stream-id counter. */
    private int streamID;

    /** The server's name. */
    private String nameServer="JabberSkype";

    /** Main port the server listens at. */
    private int portListen=5222;

    /** Reference to the SessionManager. */
    private SessionManager mgrSession;

    /** Reference to the RosterManager. */
    private RosterManager mgrRoster;

    /** Date/time the server was started at. */
    private Date dtServerStarted;
    
    private boolean debug;
    private com.codecobra.chime.JabberSkype main;
    private Thread tPackets;
    private Thread tConnections;
    private boolean running = false;

    /**
     * Initializes all parts of the chime.
     *
     * @param conf The file-path to the server's configuration file.
     */
    public Server(com.codecobra.chime.JabberSkype main) {
        logger=Logger.getLogger("com.codecobra.chime");
        this.main = main;
    } //constructor Server
    public void start() {
    	if (!running) {
	    	running = true;
	        dtServerStarted=new Date();
	        streamID=0;
	    	
	        mgrSession=new SessionManager(this);
	        mgrRoster=new RosterManager(this);
	
	        establishListeningSocket();
	
	        queue=new PacketQueue();
	
	        QueueProcessor qp=new QueueProcessor(this);
	        setupPacketHandlers(qp);
	        tPackets = (new Thread(qp));
	        tPackets.start();
	        //TODO: Do we need to check skype is attached first?
	        //Skype.isInstalled()
	        //Skype.isRunning()
	        //com.skype.NotAttachedException
	        JabberSkypeListeners.enableListeners(this);

	        tConnections = (new Thread(this));
	        tConnections.start();
    	}
    }
    @SuppressWarnings("deprecation")
	public void stop() {
    	if (running) {
	    	JabberSkypeListeners.disableListeners();
	    	mgrSession.removeAll();
	    	running = false;
	    	try {
	    		ssListen.close();
	    	} catch (Exception e){
	    		e.printStackTrace();
	    	}
    	};
    }


    /**
     * Returns the Logger object to use for any sub-component of the program.
     *
     * @return The Logger object.
     */
    public Logger getLogger() {
        return logger;
    } //getLogger


    /**
     * Sets up the listening socket for new incoming connections.
     * Port-number is hardcoded to 5222.
     */
    public void establishListeningSocket() {
        ssListen=null;
        try {
            ssListen=new ServerSocket(portListen);
        }
        catch (IOException ioe) {
            getLogger().severe("Unable to establish listening socket: "+ioe.getMessage());
            System.exit(0);
        }
    } //establishListeningSocket


    /**
     * Returns a reference to the JabberPacket object queue.
     *
     * @return a reference to the PacketQueue.
     */
    public PacketQueue getPacketQueue() {
        return queue;
    } //getPacketQueue

    public void run() {
    	processNewConnections();
	}

    /**
     * Processes new incoming connections.
     */
    public void processNewConnections() {
        getLogger().info("Accepting connections for: "+getServerName());

        while (running) {
            Socket sClient=null;
            try {
                sClient=ssListen.accept();
            }
            catch (IOException ioe) {
                getLogger().warning("Unable to accept connection: "+ioe.getMessage());
                sClient=null;
            }

            if (sClient!=null) {
                UserSession us=new UserSession(this,sClient);
                DataProcessor dp=new DataProcessor(this,us);
                (new Thread(dp)).start();
            } //sClient!=null
        } //true
    } //processNewConnections


    /**
     * Creates and registers the various packet handlers.
     *
     * @param qp The QueueProcessor object.
     */
    private void setupPacketHandlers(QueueProcessor qp) {
        PacketHandler h=null;

        h=new DefaultHandler(this);
        qp.setDefaultHandler(h);

        h=new OpenStreamHandler(this);
        qp.setHandler(h,"stream:stream");

        h=new CloseStreamHandler(this);
        qp.setHandler(h,"/stream:stream");

        h=new PresenceHandler(this);
        qp.setHandler(h,"presence");

        h=new IQAuthenticationHandler(this);
        qp.setHandler(h,"jabber:iq:auth");

        h=new IQRosterHandler(this);
        qp.setHandler(h,"jabber:iq:roster");

        h=new IQRosterHandler(this);
        qp.setHandler(h,"http://jabber.org/protocol/disco#info");
        
        h=new VCardHandler(this);
        qp.setHandler(h,"vcard-temp");
        
        h=new MessageHandler(this);
        qp.setHandler(h,"message");
    } //setupPacketHandlers


    /**
     * Obtains a new stream-id.
     * This function is typically only invoked by the OpenStreamHandler.
     * Currently, it assigns stream-ids sequentially, starting at 0.
     * This may have to change later on down the line.
     *
     * @return The new stream-id.
     */
    public String getNextStreamID() {
        int id=0;

        synchronized(this) {
            id=streamID++;
        } //synchronized(streamID)

        return Integer.toHexString(id);
    } //getNextStreamID


    /**
     * Obtains the server's name.
     *
     * @return The server's name.
     */
    public String getServerName() {
        return nameServer;
    } //getServerName


    /**
     * Sets the server's name.
     *
     * @param name The server's name.
     */
    public void setServerName(String name) {
        this.nameServer=name;
    } //setServerName
    
    public int getPortListen() {
        return portListen;
    }
    public void setPortListen(int portListen) {
        this.portListen=portListen;
    }

    /**
     * Obtains a reference to the SessionManager.
     *
     * @return Reference to the SessionManager.
     */
    public SessionManager getSessionManager() {
        return mgrSession;
    } //getSessionManager


    /**
     * Obtains a reference to the RosterManager.
     *
     * @return Reference to the RosterManager.
     */
    public RosterManager getRosterManager() {
        return mgrRoster;
    } //getRosterManager
    

    /**
     * Returns the date the server was started at.
     *
     * @return A Date object containing the starting date.
     */
    public Date getDateStarted() {
        return dtServerStarted;
    } //getDateStarted
    
    public String getUserName() {
    	try {
    		return Skype.getProfile().getId();
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    public com.codecobra.chime.JabberSkype getMain() {
    	return main;
    }
    public boolean getRunning() {
    	return running;
    }
} //class Server
