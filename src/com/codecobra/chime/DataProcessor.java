/*
  DataProcessor.java
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

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

import org.w3c.dom.Document;


/**
 * Handles data received from a client socket.
 * Each UserSession has its own DataProcessor associate with it that is in charge of parsing data it receives on its socket.
 *
 * @author MW
 */
public class DataProcessor implements Runnable {
    /** Reference to Server object. */
    private Server server;

    /** UserSession the DataProcessor is monitoring. */
    private UserSession session;

    /** SAXParser to be used for the XML stream. */
    private SAXParser xmlParser;


    /**
     * Initializes the DataProcessor.
     * Creates a new SAXParser instance.
     *
     * @param srv A reference to the Server object.
     * @param sess A reference to the UserSession object.
     */
    public DataProcessor(Server srv,UserSession sess) {
        server=srv;
        session=sess;

        SAXParserFactory saxpf=null;
        try {
            saxpf=SAXParserFactory.newInstance();
        }
        catch (FactoryConfigurationError fce) {
            server.getLogger().severe("Unable to get SAXParserFactory: "+fce.getMessage());
            System.exit(0);
        }

        xmlParser=null;
        try {
            xmlParser=saxpf.newSAXParser();
        }
        catch (ParserConfigurationException pce) {
            server.getLogger().severe("Unable to get SAXParser: "+pce.getMessage());
        }
        catch (SAXException saxe) {
            server.getLogger().severe("Unable to get SAXParser: "+saxe.getMessage());
        }
    } //constructor DataProcessor


    /**
     * Method actually processes data.
     */
    public void run() {
        processData();

        session.destroySession();
    } //run


    /**
     * Method to actually process data.
     * Data is read from the reader and is parsed.
     */
    protected void processData() {
        InputSource is=new InputSource(session.getReader());

        try {
            xmlParser.parse(is,new XMLDataHandler());
        }
        catch (SAXException saxe) {
            server.getLogger().warning("SAXException: "+saxe.getMessage());
            server.getSessionManager().removeSession(session);
            return;
        }
        catch (IOException ioe) {
            server.getLogger().warning("IOException: "+ioe.getMessage());
            server.getSessionManager().removeSession(session);
            return;
        }
    } //processData


    /**
     * Handles events issued by the parser.
     *
     * @author MW
     */
    class XMLDataHandler extends DefaultHandler {
        /** JabberPacket we are processing. */
        private JabberPacket packet;

        /** Current depth we are at. */
        private int depth;


        /**
         * Sets depth to 0.
         */
        public XMLDataHandler() {
            depth=0;
        } //constructor XMLDataHandler


        /**
         * Event that we are about to start a new element.
         * Increments the depth counter and manages packages.
         */
        public void startElement(String uri,String localName,String qName,Attributes attributes) throws SAXException {
            if (depth==0) {
                if (!qName.equals("stream:stream"))
                    //first packet must be <stream:stream>
                    throw new SAXException("Root element must be <stream:stream>");

                packet=new JabberPacket(null,qName,attributes);
                packet.setSession(session);

                server.getPacketQueue().push(packet);
            } else if (depth==1) {
                //top-level packet (message, iq, etc.)
                packet=new JabberPacket(null,qName,attributes);
                packet.setSession(session);
            } else {
                //any other depth must be a child of the current packet
                packet=new JabberPacket(packet,qName,attributes);
            } //depth

            depth++;
        } //startElement


        /**
         * Event that an element is ending.
         * Decrement depth counter.
         * Push packets onto queue (if they are done).
         */
        public void endElement(String uri,String localName,String qName) throws SAXException {
            depth--;

            if (depth==0) {
                //must be a </stream:stream> packet
                packet=new JabberPacket(null,"/stream:stream",null);
                packet.setSession(session);
                server.getPacketQueue().push(packet);
            } else if (depth==1) {
                //packet completed, push onto queue;
                server.getPacketQueue().push(packet);
            } else {
                //go back up one level
                packet=packet.getParent();
            } //depth
        } //endElement


        /**
         * Event that character data was received for the current element.
         * Append the data to the element value.
         */
        public void characters(char[] ch,int start,int length) throws SAXException {
            String s=new String(ch,start,length);

            if (packet!=null)
                packet.appendValue(s);
        } //characters
    } //class XMLDataHandler
} //class DataProcessor
