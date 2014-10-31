package com.codecobra.chime;

import com.skype.ApplicationListener;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.SkypeException;

public class JabberSkypeListener implements ChatMessageListener, UserStatusListener {
	private Server server;
	
	public JabberSkypeListener(Server server) {
		this.server = server;
	}
	public void chatMessageReceived(ChatMessage arg0) throws SkypeException {
		UserSession[] sessions = server.getSessionManager().getSessionsByUserName(server.getUserName());
		for (int i=0; i < sessions.length; i++ ) {
			String msg = arg0.getContent();
			if ("".compareTo(msg) != 0) {
				JabberPacket message = new JabberPacket(null,"message",null);
				String from = arg0.getSender().getId();
				message.setTo(sessions[i].getJID().toString());
				message.setFrom(from + "@" + server.getServerName());
				/*String id = sessions[i].getChatID(from);
				if ("".compareTo(id) != 0) {
					message.setID(id);
				}*/
				JabberPacket body =  message.addChild("body");
					body.appendValue(msg);
					sessions[i].sendPacket(message);
			}
		}
	}

	public void chatMessageSent(ChatMessage arg0) throws SkypeException {
		
	}
	public void statusChanged(String skypeID, String status) throws SkypeException {
		UserSession[] sessions = server.getSessionManager().getSessionsByUserName(server.getUserName());
		for (int i=0; i < sessions.length; i++ ) {
			server.getRosterManager().sendPresence(skypeID, status, sessions[i], server);
		}
	}
}
