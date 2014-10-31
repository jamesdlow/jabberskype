/*
  IQRosterHandler.java
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


/**
 * Handles roster requests.
 *
 * @author MW
 */
public class IQRosterHandler implements PacketHandler {
    /** Reference to Server object. */
    private Server server;


    /**
     * Initializes the handler.
     *
     * @param srv Reference to the server object.
     */
    public IQRosterHandler(Server srv) {
        this.server=srv;
    } //constructor IQRosterHandler


    /**
     * Processes the incoming packet.
     *
     * @param packet The JabberPacket.
     */
    public void processPacket(JabberPacket packet) {
        String type=packet.getType();

        if (type.equals("get")) {
            //client wants a roster reset, i.e. transmittal of all roster data

            server.getRosterManager().sendRosterReset(packet);
        } else if (type.equals("set")) {
            //return an error, indicating that the user is not able to update the roster information
            JabberPacket response=new JabberPacket(null,"iq",null);
            response.setType("error");
            if (packet.getID()!=null)
                response.setID(packet.getID());

            JabberPacket packetError=response.addChild("error");
            packetError.setType("cancel");

            JabberPacket cond=packetError.addChild("feature-not-implemented");
            cond.setAttribute("xmlns","urn:ietf:params:xml:ns:xmpp-stanzas");

            packet.getSession().sendPacket(response);
        } //type
    } //processPacket
} //class IQRosterHandler
