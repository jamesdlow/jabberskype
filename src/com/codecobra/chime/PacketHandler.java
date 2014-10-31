/*
  PacketHandler.java
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
 * The interface that defines the methods used in packet handlers.
 *
 * @author MW
 */
public interface PacketHandler {
    /**
     * Function invoked by the QueueProcessor to process a packet.
     *
     * @param packet The JabberPacket to process.
     */
    public void processPacket(JabberPacket packet);
} //interface PacketHandler
