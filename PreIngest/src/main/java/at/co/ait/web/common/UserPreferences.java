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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;


public class UserPreferences implements UserDetails {

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String basedirectory;
	private String workdirectory;
	private String organization;
	private List<String> roles;
	private List<GrantedAuthority> AUTHORITIES;
	private String smtparams;
	
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	public String getSmtparams() {
		return smtparams;
	}
	public void setSmtparams(String smtparams) {
		this.smtparams = smtparams;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public String getBasedirectory() {
		return basedirectory;
	}	
	public File getBasedirectoryFile() {
		URI u;
		try {
			u = new URI(basedirectory);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Configuration error in option 'basedirectory'", e);
		}
		return new File(u);
	}	
	public void setBasedirectory(String basedirectory) {
		this.basedirectory = basedirectory;
	}
	public String getWorkdirectory() {
		return workdirectory;
	}
	public void setWorkdirectory(String workdirectory) {
		this.workdirectory = workdirectory;
	}
	
	public Collection<GrantedAuthority> getAuthorities() {
		// XStream doesn't serialize AUTHORITIES so it needs to be initialized
		if (AUTHORITIES==null || AUTHORITIES.isEmpty()) {
			AUTHORITIES = new ArrayList<GrantedAuthority>();
			for (String role: this.roles) {
				AUTHORITIES.add(new GrantedAuthorityImpl(role));
			}
		}
		return AUTHORITIES;
	}
	
	public boolean isAccountNonExpired() {
		return true;
	}
	public boolean isAccountNonLocked() {
		return true;
	}
	public boolean isCredentialsNonExpired() {
		return true;
	}
	public boolean isEnabled() {
		return true;
	}
}
