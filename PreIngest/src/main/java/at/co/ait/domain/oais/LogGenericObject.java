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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.co.ait.utils.DateUtils;

public class LogGenericObject implements Observer {

	private static final Logger logger = LoggerFactory
			.getLogger(LogGenericObject.class);

	private List<Map<String, String>> bag = new ArrayList<Map<String, String>>();

	public List<Map<String, String>> getBag() {
		return bag;
	}

	public void update(Observable o, Object arg) {

		Map<String, String> info = null;		
		final int MAX = 100;
		String user = null;
		String filename = null;
		String parentfoldername = null;

		int hashcode = 0;

		if (o instanceof DigitalObject) {
			user = ((DigitalObject) o).getPrefs().getUsername();
			hashcode = ((DigitalObject) o).getSubmittedFile().hashCode();
			filename = ((DigitalObject) o).getSubmittedFile().getName();
			parentfoldername = ((DigitalObject) o).getSubmittedFile()
					.getParentFile().getName();
		}
		if (o instanceof InformationPackageObject) {
			user = ((InformationPackageObject) o).getPrefs().getUsername();
			hashcode = ((InformationPackageObject) o).getSubmittedFile()
					.hashCode();
			filename = ((InformationPackageObject) o).getSubmittedFile()
					.getName();
			parentfoldername = ((InformationPackageObject) o)
					.getSubmittedFile().getParentFile().getName();
		}

		info = new HashMap<String, String>();
		info.put("timestamp", new Date().toString());
		info.put("displaydate",DateUtils.now());
		info.put("parentfolder", parentfoldername);
		info.put("filename", filename);
		info.put("hashcode", String.valueOf(hashcode));
		info.put("username", user);
		info.put("observation", arg.toString());
		bag.add(info);
		
		if (bag.size() > MAX) {
			// delete first entry to keep size at MAX
			bag.remove(0);
		}		
	}

}