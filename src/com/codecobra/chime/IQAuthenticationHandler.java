/*
  IQAuthenticationHandler.java
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

import com.jameslow.*;

/**
 * Handles IQ Authentication requests.
 * Compares user information sent from the client with the information saved in the store.
 *
 * @author MW
 */
public class IQAuthenticationHandler implements PacketHandler {
    /** Reference to Server object. */
    private Server server;


    /**
     * Initializes the handler.
     *
     * @param srv Reference to the server object.
     */
    public IQAuthenticationHandler(Server srv) {
        this.server=srv;
    } //constructor IQAuthenticationHandler


    /**
     * Processes the incoming packet.
     *
     * @param packet The JabberPacket.
     */
    public void processPacket(JabberPacket packet) {
        String type=packet.getType();

        JabberPacket query=packet.getFirstChild("query");

        if (type.equals("get")) {
            //client is asking what they need to send to be authenticated
            //FUTURE currently only allow plain authentication; support better
            //authentication

            JabberPacket response=new JabberPacket(null,"iq",null);
            response.setType("result");
            response.setID(packet.getID());
            response.setAttribute("xmlns","jabber:client");

            String userName=query.getFirstChild("username").getValue();

            //reuse query variable
            query=response.addChild("query");

            query.setAttribute("xmlns","jabber:iq:auth");
            query.addChild("username").appendValue(userName);
            query.addChild("resource");
            query.addChild("password");

            packet.getSession().sendPacket(response);
        } else if (type.equals("set")) {
            //got the information back from the user

            //TODO handle condition where client did not supply the requested
            //fields
            String userName=query.getFirstChild("username").getValue();
            String password=query.getFirstChild("password").getValue();
            String resource=query.getFirstChild("resource").getValue();
            String hostname=packet.getSession().getSocket().getInetAddress().getHostName().toString();
            boolean auth=false;
            JabberSkypeSettings settings = (JabberSkypeSettings) JabberSkype.Settings();
            if ((settings.getHostname().compareTo("") == 0) || (settings.getHostname().compareTo(hostname) == 0)) {
	            if ((server.getUserName().compareTo(userName) == 0)  && ((settings.getPassword().compareTo(password) == 0) || (settings.getPassword().compareTo("") == 0))) {
	            	auth = true;
	            }
            }
            if (auth) {
                //correct username/password
                packet.getSession().setJID(new JabberID(userName,server.getServerName(),resource));

                server.getSessionManager().addSession(packet.getSession());

                //send response
                JabberPacket response=new JabberPacket(null,"iq",null);
                response.setType("result");
                response.setID(packet.getID());

                packet.getSession().sendPacket(response);
            } else {
                //unable to authenticate, send back error packet

                JabberPacket response=new JabberPacket(null,"iq",null);
                response.setType("error");
                response.setID(packet.getID());

                JabberPacket packetError=response.addChild("error");
                packetError.setType("auth");

                JabberPacket cond=packetError.addChild("not-authorized");
                cond.setAttribute("xmlns","urn:ietf:params:xml:ns:xmpp-stanzas");

                server.getLogger().info("Bad authentication credentials for user "+userName);

                packet.getSession().sendPacket(response);
            } //authenticateUser
        } //type
    } //processPacket
} //class IQAuthenticationHandler
