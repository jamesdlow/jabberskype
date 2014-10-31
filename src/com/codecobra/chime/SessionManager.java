/*
  SessionManager.java
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
import java.util.Vector;

/**
 * Keeps track of UserSession objects.
 * Allows delivery handlers to properly route packages.
 *
 * @author MW
 */
public class SessionManager {
    /** Reference to Server object. */
    private Server server;

    /**
     * List of sessions by user-name.
     * Each value is a Vector that lists the sessions.
     */
    private HashMap mapUserName;


    /**
     * Initializes the SessionManager.
     *
     * @param server Reference to the Server object.
     */
    public SessionManager(Server server) {
        this.server=server;

        mapUserName=new HashMap();
    } //constructor SessionManager


    /**
     * Adds the session to the list of known sessions.
     *
     * @param session The session to add.
     */
    public void addSession(UserSession session) {
        //TODO this doesn't seem thread-safe
        String userName=session.getJID().getUserName();

        Vector v=(Vector)mapUserName.get(userName);

        if (v==null) {
            v=new Vector();

            v.add(session);

            mapUserName.put(userName,v);
        } else {
            if (!v.contains(session))
                v.add(session);
        } //v==null
    } //addSession


    /**
     * Removes a registered session.
     * If the session's presence is not flagged as unavailable, the procedure sets the presence to unavailable and distributes that information to subscribers.
     *
     * @param session The UserSession object to remove.
     */
    public void removeSession(UserSession session) {
        //TODO this doesn't seem thread-safe
        if (session==null)
            return;

        JabberID jid=session.getJID();
        if (jid==null)
            return;

        //check presence availability
        if (session.getPresenceAvailability()!=UserSession.PRESENCE_AVAILABILITY_UNAVAILABLE) {
            //presence not marked as unavailable; set it manually and distribute;
            session.setPresenceAvailability(UserSession.PRESENCE_AVAILABILITY_UNAVAILABLE);
            session.setPresenceState(UserSession.PRESENCE_STATE_XA);
        } //presence availability

        String userName=jid.getUserName();

        Vector v=(Vector)mapUserName.get(userName);

        if (v==null)
            server.getLogger().warning("Unable to remove session, user-name not registered: "+session.getJID().toString());
        else {
            //did find an entry for that user-name
            if (!v.remove(session))
                server.getLogger().warning("Unable to remove session, session not found: "+session.getJID().toString());
            else {
                //was able to remove the session, check if we should remove the user-name altogether
                if (v.size()<=0)
                    mapUserName.remove(userName);
            } //remove(session)
        } //v==null
    } //removeSession


    /**
     * Obtains a list of UserSession objects for a given user-name.
     *
     * @param user The user-name.
     * @return An array containing the sessions for this user.
     * @throws NullPointerException If <code>null</code> was provided as the user-name.
     */
    public UserSession[] getSessionsByUserName(String user) throws NullPointerException {
        if (user==null)
            throw new NullPointerException("You must specify a user-name.");

        Vector v=(Vector)mapUserName.get(user);

        if (v==null)
            return new UserSession[0];

        UserSession[] us=new UserSession[v.size()];
        v.toArray(us);

        return us;
    } //getSessionsByUserName
    
    public void removeAll() {
    	UserSession[] sessions = getSessionsByUserName(server.getUserName());
    	for (int i=0; i<sessions.length; i++){
    		try {
    			sessions[i].getSocket().close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		mapUserName=new HashMap();
    	}
    }
} //class SessionManager
