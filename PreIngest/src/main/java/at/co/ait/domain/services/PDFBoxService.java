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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;

import at.co.ait.domain.oais.DigitalObject;
import at.co.ait.domain.oais.LogGenericObject;
import at.co.ait.utils.ConfigUtils;
import at.co.ait.utils.PDFPageImageWriter;
import at.co.ait.utils.TikaUtils;

@MessageEndpoint
public class PDFBoxService {
	private static final Logger logger = LoggerFactory.getLogger(PDFBoxService.class);

	private @Autowired LogGenericObject loggenericobject;
	
	public List<DigitalObject> extract(List<DigitalObject> doList) throws IOException {
		ArrayList<DigitalObject> ret = new ArrayList<DigitalObject>();
        DigitalObject prev = null;
		for(DigitalObject obj : doList) {
			if("application/pdf".equals(TikaUtils.detectedMimeType(obj.getSubmittedFile()))) {
				logger.debug("PDFBox on " + obj.getSubmittedFile().getAbsolutePath());
				
				PDDocument doc = PDDocument.load(obj.getSubmittedFile());
				
								
				int resolution = 96; // TODO add default DPI to User options.
	            String prefix = obj.getSubmittedFile().getName().replace(".pdf", "_pdf_");
				File outputDir = new File(obj.getSubmittedFile().getParentFile(), ".aip");
				if(!outputDir.exists()) {
					outputDir.mkdirs();
				}
				
				List<?> pages = doc.getDocumentCatalog().getAllPages();
				PDFPageImageWriter pageImgWr = new PDFPageImageWriter();
				for(int pageNo = 1; pageNo <= pages.size(); ++pageNo ) {
					logger.debug("Extracting page " + pageNo + 
								" from " + obj.getSubmittedFile().getName());
					File targetImg = new File(outputDir, prefix + pageNo + ".jpg");
					File targetTxt = new File(outputDir, prefix + pageNo + ".jpg.txt");
					PDPage page = (PDPage) pages.get(pageNo - 1);
					pageImgWr.pageToImage(page, targetImg, "JPEG", resolution);
					
					PDFTextStripper stripper = new PDFTextStripper();
					stripper.setStartPage(pageNo);
					stripper.setEndPage(pageNo);
					String text = stripper.getText(doc);
					FileUtils.writeStringToFile(targetTxt, text, "UTF-8");
					
					if(targetImg.isFile()) {
						DigitalObject dobj = new DigitalObject(loggenericobject);
		    			dobj.setPrefs(obj.getPrefs());			
		    			dobj.setFolder(outputDir);
		    			dobj.setPrev(prev);
		    			dobj.setSubmittedFile(targetImg);			
		    			dobj.setFileurl(ConfigUtils.createFileURL(targetImg));
		    			ret.add(dobj);
		    			prev = dobj;
					}
				}
				
			} else {
				logger.debug("PDFBox passing on " + obj.getSubmittedFile().getAbsolutePath());
				ret.add(obj);
				obj.setPrev(prev);
				prev = obj;
			}
			if(prev == null) prev = obj;
		}
		return ret;
	}
	
}