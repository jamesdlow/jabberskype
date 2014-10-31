/*
  JabberPacket.java
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;


/**
 * A JabberPacket represents the parsed data received from the client.
 * Data is parsed by the DataProcessor thread.
 * Since using DOM is an overkill for this project, we build our own class to save the parsed data.
 * The class JabberPacket does, however, have a large number of similarities with the Node class used in the DOM model.
 *
 * @author MW
 */
public class JabberPacket {
    /** This packet's parent JabberPacket. */
    private JabberPacket parent;

    /** The element's name. */
    private String element;

    /** The element's value (if given). */
    private StringBuffer elementValue=null;

    /** List of children JabberPackets. */
    private List children=new LinkedList();

    /** Table of attributes. */
    private Map attributes=new HashMap();

    /** Reference to UserSession object this packet is for. */
    private UserSession session=null;


    /**
     * Constructs a new JabberPacket.
     *
     * @param parent The parent JabberPacket, or <code>null</code> if no parent.
     * @param element The element name.
     * @param attributes The list of attributes, or <code>null</code> if no attributes.
     */
    public JabberPacket(JabberPacket parent,String element,Attributes attributes) {
        setParent(parent);
        setElement(element);
        setAttributes(attributes);
    } //constructor JabberPacket


    /**
     * Obtains this packet's parent JabberPacket.
     *
     * @return the parent JabberPacket.
     */
    public JabberPacket getParent() {
        return parent;
    } //getParent


    /**
     * Sets the packet's parent.
     * Registers this node as a child of the parent packet.
     *
     * @param parent The parent JabberPacket object.
     */
    public void setParent(JabberPacket parent) {
        this.parent=parent;

        if (this.parent!=null)
            this.parent.addChild(this);
    } //setParent


    /**
     * Obtains the packet's element name.
     *
     * @return The packet's element name.
     */
    public String getElement() {
        return element;
    } //getElement


    /**
     * Sets the packet's element name.
     *
     * @param element The packet's element name.
     */
    public void setElement(String element) {
        if (element!=null)
            this.element=element.toLowerCase();
        else
            this.element=null;
    } //setElement


    /**
     * Sets the reference to the UserSession object.
     *
     * @param session The UserSession object.
     */
    public void setSession(UserSession session) {
        this.session=session;
    } //setSession


    /**
     * Gets the reference to the UserSession object this packet is for.
     *
     * @return The UserSession object, or <code>null</code> if none was specified.
     */
    public UserSession getSession() {
        return session;
    } //getSession


    /**
     * Clears the list of attributes for this packet.
     */
    public void clearAttributes() {
        attributes.clear();
    } //clearAttributes


    /**
     * Gets the list of attributes.
     *
     * @return a Map containing the attributes; the Map will be empty if no attributes have been specified.
     */
    public Map getAttributes() {
        return attributes;
    } //getAttributes


    /**
     * Sets the packet's list of attributes.
     * The function loops through the attributes given and sets this packet's attributes.
     * The function discards any attributes perviously set for this packet.
     *
     * @param attr The list of attributes.
     */
    public void setAttributes(Attributes attr) {
        clearAttributes();

        if (attr==null)
            return;

        for (int a=0;a<attr.getLength();a++)
            setAttribute(attr.getQName(a),attr.getValue(a));
    } //setAttributes


    /**
     * Sets a specific attribute.
     * Overwrites any previous value set for the same attribute name.
     *
     * @param attr The attribute's name.
     * @param val The attribute's value.
     */
    public void setAttribute(String attr,String val) {
        attributes.put(attr.toLowerCase(),val);
    } //setAttribute


    /**
     * Returns a specific attribute's value.
     *
     * @param attr The attribute's name.
     * @return The attribute's value, or <code>null</code> if the attribute has no value or was not found.
     */
    public String getAttribute(String attr) {
        return (String)attributes.get(attr.toLowerCase());
    } //getAttribute


    /**
     * Gets the element's value.
     *
     * @return The element's value, or <code>null</code>, if none was set.
     */
    public String getValue() {
        if (elementValue==null)
            return null;
        else
            return elementValue.toString();
    } //getValue


    /**
     * Appends a String to the value of this element.
     *
     * @param v The value to append to the list of values.
     */
    public void appendValue(String v) {
        if (elementValue==null)
            elementValue=new StringBuffer();

        elementValue.append(v);
    } //appendValue


