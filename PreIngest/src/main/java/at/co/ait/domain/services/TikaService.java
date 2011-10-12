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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.tika.Tika;

import at.co.ait.domain.oais.DigitalObject;
import at.co.ait.domain.oais.DigitalObjectType;
import at.co.ait.utils.TikaUtils;

public class TikaService {

	public static final Tika tika = new Tika();
	static private List<String> imageMimeTypes = new ArrayList<String>();
	static private List<String> metadataMimeTypes = new ArrayList<String>();
	static private List<String> pdfMimeTypes = new ArrayList<String>();
	static {
		imageMimeTypes.add("image/tiff");
		imageMimeTypes.add("image/jpeg");
		// FIXME: maybe not every text or xml file is metadata
		metadataMimeTypes.add("text/xml");
		metadataMimeTypes.add("application/xml");		
		pdfMimeTypes.add("application/pdf");
		pdfMimeTypes.add("application/octet-stream");
	}

	/**
	 * Identifies the DigitalObject and decides the DigitalObjectType.
	 * 
	 * @param fileObj
	 *            Submitted File.
	 * @return DigitalObjectType
	 */
	public DigitalObjectType detectObjectType(File fileObj) {
		if (fileObj.isFile())
			return decideImageOrMetadata(fileObj);
		if (fileObj.isDirectory())
			return DigitalObjectType.INFORMATIONPACKAGE;
		return null;
	}

	private DigitalObjectType decideImageOrMetadata(File fileObj) {
		String mimeType = TikaUtils.detectedMimeType(fileObj);
		if (imageMimeTypes.contains(mimeType))
			return DigitalObjectType.IMAGE;
		// FIXME metadataMimeTypes isn't particularly needed, but startsWith("text") seems 
		// not too much reliable
		if (metadataMimeTypes.contains(mimeType) ||
			mimeType.startsWith("text"))
			return DigitalObjectType.METADATA;
		if (pdfMimeTypes.contains(mimeType))
				return DigitalObjectType.PDF;
		return DigitalObjectType.UNKNOWN;
	}

	/**
	 * Enriches the digtal object by adding technical metadata
	 * 
	 * @param obj
	 *            DigitalObject is getting enriched by technical metadata.
	 * @return
	 */
	public DigitalObject enrich(DigitalObject obj) {
		obj.setObjecttype(detectObjectType(obj.getSubmittedFile()));
		obj.setMimetype(TikaUtils.detectedMimeType(obj.getSubmittedFile()));
		return obj;
	}

	// FIXME unreliable detection based on Suffix, soon @deprecated.
	public String identify(File fileObject) {

		IOFileFilter content = new SuffixFileFilter(new String[] { "tif",
				"tiff", "jpg", "jpeg" });
		IOFileFilter metadata = new SuffixFileFilter(new String[] { "txt",
				"xml", "mrc", "mrk" });

		String ident = "?";

		// File[] list = fileObject.listFiles((FilenameFilter) new
		// OrFileFilter(content,metadata));
		// for (File i : list) {
		// logger.info(i.getName());
		// }

		if (fileObject.isDirectory()) {

			if ((fileObject.listFiles((FilenameFilter) content).length > 0)
					&& (fileObject.listFiles((FilenameFilter) metadata).length > 0))
				ident = "I";

			if ((fileObject.listFiles((FilenameFilter) content).length == 0)
					&& (fileObject.listFiles((FilenameFilter) metadata).length > 0))
				ident = "T";

		} else {

			ident = "F";

		}
		return ident;
	}

}
