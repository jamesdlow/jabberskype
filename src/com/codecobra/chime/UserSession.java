/*
  UserSession.java
  Copyright (c) 2004 by Code Cobra
  

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


import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.net.Socket;
import java.util.*;

/**
 * Encapsulates the user session information.
 * A new UserSession is created each time a new client connects to the server.
 * The UserSession object tracks the java.net.Socket, the jabber-id, and the resource identifier.
 *
 * @author MW
 */
public class UserSession {
    /** Presence availability. */
    public static final int PRESENCE_AVAILABILITY_AVAILABLE=0,PRESENCE_AVAILABILITY_UNAVAILABLE=1;

    /** Presence state. */
    public static final int PRESENCE_STATE_NORMAL=4,PRESENCE_STATE_CHAT=0,PRESENCE_STATE_AWAY=1,PRESENCE_STATE_XA=2,PRESENCE_STATE_DND=3;

    /** A reference to the Server object. */
    private Server server;

    /** The socket used for communications between the server and the client's session. */
    private Socket socket;

    /**
     * The JabberID associated with the UserSession.
     * Set to <code>null</code> if not assigned yet.
     */
    private JabberID jid;

    /** The stream-id for this session. */
    private String streamID;

    /** The Reader object for this session. */
    private Reader reader;

    /** The Writer object for this session. */
    private Writer writer;

    /** Presence availability of the session. */
    private int presenceAvailability;

    /** Presence state of the session. */
    private int presenceState;

    /** Presence priority for the session. */
    private int presencePriority;

    /** Presence status information. */
    private String presenceStatus;

    private Map chatids;

    /**
     * Initializes the UserSession object.
     * Sets the presence availability to PRESENCE_AVAILABILITY_AVAILABLE, state to PRESENCE_STATE_NORMAL, and the priority to 0.
     *
     * @param srv The reference to the Server object.
     * @param s The socket to use for this session.
     */
    public UserSession(Server srv,Socket s) {
        this.server=srv;
        this.socket=s;
        this.jid=null;
        this.streamID=null;
        chatids = new HashMap();
        this.reader=null;
        try {
            this.reader=new InputStreamReader(getSocket().getInputStream());
        }
        catch (IOException ioe) {
            this.server.getLogger().severe("Unable to get a reader for the socket: "+ioe.getMessage());
            this.reader=null;
        }

        this.writer=null;
        try {
            this.writer=new OutputStreamWriter(getSocket().getOutputStream());
        }
        catch (IOException ioe) {
            this.server.getLogger().severe("Unable to get writer for the socket: "+ioe.getMessage());
            this.writer=null;
        }

        setPresenceAvailability(PRESENCE_AVAILABILITY_AVAILABLE);
        setPresenceState(PRESENCE_STATE_NORMAL);
        setPresencePriority(0);
        setPresenceStatus(null);
    } //constructor Socket


    /**
     * Initializes the UserSession for a built-in bot.
     * Sets the presence availability to PRESENCE_AVAILABILITY_AVAILABLE, state to PRESENCE_STATE_NORMAL, and the priority to 0.
     * Bots get packets handed to them directly.
     *
     * @param srv Reference to the Server object.
     * @param jid JabberID for the session.
     * @param q Reference to the PacketQueue for the bot.
     */
    public UserSession(Server srv,JabberID jid,PacketQueue q) {
        this.server=srv;
        this.socket=null;
        this.streamID=null;

        this.reader=null;
        this.writer=null;

        setPresenceAvailability(PRESENCE_AVAILABILITY_AVAILABLE);
        setPresenceState(PRESENCE_STATE_NORMAL);
        setPresencePriority(0);
        setPresenceStatus(null);
        setJID(jid);
    } //constructor UserSession


    /**
     * Return the Socket to be used by the session.
     *
     * @return the reference to the Socket used by the session.
     */
    public Socket getSocket() {
        return socket;
    } //getSocket


    /**
     * Obtain the JabberID associated with this UserSession.
     *
     * @return the JabberID.
     */
    public JabberID getJID() {
        return jid;
    } //getJID


    /**
     * Sets the stream-id for this session.
     * Typically initialized by the OpenStreamHandler.
     *
     * @param id The stream-id to use.
     */
    public void setStreamID(String id) {
        streamID=id;
    } //setStreamID


    /**
     * Obtains the stream-id associated with this session.
     *
     * @return The stream-id, or <code>null</code> if it has not been set yet.
     */
    public String getStreamID() {
        return streamID;
    } //getStreamID


