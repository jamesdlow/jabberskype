/*
  PresenceHandler.java
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

import com.skype.Skype;
import com.skype.Profile.Status;

/**
 * Processes presence packets.
 *
 * @author MW
 */
public class PresenceHandler implements PacketHandler {
    /** Reference to the Server object. */
    private Server server;


    /**
     * Initializes the handler.
     *
     * @param server Reference to the Server object.
     */
    public PresenceHandler(Server server) {
        this.server=server;
    } //constructor PresenceHandler


    /**
     * Processes the presence packet.
     *
     * @param packet The JabberPacket.
     */
    public void processPacket(JabberPacket packet) {
        UserSession session=packet.getSession();
        if (session==null)
            return;

        //parse the incoming presence packet
        if (!updateSessionPresence(packet))
            return;
        //server.getRosterManager().distributePresence(session);
    } //processPacket


    /**
     * Updates the presence information for the given session.
     *
     * @param packet The incoming presence packet.
     * @return <code>true</code> if the update was successful, <code>false</code> if it was not.
     */
    private boolean updateSessionPresence(JabberPacket packet) {
        UserSession session=packet.getSession();

        //do not process presence updates directly to other users
        if (packet.getTo()!=null)
            return false;

        //type
        String type=packet.getType();
        int presenceAvailability=UserSession.PRESENCE_AVAILABILITY_AVAILABLE;
        if (type!=null) {
            if (type.equals("unavailable"))
                presenceAvailability=UserSession.PRESENCE_AVAILABILITY_UNAVAILABLE;
            else if (type.equals("probe")) {
                //we do not handle probes
                return false;
            } else if (type.equals("error")) {
                //record an error and return
                server.getLogger().warning("Error presence packet: "+packet.generateXML());
                return false;
            } //type
        } //type!=null
        session.setPresenceAvailability(presenceAvailability);

        //check if a specific presence state was indicated
        JabberPacket pShow=packet.getFirstChild("show");
        int presenceState=UserSession.PRESENCE_STATE_NORMAL;        
        try {
	        if (pShow!=null) {
	            String valShow=pShow.getValue();
	            if (valShow!=null) {
	                if (valShow.equals("chat")) {
	                    presenceState=UserSession.PRESENCE_STATE_CHAT;
	                	Skype.getProfile().setStatus(Status.SKYPEME);
	                } else if (valShow.equals("away")) {
	                    presenceState=UserSession.PRESENCE_STATE_AWAY;
	                    Skype.getProfile().setStatus(Status.AWAY);
	                } else if (valShow.equals("xa")) {
	                    presenceState=UserSession.PRESENCE_STATE_XA;
	                    Skype.getProfile().setStatus(Status.NA);
	                } else if (valShow.equals("dnd")) {
	                    presenceState=UserSession.PRESENCE_STATE_DND;
	                    Skype.getProfile().setStatus(Status.DND);
	                } else {
	                	Skype.getProfile().setStatus(Status.ONLINE);
	                }
	            } //valShow!=null
	        } else {
	        	Skype.getProfile().setStatus(Status.ONLINE);
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        session.setPresenceState(presenceState);

        //check for priority
        JabberPacket pPriority=packet.getFirstChild("priority");
        int priority=0;
        if (pPriority!=null) {
            String valPriority=pPriority.getValue();

            if (valPriority!=null) {
                try {
                    priority=Integer.parseInt(valPriority);
                }
                catch (NumberFormatException nfe) {
                    server.getLogger().warning("Unable to parse priority for presence packet: "+valPriority);
                    priority=0;
                }
            } //valPriority!=null
        } //pPriority!=null
        session.setPresencePriority(priority);

        //check for status message
        JabberPacket pStatus=packet.getFirstChild("status");
        String status=null;
        if (pStatus!=null) {
            status=pStatus.getValue();
        } //pStatus!=null
        session.setPresenceStatus(status);

        return true;
    } //updateSessionPresence
} //class PresenceHandler
