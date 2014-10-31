/*
  CloseStreamHandler.java
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
 * Processes the closing stream tag.
 *
 * @author MW
 */
public class CloseStreamHandler implements PacketHandler {
    /** Reference to the Server object. */
    private Server server;


    /**
     * Initializes the CloseStreamHandler.
     *
     * @param srv The reference to the Server object.
     */
    public CloseStreamHandler(Server srv) {
        server=srv;
    } //constructor CloseStreamHandler


    /**
     * Processes the closing stream packet.
     *
     * @param packet The JabberPacket to process.
     */
    public void processPacket(JabberPacket packet) {
        UserSession session=packet.getSession();
        server.getSessionManager().removeSession(session);
        try {
        	Skype.getProfile().setStatus(Status.OFFLINE);
        } catch (Exception e){
        	e.printStackTrace();
        }
    } //processPacket
} //class CloseStreamHandler