    /**
     * Returns the list of child nodes associated with this packet.
     *
     * @return The List of child packets; the List will be empty if no child nodes exist.
     */
    public List getChildren() {
        return children;
    } //getChildren


    /**
     * Obtains the first child node with a given element name.
     * Does not descend into the children of a child packet.
     *
     * @return The first child packet with a given element name, or <code>null</code> if no child packet with that element name was found.
     */
    public JabberPacket getFirstChild(String element) {
        JabberPacket c=null;

        List lChildren=getChildren();
        Iterator iChildren=lChildren.iterator();
        while (iChildren.hasNext()&&c==null) {
            c=(JabberPacket)iChildren.next();
            if (!c.getElement().equals(element.toLowerCase()))
                c=null;
        } //iChildren.hasNext

        return c;
    } //getFirstChild


    /**
     * Adds a JabberPacket as a child node of this packet.
     *
     * @param c The Child packet.
     */
    public void addChild(JabberPacket c) {
        children.add(c);
    } //addChild


    /**
     * Creates a new JabberPacket with the given element name and adds it as a child node of this packet.
     *
     * @param element The element name to use.
     */
    public JabberPacket addChild(String element) {
        JabberPacket p=new JabberPacket(this,element,null);
        return p;
    } //addChild


    /**
     * Generates XML output from the packet hierarchy.
     * Traverses the list of child packets, assembling the XML output.
     *
     * @return A buffer containing the XML data representing the packet hierarchry.
     */
    public String generateXML() {
        StringBuffer buffer=new StringBuffer();

        buffer.append("<");
        buffer.append(getElement());

        Map attr=getAttributes();

        if (attr.size()>0) {
            Set attrKeys=attr.keySet();
            Iterator iKeys=attrKeys.iterator();

            while (iKeys.hasNext()) {
                buffer.append(" ");

                String key=(String)iKeys.next();
                String val=(String)attr.get(key);

                buffer.append(key);

                buffer.append("='");
                buffer.append(val);
                buffer.append("'");
            } //iKeys.hasNext
        } //attr.size()>0

        buffer.append(">");


        String val=getValue();
        if (val!=null)
            buffer.append(val);


        List childNodes=getChildren();

        if (childNodes.size()>0) {
            Iterator iChildren=childNodes.iterator();

            while (iChildren.hasNext()) {
                JabberPacket child=(JabberPacket)iChildren.next();
                buffer.append(child.generateXML());
            } //iChildren.hasNext
        } //childNodes.size()>0

        //do not send closing tag if this is a stream:stream or /stream:stream tag
        if (!getElement().equals("stream:stream")&&!getElement().equals("/stream:stream")) {
            buffer.append("</");
            buffer.append(getElement());
            buffer.append(">");
        } //stream:stream

        return buffer.toString();
    } //generateXML


    /**
     * Convenience method that calls generateXML.
     *
     * @see #generateXML()
     */
    public String toString() {
        return generateXML();
    } //toString


    /**
     * Convenience method to get the From attribute.
     *
     * @return The From attribute.
     */
    public String getFrom() {
        return getAttribute("from");
    } //getFrom


    /**
     * Convenience method to set the From attribute.
     *
     * @param from The String to use for the From attribute.
     */
    public void setFrom(String from) {
        setAttribute("from",from);
    } //setFrom


    /**
     * Convenience method to get the To attribute.
     *
     * @return The To attribute.
     */
    public String getTo() {
        return getAttribute("to");
    } //getTo


    /**
     * Convenience method to set the To attribute.
     *
     * @param to The value for the To attribute.
     */
    public void setTo(String to) {
        setAttribute("to",to);
    } //setTo


    /**
     * Convenience method to get the stream-id attribute.
     *
     * @return The stream-id attribute.
     */
    public String getID() {
        return getAttribute("id");
    } //getID


    /**
     * Convenience method to set the stream-id attribute.
     *
     * @param id The ID to use.
     */
    public void setID(String id) {
        setAttribute("id",id);
    } //setID


    /**
     * Convenience method to get the Type attribute.
     *
     * @return The Type attribute.
     */
    public String getType() {
        return getAttribute("type");
    } //getType


    /**
     * Convenience method to set the Type attribute.
     *
     * @param type The value for the Type attribute.
     */
    public void setType(String type) {
        setAttribute("type",type);
    } //setType
} //class JabberPacket
