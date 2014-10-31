/*
  RosterManager.java
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

import com.skype.*;

/**
 * Provides functionality for managing roster data.
 *
 * @author MW
 */
public class RosterManager {
    /** Reference to the Server object. */
    private Server server;

    /**
     * Initialize the RosterManager.
     * Calls function to load roster data.
     *
     * @param server Reference to the Server object.
     */
    public RosterManager(Server server) {
        this.server=server;
    } //constructor RosterManager
    
    /**
     * Sends a complete copy of the roster to the session.
     * Sends presence information to the session for each member on the roster.
     *
     * @param request The packet that requested the roster reset.
     */
    public void sendRosterReset(JabberPacket request) {
        if (request==null)
            return;

        UserSession session=request.getSession();
        if (session==null)
            return;

        JabberID jid=session.getJID();
        if (jid==null)
            return;

        //need to optimize how we send back information
        //absolutely need to list multiple groups in the same item packet

        //generate a list of unique member names we have
        Friend[] friends;
        try {
        	ContactList list = Skype.getContactList();
        	friends = list.getAllFriends();
        } catch (Exception e) {
        	friends = new Friend[0];
        	e.printStackTrace();
        }

        JabberPacket response=new JabberPacket(null,"iq",null);
        response.setType("result");
        response.setID(request.getID());

        JabberPacket pQuery=response.addChild("query");
        pQuery.setAttribute("xmlns","jabber:iq:roster");

        for (int i=0; i<friends.length; i++) {
        	String skypeID = friends[i].getId();
            JabberID jidMember=new JabberID(friends[i].getId(),server.getServerName(),null);

            JabberPacket pItem=pQuery.addChild("item");
            pItem.setAttribute("jid",jidMember.toString());
            pItem.setAttribute("name",skypeID + "@" + server.getServerName());
            pItem.setAttribute("subscription","to");
            //friends[i].getDisplayName()
            //JabberPacket pGroup=pItem.addChild("group");
            //pGroup.appendValue(friends[i].);
            
        } //a<vMembers.size()


        //send the response to the client
        session.sendPacket(response);

        //update the presence information for the roster
        sendMemberPresence(session);
    } //sendRosterReset

    /**
     * Refreshes the presence information for the members on the owner's roster.
     * This function is invoked when a user first logs into the system: It expects to get presence notifications for each of the users on its roster.
     *
     * @param session The session to which we send the presence information.
     */
    public void sendMemberPresence(UserSession session) {
        if (session==null)
            return;

        JabberID jidOwner=session.getJID();
        if (jidOwner==null)
            return;

        SessionManager mgrSession=server.getSessionManager();

        Friend[] friends;
        try {
        	ContactList list = Skype.getContactList();
        	friends = list.getAllFriends();
        } catch (Exception e) {
        	friends = new Friend[0];
        	e.printStackTrace();
        }

        for (int i=0; i<friends.length; i++) {
        	//sendPresence(friends[i],session);
        	try {
        		sendPresence(friends[i].toString(),friends[i].getOnlineStatus().toString(),session,server);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        } //a<vMember.size()
    } //sendMemberPresence
    public static void sendPresence(String skypeID, String status, UserSession session, Server server) {
    	JabberID jidOwner=session.getJID();

        JabberPacket pPresence=new JabberPacket(null,"presence",null);
        pPresence.setTo(jidOwner.toString());
        	if (status.compareTo(JabberSkypeListeners.SKYPE_OFFLINE) == 0) {
        		pPresence.setType("unavailable");
        	}
        	//JabberPacket pPriority=pPresence.addChild("priority");
        	//pPriority.appendValue(Integer.toString(1));
        	JabberPacket pStatus=pPresence.addChild("status");
        	//pStatus.appendValue(friend.getAbout());
        	if (status.compareTo(JabberSkypeListeners.SKYPE_ONLINE) == 0) {
        		
        		
        	} else {
        		JabberPacket pState=pPresence.addChild("show");
        		pState.appendValue(JabberSkypeListeners.getStatus(status));
        	}
        	
        JabberID jidMember= new JabberID(skypeID,server.getServerName(),server.getServerName());
        if (jidMember!=null) {
            pPresence.setFrom(jidMember.toString());
            session.sendPacket(pPresence);
        }
    }
    /*
    public void sendPresence(Friend friend, UserSession session) {
    	try {
    		JabberID jidOwner=session.getJID();
            String skypeID = friend.getId();
            com.skype.User.Status status = friend.getOnlineStatus();
 
	        JabberPacket pPresence=new JabberPacket(null,"presence",null);
            pPresence.setTo(jidOwner.toString());
            	if (status.compareTo(status.OFFLINE) == 0) {
            		pPresence.setType("unavailable");
            	}
            	//JabberPacket pPriority=pPresence.addChild("priority");
            	//pPriority.appendValue(Integer.toString(1));
            	JabberPacket pStatus=pPresence.addChild("status");
            	pStatus.appendValue(friend.getAbout());
            	if (status.compareTo(status.ONLINE) == 0) {
            		
            		
            	} else {
            		JabberPacket pState=pPresence.addChild("show");
            		pState.appendValue(JabberSkype.getStatus(status));
            	}
            	
            JabberID jidMember= new JabberID(skypeID,server.getServerName(),server.getServerName());
            if (jidMember!=null) {
                pPresence.setFrom(jidMember.toString());
                session.sendPacket(pPresence);
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }*/
} //class RosterManager
