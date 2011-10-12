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
package at.co.ait.domain.oais;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;

import at.co.ait.web.common.UserPreferences;

import com.google.common.io.Files;

public class GenericObject extends Observable {

	public GenericObject() {
		this.setId(UUID.randomUUID());	
	}

	/**
	 * A unique identifier.
	 */
	private UUID id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.co.ait.domain.oais.IGenericObject#getId()
	 */
	public UUID getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.co.ait.domain.oais.IGenericObject#setId(java.util.UUID)
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * A submitted file.
	 */
	private File submittedFile;

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.co.ait.domain.oais.IGenericObject#getSubmittedFile(java.io.File)
	 */
	public File getSubmittedFile() {
		return submittedFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.co.ait.domain.oais.IGenericObject#setSubmittedFile(java.io.File)
	 */
	public void setSubmittedFile(File submittedFile) {
		this.submittedFile = submittedFile;
		if (!submittedFile.isDirectory()) {
			// calculate Hash Value
			try {
				digestValueinHex = calculateHash(submittedFile);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setChanged();
		notifyObservers("SUBMITTED: " + getSubmittedFile().getName());
	}

	/**
	 * Secure hash (SHA-1) of the submitted file.
	 */
	private String digestValueinHex;

	public String getDigestValueinHex() {
		return digestValueinHex;
	}

	/**
	 * Calculate SHA-1 hash value for submitted file
	 * 
	 * @param fileObj
	 *            Submitted File.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private static final String calculateHash(File fileObj) throws IOException,
			NoSuchAlgorithmException {

		byte[] digest = Files.getDigest(fileObj,
				MessageDigest.getInstance("SHA-1"));

		return (new String(Hex.encodeHex(digest)));

	}

	/**
	 * Contains any user-defined and/or user-specific information used for
	 * preservation.
	 */
	private UserPreferences prefs;

	public UserPreferences getPrefs() {
		return prefs;
	}

	public void setPrefs(UserPreferences prefs) {
		this.prefs = prefs;
	}
	
	/**
	 * Contains reference to this file as URL.
	 */
	private String fileurl;
	
	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}

	public String getFileurl() {
		return fileurl;
	}
	
	
	public void dispose() {
		setChanged();
		notifyObservers("DISPOSED: " + getSubmittedFile().getName());
		deleteObservers();		
	}
	
}