    /**
     * Set the JabberID associated with this UserSession.
     *
     * @param j the JabberID.
     */
    public void setJID(JabberID j) {
        jid=j;
    } //setJID


    /**
     * Returns a Reader object to use to read from the session's socket.
     * Uses an InputStreamReader to read from the InputStream associated with the socket.
     *
     * @return The Reader object, or <code>null</code> if there is an error.
     */
    public Reader getReader() {
        return reader;
    } //getReader


    /**
     * Returns a Writer object to use to write to the session's socket.
     * Uses an OutputStreamWriter to write to the OutputStream associated with the socket.
     *
     * @return The Writer object, or <code>null</code> if there is an error.
     */
    public Writer getWriter() {
        return writer;
    } //getWriter


    /**
     * Sends the JabberPacket to the client connected in this session.
     * For a built-in bot, the packet is delivered directly.
     * Session flushes the Writer after writing the XML data.
     *
     * @param packet The JabberPacket to send.
     */
    public void sendPacket(JabberPacket packet) {
        try {
            Writer w=getWriter();
            if (w!=null) {
            	if (((JabberSkypeSettings) JabberSkype.Settings()).getDebug()) {
                	System.out.println("Server: " + packet.generateXML());
                }
                w.write(packet.generateXML());
                w.flush();
            } //w!=null
        }
        catch (IOException ioe) {
            server.getLogger().warning("Unable to write packet to the session: "+ioe.getMessage());
        }
    } //sendPacket

    /**
     * Destroys the UserSession object.
     * Closes the socket associated with the client.
     * This method does not clean up the session information in the SessionManager.
     */
    public void destroySession() {
        Socket s=getSocket();

        if (s!=null) {
            try {
                s.close();
            }
            catch (IOException ioe) {
                server.getLogger().warning("Unable to close client socket: "+ioe.getMessage());
            }
        } //s!=null
    } //destroySession


    /**
     * Get the presence state of the current session.
     *
     * @return The presence state.
     */
    public int getPresenceState() {
        return presenceState;
    } //getPresenceState


    /**
     * Sets the state of the session.
     *
     * @param state The state to set the session to.
     */
    public void setPresenceState(int state) {
        this.presenceState=state;
    } //setPresenceState


    /**
     * Obtains the presence priority for this session.
     *
     * @return The presence priority.
     */
    public int getPresencePriority() {
        return this.presencePriority;
    } //getPresencePriority


    /**
     * Sets the presence priority for the session.
     *
     * @param priority The presence priority.
     */
    public void setPresencePriority(int priority) {
        this.presencePriority=priority;
    } //setPresencePriority


    /**
     * Sets the presence priority for the session.
     * Parses the incoming String as an integer.
     * If the String does not contain a valid integer, the function sets the presence priority to 0.
     *
     * @param priority The priority.
     */
    public void setPresencePriority(String priority) {
        int p=0;
        try {
            p=Integer.parseInt(priority);
        }
        catch (NumberFormatException nfe) {
            String error="Unable to parse a presence priority: "+priority;
            JabberID jid=getJID();
            if (jid!=null)
                error=error+" for jid "+jid.toString();
            server.getLogger().warning(error);
            p=0;
        }

        setPresencePriority(p);
    } //setPresencePriority


    /**
     * Obtains the presence availability for the session.
     *
     * @return The presence availability.
     */
    public int getPresenceAvailability() {
        return presenceAvailability;
    } //getPresenceAvailability


    /**
     * Sets the presence availability for the session.
     *
     * @param avail The presence availability.
     */
    public void setPresenceAvailability(int avail) {
        this.presenceAvailability=avail;
    } //setPresenceAvailability


    /**
     * Obtains the presence status information.
     *
     * @return The presence status information, or <code>null</code> if none was set.
     */
    public String getPresenceStatus() {
        return presenceStatus;
    } //getPresenceStatus


    /**
     * Sets the presence status information.
     *
     * @param status The presence status information.
     */
    public void setPresenceStatus(String status) {
        this.presenceStatus=status;
    } //setPresenceStatus
    
    public String getChatID(String skypeid) {
    	if (chatids.containsKey(skypeid)) {
    		return (String)chatids.get(skypeid);
    	} else {
    		return "";
    	}
    }
    public void setChatID(String skypeid, String chatid) {
    	if (chatids.containsKey(skypeid)) {
    		chatids.remove(skypeid);
    	}
    	chatids.put(skypeid, chatid);
    }
} //class UserSession
