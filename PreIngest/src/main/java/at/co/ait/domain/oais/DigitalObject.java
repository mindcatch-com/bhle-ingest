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
import java.util.Observer;
import java.util.UUID;

public class DigitalObject extends GenericObject {

	public DigitalObject(LogGenericObject loggenericobject) {
		addObserver((Observer) loggenericobject);
	}

	/**
	 * Identifier used by content producers to identify the uploaded information
	 * package. Doesn't identify the file.
	 * 
	 */
	//TODO externalIdentifier currently unused 
	// folder name of submission (identifier@submitters site)
	private String externalIdentifier;

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public void setExternalIdentifier(String externalIdentifier) {
		this.externalIdentifier = externalIdentifier;
		notifyObservers("EXTERNAL_ID: " + externalIdentifier);
	}

	/**
	 * Extracted technical metadata of the submitted file. Generated using JHOVE
	 * library.
	 */
	private File techMetadata;

	public File getTechMetadata() {
		return techMetadata;
	}

	public void setTechMetadata(File techMetadata) {
		this.techMetadata = techMetadata;
		setChanged();
		notifyObservers("TECH_METADATA: " + techMetadata.getName());
	}

	/**
	 * DigitalObjectType is primarily used to route message, e.g. only metadata
	 * objects are passed alond to the descriptive mapping service.
	 */
	private DigitalObjectType objecttype;

	public DigitalObjectType getObjecttype() {
		return objecttype;
	}

	public void setObjecttype(DigitalObjectType objecttype) {
		this.objecttype = objecttype;
		setChanged();
		notifyObservers("OBJECT_TYPE: " + objecttype.name());
	}

	// needed for SpEL evaluation
	public Integer getObjecttypeIdx() {
		return objecttype.getIndex();
	}

	/**
	 * File reference to output of SMTService (Schema Mapping Tool)
	 */
	private File smtoutput;

	public File getSmtoutput() {
		return smtoutput;
	}

	public void setSmtoutput(File smtoutput) {
		this.smtoutput = smtoutput;
		setChanged();
		notifyObservers("SCHEMA_MAPPING_TOOL: " + smtoutput.getName());
	}

	private UUID informationPackageUUID;

	public UUID getInformationPackageUUID() {
		return informationPackageUUID;
	}

	public void setInformationPackageUUID(UUID informationPackageUUID) {
		this.informationPackageUUID = informationPackageUUID;
	}

	private File folder;

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}

	private Integer order;

	/** Ordner (unique) number in Mods.
	 * 
	 * @return
	 */
	public Integer getOrder() {
		if(order == null) {
			if(prev == null) {
				order = 0;
			} else {
				order = prev.getOrder() + 1;
			}
			
		}
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	private DigitalObject prev;
	

	/** Set a the previous DO in the chain.
	 * 
	 * If the order Number is not set its automatically a sequence.
	 * 
	 * @param prev Previous object
	 */
	public void setPrev(DigitalObject prev) {
		this.prev = prev;
	}

	/** Previuos DO in the chain.
	 * 
	 * @return the prev
	 */
	public DigitalObject getPrev() {
		return prev;
	}
	
	/**
	 * SMT generated stderr/stdout.
	 */
	private String smtServiceLog;

	public String getSmtServiceLog() {
		return smtServiceLog;
	}

	public void setSmtServiceLog(String smtServiceLog) {
		this.smtServiceLog = smtServiceLog;
	}

	/**
	 * OCR'ed text - can be dirty.
	 */
	private File ocr;

	public File getOcr() {
		return ocr;
	}

	public void setOcr(File ocr) {
		// FIXME check if file exists because tesseract sometimes crashes
		if (ocr.exists()) {
			this.ocr = ocr;
			setChanged();
		}
		notifyObservers("TESSERACT_OCR: " + ocr.getName());
	}

	/**
	 * File reference to taxon finder generated output
	 */
	private File taxa;

	public File getTaxa() {
		return taxa;
	}

	public void setTaxa(File taxa) {
		this.taxa = taxa;
		setChanged();
		notifyObservers("TAXON_FINDER: " + taxa.getName());
	}

	/**
	 * Mimetype of current submittedFile
	 */
	private String mimetype;

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
		setChanged();
		notifyObservers("MIMETYPE: " + mimetype);
	}

	@Override
	public String toString() {
		// TODO use getClass() to build toString()
		return "submitted file: " + getSubmittedFile().getName() + "\n"
				+ "external identifier: " + getExternalIdentifier() + "\n"
				+ "order: " + getOrder() + "\n" + "by: "
				+ getPrefs().getUsername() + "\n";
	}


}
