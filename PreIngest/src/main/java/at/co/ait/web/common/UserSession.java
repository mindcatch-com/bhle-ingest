/**
 * PreIngest - Metadata preparation tool before archival ingest.
 * Copyright (C) 2011 AIT Forschungsgesellschaft mbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package at.co.ait.web.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserSession implements IUserSession {
	private static final Logger logger = LoggerFactory.getLogger(UserSession.class);
	private UserPreferences prefs;
	private String smtparams;
	private String username;
	
	/* (non-Javadoc)
	 * @see at.co.ait.web.common.IUserSession#init()
	 */
	public void init() {
		logger.debug("init usersession");
	}

	/* (non-Javadoc)
	 * @see at.co.ait.web.common.IUserSession#getPrefs()
	 */
	public UserPreferences getPrefs() {
		return (UserPreferences) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/* (non-Javadoc)
	 * @see at.co.ait.web.common.IUserSession#setPrefs(at.co.ait.web.common.UserPreferences)
	 */
	public void setPrefs(UserPreferences prefs) {
		this.prefs = prefs;
	}
	
	/* (non-Javadoc)
	 * @see at.co.ait.web.common.IUserSession#getSmtparams()
	 */
	public String getSmtparams() {
		logger.debug("getting smtparams");
		logger.debug(getPrefs().getSmtparams());
		return getPrefs().getSmtparams();
	}
	
	/* (non-Javadoc)
	 * @see at.co.ait.web.common.IUserSession#getUsername()
	 */
	public String getUsername() {
		return getPrefs().getUsername();
	}
	
}
