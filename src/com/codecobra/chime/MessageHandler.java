/*
  MessageHandler.java
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

import com.skype.*;

/**
 * The MessageHandler delivers packets to the appropriate end-point.
 *
 * @author MW
 */
public class MessageHandler implements PacketHandler {
    /** Reference to the Server object. */
    private Server server;

    /** Reference to the PacketAuthorizationManager. */
    //private PacketAuthorizationManager mgrPacketAuthorization;

    /** Reference to packet logger manager. */
    //private PacketLoggerManager mgrPacketLogger;


    /**
     * Initializes the MessageHandler object.
     *
     * @param srv The reference to the Server object.
     */
    public MessageHandler(Server srv) {
        server=srv;

        //mgrPacketAuthorization=server.getPacketAuthorizationManager();
        //mgrPacketLogger=server.getPacketLoggerManager();
    } //constructor MessageHandler


    /**
     * Processes the incoming packet.
     *
     * @param packet The packet to process.
     */
    public void processPacket(JabberPacket packet) {
        String sFrom=packet.getFrom();
        String sTo=JabberSkypeListeners.removeSuffix(packet.getTo());

        if (sTo==null) {
            //error condition: must not continue processing on this packet;
            return;
        } //sTo==null

        if (sFrom==null) {
            sFrom=packet.getSession().getJID().toString();

            if (sFrom==null)
                return;

            packet.setFrom(sFrom);
        } //sFrom==null

        if (true) {
            //mgrPacketLogger.logPacket(packet);

            JabberID jidTo=JabberID.valueOf(sTo);
            String sToUserName=jidTo.getUserName();
            String sToResource=jidTo.getResource();

            	try {
            		/*
            		String id = "";
            		try {
            			id = packet.getAttribute("id");	
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            		if ("".compareTo(id) != 0) {
            			packet.getSession().setChatID(sTo,id);
            		}
            		*/
            		
            		JabberPacket pBody = packet.getFirstChild("body");
            		if (pBody != null) {
	            		String sMsg = pBody.getValue();
	            		if (sMsg != null) {
	            			Skype.getUser(sTo).chat().send(sMsg);
	            		}
            		}
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	/*
                if (sToResource==null)
                    sToResource="";

                int highestPriority=-1; //never deliver to negative priorities
                UserSession sessionHighestPriority=null; //UserSession with the highest priority

                boolean foundSession=false; //did we find a session to deliver the message to?

                for (int a=0;a<us.length&&!foundSession;a++) {
                    int p=us[a].getPresencePriority();

                    if (p>highestPriority) {
                        highestPriority=p;
                        sessionHighestPriority=us[a];
                    } //p>highestPriority

                    if (!sToResource.equals("")) {
                        //specified a specific resource

                        String r=us[a].getJID().getResource();
                        if (r.equals(sToResource)) {
                            foundSession=true;
                            us[a].sendPacket(packet);
                        } //r==sToResource
                    } //sToResource!=""
                } //a<us.length
            	 */
        } else {
            //send back an error message
            //TODO should not send back an error message if a bot sent out the message originally
            JabberPacket response=new JabberPacket(null,"message",null);

            String id=packet.getID();
            if (id!=null)
                response.setID(id);
            response.setFrom(server.getServerName());
            response.setTo(sFrom);
            response.setType("error");

            JabberPacket body=response.addChild("body");
            body.appendValue("You are not authorized to send messages to this user."); //FUTURE i18n

            JabberPacket error=response.addChild("error");
            error.setType("auth");

            JabberPacket cond=error.addChild("not-authorized");
            cond.setAttribute("xmlns","urn:ietf:params:xml:ns:xmpp-stanzas");

            packet.getSession().sendPacket(response);

            //FUTURE may want to log the failed attempt
        } //allowPacket
    } //processPacket
} //class MessageHandler
