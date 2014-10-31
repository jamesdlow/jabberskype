package com.codecobra.chime;

import java.io.*;

import com.jameslow.Base64Coder;
import com.skype.Skype;
import com.skype.Friend;

public class VCardHandler implements PacketHandler {
    /** Reference to Server object. */
    private Server server;
    private String filename;

    /**
     * Initializes the handler.
     *
     * @param srv Reference to the server object.
     */
    public VCardHandler(Server srv) {
        this.server=srv;
        String dir = JabberSkype.OS().settingsDir();
        (new File(dir)).mkdirs();
        filename = dir + JabberSkype.OS().fileSeparator() + server.getUserName() + ".png";
    } //constructor IQRosterHandler


    /**
     * Processes the incoming packet.
     *
     * @param packet The JabberPacket.
     */
    public void processPacket(JabberPacket packet) {
    	String type = packet.getAttribute("type");
    	
    	if (type.compareTo("get") == 0) {
    		JabberPacket pVcard = packet.getFirstChild("vcard");
        	if (pVcard != null) {
        		try {
	        		String skypeid = JabberSkypeListeners.removeSuffix(packet.getTo());
	        		Friend friend = Skype.getContactList().getFriend(skypeid);
	        		
	        		JabberPacket response=new JabberPacket(null,"iq",null);
	                response.setType("result");
	                response.setID(packet.getID());
	                	pVcard = response.addChild("vcard");
	                	pVcard.addChild("NICKNAME").appendValue(friend.getFullName());
	                	pVcard.addChild("JABBERID").appendValue(skypeid + "@" + server.getServerName());
	                packet.getSession().sendPacket(response);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        	}
    	} else if (type.compareTo("set") == 0) {
    		JabberPacket pVcard = packet.getFirstChild("vcard");
        	if (pVcard != null) {
    	    	JabberPacket pPhoto = pVcard.getFirstChild("photo");
    	    	if (pPhoto != null) {
    	    		JabberPacket pBinval = pPhoto.getFirstChild("binval");
    	    		if (pBinval != null) {
    	    			try {
        			        BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        			        out.write(Base64Coder.decodeString(pBinval.getValue()));
        			        out.close();
        			        JabberSkypeListeners.setProperty("AVATAR", "1", filename);
        			    } catch (Exception e) {
        			    	e.printStackTrace();
        			    }
        			    JabberPacket response=new JabberPacket(null,"iq",null);
                        response.setType("result");
                        response.setID(packet.getID());
                        packet.getSession().sendPacket(response);
    	    		}
    	    	}
        	}
    	} else {
    		
    	}
    	
    }
}
