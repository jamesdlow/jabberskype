/*
  PacketQueue.java
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


import java.util.LinkedList;


/**
 * The queue for JabberPacket objects.
 * Clients can push packets onto the queue and can retrieve packets from the
 * queue.
 * The queue represents a FIFO queue that is synchronized so that multiple
 * threads can access it at the same time.
 *
 * @author MW
 */
public class PacketQueue {
    /** The List used to store the packets. */
    private LinkedList queue;


    /**
     * Creates the initially empty queue.
     */
    public PacketQueue() {
        queue=new LinkedList();
    } //constructor PacketQueue


    /**
     * Pushes a new packet onto the queue.
     * Notifies all threads waiting for pull operation that a packet has become available.
     *
     * @param packet The JabberPacket to push onto the queue.
     */
    public synchronized void push(JabberPacket packet) {
        queue.addLast(packet);
        notifyAll();
    } //push


    /**
     * Pulls the first packet off the queue.
     * Makes threads wait if there are no more packets in the queue.
     *
     * @return The next packet in the queue, or <code>null</code> if interrupted.
     */
    public synchronized JabberPacket pull() {
        while (queue.size()<=0) {
            try {
                wait();
            }
            catch (InterruptedException ie) {
                return null;
            }
        } //queue.size()<=0

        return (JabberPacket)queue.removeFirst();
    } //pull
} //class PacketQueue
