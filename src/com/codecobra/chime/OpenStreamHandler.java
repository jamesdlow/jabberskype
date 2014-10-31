/*
  OpenStreamHandler.java
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


import java.io.IOException;


/**
 * Processes the opening stream tag.
 * Sets the current session's information and sends back a packet that acknowledges the opening of the stream.
 *
 * @author MW
 */
public class OpenStreamHandler implements PacketHandler {
    /** Reference to the Server object. */
    private Server server;


    /**
     * Initializes the OpenStreamHandler.
     *
     * @param srv The reference to the Server object.
     */
    public OpenStreamHandler(Server srv) {
        server=srv;
    } //constructor OpenStreamHandler


    /**
     * Processes the opening stream packet.
     * Sets the session's fields and assigns a new stream-id.
     * Also sends back an acknowledgement packet saying that the server opened the stream to the client.
     *
     * @param packet The JabberPacket to process.
     */
    public void processPacket(JabberPacket packet) {
        UserSession session=packet.getSession();
        session.setStreamID(server.getNextStreamID());

        //create response packet
        JabberPacket response=new JabberPacket(null,"stream:stream",null);

        response.setFrom(server.getServerName());
        response.setID(session.getStreamID());
        response.setAttribute("xmlns:stream",packet.getAttribute("xmlns:stream"));
        response.setAttribute("xmlns",packet.getAttribute("xmlns"));

        session.sendPacket(response);
    } //processPacket
} //class OpenStreamHandler
