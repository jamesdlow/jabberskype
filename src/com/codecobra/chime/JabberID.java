/*
  JabberID.java
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
 * Represents a jabber-id, consisting of user-name, domain, and resource.
 *
 * @author MW
 */
public class JabberID {
    /** The user-name portion of the jabber-id; optional. */
    private String username;

    /** The domain portion of the jabber-id; required. */
    private String domain;

    /** The resource portion of the jabber-id; optional. */
    private String resource;


    /**
     * Initializes all fields to <code>null</code>.
     */
    public JabberID() {
        setUserName(null);
        setDomain(null);
        setResource(null);
    } //constructor JabberID


    /**
     * Initializes the JabberID object with the given parameters.
     *
     * @param username User-name (or <code>null</code> if not specified)
     * @param domain Domain
     * @param resource Resource (or <code>null</code> if not specified)
     */
    public JabberID(String username,String domain,String resource) {
        setUserName(username);
        setDomain(domain);
        setResource(resource);
    } //constructor JabberID


    /**
     * Returns the user-name portion of the JabberID.
     *
     * @return The user-name portion, or <code>null</code>, if not specified.
     */
    public String getUserName() {
        return username;
    } //getUserName


    /**
     * Sets the user-name portion of the JabberID.
     *
     * @param u The user-name, or <code>null</code>, if not specified.
     */
    public void setUserName(String u) {
        if (u!=null)
            username=u.toLowerCase();
        else
            username=null;
    } //setUserName


    /**
     * Returns the domain portion of the JabberID.
     *
     * @return The domain portion.
     */
    public String getDomain() {
        return domain;
    } //getDomain


    /**
     * Sets the domain portion of the JabberID.
     *
     * @param d The domain.
     */
    public void setDomain(String d) {
        if (d!=null)
            domain=d.toLowerCase();
        else
            domain=null;
    } //setDomain


    /**
     * Get the resource portion of the JabberID.
     *
     * @return The resource portion, or <code>null</code> if not specified.
     */
    public String getResource() {
        return resource;
    } //getResource


    /**
     * Sets the resource portion of the JabberID.
     *
     * @param r The resource, or <code>null</code>, if not specified.
     */
    public void setResource(String r) {
        if (r!=null)
            resource=r.toLowerCase();
        else
            resource=null;
    } //setResource


    /**
     * Converts the JabberID into a string that can be used in the Jabber protocol.
     *
     * @return A <code>String</code> representing the JabberID.
     */
    public String toString() {
        StringBuffer buffer=new StringBuffer();

        if (getUserName()!=null) {
            buffer.append(getUserName());
            buffer.append("@");
        } //username!=null

        if (getDomain()==null)
            buffer.append("ERROR.UNKNOWN.DOMAIN");
        else
            buffer.append(getDomain());

        if (getResource()!=null) {
            buffer.append("/");
            buffer.append(getResource());
        } //resource!=null

        return buffer.toString();
    } //toString


    /**
     * Parses a String to obtain a JabberID.
     *
     * @param s A String representing the jabber-id.
     * @throws NullPointerException If <code>null</code> is provided as the String to be parsed.
     */
    public void parseJID(String s) throws NullPointerException {
        if (s==null)
            throw new NullPointerException();

        int idxAt=s.indexOf('@');
        int idxSlash=s.indexOf('/');

        //user-name portion
        if (idxAt!=-1)
            setUserName(s.substring(0,idxAt));

        //domain portion
        if (idxAt==-1) {
            if (idxSlash==-1)
                setDomain(s);
            else
                setDomain(s.substring(0,idxSlash));
        } else {
            if (idxSlash==-1)
                setDomain(s.substring(idxAt+1));
            else
                setDomain(s.substring(idxAt+1,idxSlash));
        } //idxAt==-1

        //resource portion
        if (idxSlash!=-1)
            setResource(s.substring(idxSlash+1));
    } //parseJID


    /**
     * Creates a new JabberID from a String.
     *
     * @param s A String representing the jabber-id.
     * @return A new JabberID object after parsing the String provided.
     */
    public static JabberID valueOf(String s) {
        JabberID jid=new JabberID();
        jid.parseJID(s);

        return jid;
    } //valueOf
} //class JabberID
