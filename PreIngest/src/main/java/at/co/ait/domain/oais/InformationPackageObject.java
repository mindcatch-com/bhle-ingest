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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.apsr.mtk.base.METSWrapper;

/**
 * An OAIS Information Package (IP) contains all relevant data that needs to be
 * preserved together as one entity.
 * 
 * @author sprogerb
 * 
 */
public class InformationPackageObject extends GenericObject {
	
	public InformationPackageObject(LogGenericObject loggenericobject) {
		addObserver((Observer)loggenericobject);
	}

	/**
	 * IP holds all submitted files.
	 */
	private List<DigitalObject> digitalobjects = new ArrayList<DigitalObject>();

	public List<DigitalObject> getDigitalobjects() {
		return digitalobjects;
	}

	public void setDigitalobjects(List<DigitalObject> digitalobjects) {
		this.digitalobjects = digitalobjects;
	}

	private Set<UUID> digitalObjectUUID = new HashSet<UUID>();
	
	public void addDigitalObjectUUID(UUID id) {
		logger.debug(id.toString());
		digitalObjectUUID.add(id);
	}
	
	public void removeDigitalObjectUUID(UUID id) {
		digitalObjectUUID.remove(id);
	}
	
	private static final Logger logger = LoggerFactory
	.getLogger(InformationPackageObject.class);
	
	public Boolean isReadyForDelivering() {		
		Boolean returnVal = false;
		logger.debug(String.valueOf(digitalObjectUUID.size()));
		if (digitalObjectUUID.size() == 0) returnVal = true;
		return returnVal;
	}
	
	/**
	 * Identifier stores minted nice opaque id (NOID).
	 */
	private String identifier;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
		setChanged();
		notifyObservers("PERMANENT_ID: " + identifier);
	}

	/**
	 * An external identifier is created based on the containing digital
	 * objects' parent folder name.
	 */
	private String externalIdentifier;

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public void setExternalIdentifier(String externalIdentifier) {
		this.externalIdentifier = externalIdentifier;
		setChanged();
		notifyObservers("EXTERNAL_ID: " + externalIdentifier);
	}

	/**
	 * IP stores all information in METS.
	 */
	private METSWrapper mets;

	public METSWrapper getMets() {
		return mets;
	}

	public void setMets(METSWrapper mets) {
		this.mets = mets;
		setChanged();
		notifyObservers("METS: " + getSubmittedFile().getName());	
	}

	public void addDigitalObject(DigitalObject obj) {
		getDigitalobjects().add(obj);
		setChanged();
		notifyObservers("ADDED: " + obj.getSubmittedFile().getName());
	}
	
	/**
	 * Store virusscan results for folder
	 */
	private File scanlog;
	
	public File getScanlog() {
		return scanlog;
	}

	public void setScanlog(File scanlog) {
		this.scanlog = scanlog;
		setChanged();
		notifyObservers("VIRUSSCAN_LOG: " + scanlog.getName());
	}

	private File nepomukFileOntology;

	public File getNepomukFileOntology() {
		return nepomukFileOntology;
	}

	public void setNepomukFileOntology(File nepomukFileOntology) {
		this.nepomukFileOntology = nepomukFileOntology;
		setChanged();
		notifyObservers("NEPOMUK_FILE_ONTOLOGY: " + nepomukFileOntology.getName());
	}
	
	private String metsfileurl;
	
	public void setMetsfileurl(String metsfileurl) {
		this.metsfileurl = metsfileurl;
	}

	public String getMetsfileurl() {
		return metsfileurl;
	}	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		// TODO use getClass() to build toString()
		return "noid identifer: " + getIdentifier() + "\n"
				+ "external identifier: " + getExternalIdentifier() + "\n"
				+ "digitalobject count: " + digitalobjects.size() + "\n";
	}

}
