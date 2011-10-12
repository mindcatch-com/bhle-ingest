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
package at.co.ait.domain.services;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import at.co.ait.domain.integration.ArchivingQueue;
import at.co.ait.domain.oais.DigitalObject;
import at.co.ait.domain.oais.InformationPackageObject;
import at.co.ait.utils.DateUtils;

public class WrapUpService {

	private @Autowired ArchivingQueue archivingqueue;

	public InformationPackageObject wrapup(InformationPackageObject obj) {

		// store aip information in queue for further archive
		// can be retrieved by 3rd party
		String path = obj.getSubmittedFile().getAbsolutePath();
		LinkedHashMap<String, String> line = new LinkedHashMap<String, String>();
		line.put("date", DateUtils.now());
		line.put("path", path);
		line.put("user", obj.getPrefs().getUsername());
		line.put("metsfile", obj.getMetsfileurl());
		archivingqueue.add(line);

		// clean up & remove all observers by disposing
		for (DigitalObject digobj : obj.getDigitalobjects()) {
			digobj.dispose();
		}
		obj.dispose();
		return obj;
	}
}