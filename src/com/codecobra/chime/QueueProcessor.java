/*
  QueueProcessor.java
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

import java.util.HashMap;
import java.util.Map;
import com.jameslow.*;

/**
 * The processor that handles JabberPackets in the PacketQueue.
 * It allows handlers to register themselves and goes through the list of available handlers to process JabberPackets.
 * Only one handler per packet type is allowed.
 * The processor allows a default handler to be specified, for the case where no specific handler was found to deal with the JabberPacket.
 * For packets other than iq, the processors uses the handler that matches the top-most element name.
 * For iq packets, the processor looks for a handler that matches the name-space of the iq packet.
 * If no specific handler is found, the processor uses the default handler to process the packet.
 *
 * @author MW
 */
public class QueueProcessor implements Runnable {
    /** Reference to Server object. */
    private Server server;

    /** Map that keeps track of the specific handlers specified. */
    private Map mHandlers;

    /** Default handler, in case no specific handler matches. */
    private PacketHandler hDefault;

    /** Reference to the PacketQueue. */
    private PacketQueue queue;


    /**
     * Initializes the queue processor.
     * The processor pulls JabberPacket objects from the PacketQueue and passes them on to the current handler.
     *
     * @param srv The reference to the Server object.
     */
    public QueueProcessor(Server srv) {
        server=srv;

        mHandlers=new HashMap();
        hDefault=null;

        queue=server.getPacketQueue();
    } //constructor QueueProcessor


    /**
     * Sets the default handler.
     *
     * @param handler The default handler.
     */
    public void setDefaultHandler(PacketHandler handler) {
        hDefault=handler;
    } //setDefaultHandler


    /**
     * Returns the default handler.
     *
     * @return The default handler.
     */
    public PacketHandler getDefaultHandler() {
        return hDefault;
    } //getDefaultHandler


    /**
     * Sets the handler for a given key.
     *
     * @param handler The handler to use for the element.
     * @param key The key name.
     */
    public void setHandler(PacketHandler handler,String key) {
        mHandlers.put(key,handler);
    } //setHandler


    /**
     * Obtains the handler for a given key.
     * The method will return the default handler in case no matching handler is found in the list of handlers.
     *
     * @param key The key.
     * @return The handler to use for the packet.
     */
    public PacketHandler getHandler(String key) {
        PacketHandler h=(PacketHandler)mHandlers.get(key);
        if (h==null)
            h=getDefaultHandler();
        return h;
    } //getHandler


    /**
     * Method actually processes data.
     */
    public void run() {
        JabberPacket packet=null;

        do {
            try {
                packet=queue.pull();

                if (packet!=null) {
                    PacketHandler h=null;
                    if (((JabberSkypeSettings) Main.Settings()).getDebug()) {
                    	System.out.println("Client: " + packet.generateXML());
                    }
                    String e=packet.getElement();
                    if (e!=null) {
                        String key=null;

                        if (e.equals("iq")) {
                            JabberPacket pQuery=packet.getFirstChild("query");

                            if (pQuery!=null) {
                                key=pQuery.getAttribute("xmlns");
                            } else {
                            	JabberPacket pVcard=packet.getFirstChild("vcard");
                            	if (pVcard!= null) {
                            		key=pVcard.getAttribute("xmlns");
                            		//need to do difference between setting vcard, and getting it for a user
                            	}
                            }
                        } else
                            key=e;

                        if (key!=null) {
                            h=(PacketHandler)mHandlers.get(key);

                            if (h==null)
                                h=getDefaultHandler();

                            h.processPacket(packet);
                        } //key!=null
                    } //e!=null
                } //packet!=null
            } //try
            catch (Exception e) {
                server.getLogger().severe("Caught exception: "+e.getMessage());
            }
        } while (packet!=null && server.getRunning());
    } //run
} //class QueueProcessor
