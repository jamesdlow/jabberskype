package com.codecobra.chime;

import com.skype.SkypeException;

public interface UserStatusListener {
	public void statusChanged(String skypeID, String status) throws SkypeException;
}
