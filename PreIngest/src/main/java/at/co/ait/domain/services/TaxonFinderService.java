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
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import at.co.ait.domain.integration.ITaxonFinderGateway;
import at.co.ait.domain.oais.DigitalObject;
import at.co.ait.utils.ConfigUtils;

public class TaxonFinderService {

	private @Autowired
	ITaxonFinderGateway taxonfinderGateway;
	private static final Logger logger = LoggerFactory
	.getLogger(TaxonFinderService.class);

	public DigitalObject enrich(DigitalObject obj) {
		if (obj.getOcr() != null) {
			String taxa = null;
			String text = null;
			
			// read OCR from file
			try {
				text = FileUtils.readFileToString(obj.getOcr());
			} catch (IOException e) {
				logger.error("Couldn't read OCR file.");
				return obj;
			}
			
			// URL encode OCR text
			String enc = null;
			try {
				enc = URLEncoder.encode(text, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			
			// send URL encoded OCR text to Taxon Finder gateway
			try {
				taxa = taxonfinderGateway.requestTaxa(enc).get(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				logger.error("Taxonfinder gateway call was interrupted.");
				return obj;
			} catch (ExecutionException e) {
				logger.error("Taxonfinder gateway call wasn't executed properly.");
				return obj;
			} catch (TimeoutException e) {
				logger.error("Taxonfinder gateway call has timed out (max. 10 sec).");
				return obj;
			}
			
			// Taxonfinder reply is XML formatted, check here if it has content 
			Object o = null;
			try {
				o = XPathFactory
						.newInstance()
						.newXPath()
						.evaluate("/child::*/child::*",
								new InputSource(new StringReader(taxa)),
								XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				logger.error("Couldn't apply XPath Expression to Taxonfinder's reply.");
				return obj;
			}
			
			NodeList nodes = (NodeList) o;
			// if ANY names are in the taxon finder's reply, save it to file
			if (nodes.getLength() > 0) {
				File output = ConfigUtils.getAipFile(obj.getPrefs().getBasedirectoryFile(),
						obj.getSubmittedFile(), ".taxa.xml");
				if (!output.exists()) {
					
					try {
						FileUtils.writeStringToFile(output, taxa, "UTF-8");
					} catch (IOException e) {
						logger.error("Couldn't write Taxa file.");
						return obj;
					}
				}
				obj.setTaxa(output);
			}
		}
		return obj;
	}

}