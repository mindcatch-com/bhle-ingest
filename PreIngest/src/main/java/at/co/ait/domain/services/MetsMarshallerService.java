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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import at.co.ait.domain.oais.DigitalObject;
import at.co.ait.domain.oais.DigitalObjectType;
import at.co.ait.domain.oais.InformationPackageObject;
import at.co.ait.utils.ConfigUtils;
import at.co.ait.web.common.UserPreferences;
import au.edu.apsr.mtk.base.Agent;
import au.edu.apsr.mtk.base.Div;
import au.edu.apsr.mtk.base.DmdSec;
import au.edu.apsr.mtk.base.FLocat;
import au.edu.apsr.mtk.base.FileGrp;
import au.edu.apsr.mtk.base.FileSec;
import au.edu.apsr.mtk.base.METS;
import au.edu.apsr.mtk.base.METSException;
import au.edu.apsr.mtk.base.METSWrapper;
import au.edu.apsr.mtk.base.MdWrap;
import au.edu.apsr.mtk.base.MetsHdr;
import au.edu.apsr.mtk.base.StructMap;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class MetsMarshallerService {

	private static final Logger logger = LoggerFactory.getLogger(MetsMarshallerService.class);
	
	private METS mets = null;

	private METSWrapper createMETS(InformationPackageObject obj,
			UserPreferences prefs) throws JDOMException,
			ParserConfigurationException, METSException, IOException,
			SAXException {

		METSWrapper mw = null;
		mw = new METSWrapper();

		mets = mw.getMETSObject();		
		mets.setObjID(String.valueOf(obj.getIdentifier()));
		mets.setProfile("http://www.bhl-europe.eu/profiles/bhle-mets-profile-1.0"); //$NON-NLS-1$
		mets.setType("investigation"); //$NON-NLS-1$

		MetsHdr mh = mets.newMetsHdr();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //$NON-NLS-1$
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1")); //$NON-NLS-1$
		String currentTime = df.format(cal.getTime());
		mh.setCreateDate(currentTime);
		mh.setRecordStatus("Complete"); //$NON-NLS-1$		

		Agent agent = mh.newAgent();
		agent.setRole("ARCHIVIST"); //$NON-NLS-1$
		agent.setType("ORGANIZATION"); //$NON-NLS-1$
		agent.setName(prefs.getOrganization());
		agent.setNote("BHL-Europe Content Provider"); //$NON-NLS-1$
		mh.addAgent(agent);

		agent = mh.newAgent();
		agent.setRole("ARCHIVIST"); //$NON-NLS-1$
		agent.setType("OTHER"); //$NON-NLS-1$
		agent.setName("BHL-Europe Pre-Ingest Tool"); //$NON-NLS-1$
		agent.setNote("Automatic processing of submitted content"); //$NON-NLS-1$
		mh.addAgent(agent);
		mets.setMetsHdr(mh);

		DmdSec dmd = null;
		// create dmd entry for each METADATA file
		for (DigitalObject digobj : obj.getDigitalobjects()) {
			if (digobj.getObjecttype().equals(DigitalObjectType.METADATA)) {
				dmd = newDmdEntry(digobj.getOrder(), "MODS", "derivative", //$NON-NLS-1$ //$NON-NLS-2$
						digobj.getSmtoutput());
				mets.addDmdSec(dmd);
			}
		}

		FileSec fs = mets.newFileSec();

		// create filegroup for each digitalobjecttype such as METADATA, IMAGE
		for (DigitalObjectType value : DigitalObjectType.values()) {
			FileGrp fg = fs.newFileGrp();
			fg.setUse(value.name());

			for (DigitalObject digobj : obj.getDigitalobjects()) {
				if (digobj.getObjecttype().equals(value)) {
					au.edu.apsr.mtk.base.File f = fg.newFile();
					f.setID("F-" + digobj.getOrder()); //$NON-NLS-1$
					f.setSize(FileUtils.sizeOf(digobj.getSubmittedFile()));
					f.setMIMEType(digobj.getMimetype());
					f.setChecksum(digobj.getDigestValueinHex());
					f.setChecksumType("SHA-1"); //$NON-NLS-1$
					FLocat loc = createLocat(digobj.getSubmittedFile(),
							prefs.getBasedirectory(), f);					
					f.addFLocat(loc);
					fg.addFile(f);
				}
			}
			// if filegroup contains any files, add to filesection
			if (fg.getFiles().size() > 0)
				fs.addFileGrp(fg);
		}

		// add taxa files to filesection
		FileGrp fg = fs.newFileGrp();
		fg.setUse("TAXA"); //$NON-NLS-1$
		for (DigitalObject digobj : obj.getDigitalobjects()) {
			if (digobj.getTaxa() != null) {
				au.edu.apsr.mtk.base.File f = fg.newFile();
				f.setID("F-" + digobj.getOrder()); //$NON-NLS-1$
				f.setSize(FileUtils.sizeOf(digobj.getTaxa()));
				FLocat loc = createLocat(digobj.getTaxa(),
						prefs.getBasedirectory(), f);	
				f.addFLocat(loc);
				fg.addFile(f);
			}
			// if filegroup contains any files, add to filesection
			if (fg.getFiles().size() > 0)
				fs.addFileGrp(fg);
		}

		// add ocr files to filesection
		fg = fs.newFileGrp();
		fg.setUse("OCR"); //$NON-NLS-1$
		for (DigitalObject digobj : obj.getDigitalobjects()) {
			if (digobj.getOcr() != null) {
				au.edu.apsr.mtk.base.File f = fg.newFile();
				f.setID("F-" + digobj.getOrder()); //$NON-NLS-1$
				f.setSize(FileUtils.sizeOf(digobj.getOcr()));
				FLocat loc = createLocat(digobj.getOcr(),
						prefs.getBasedirectory(), f);	
				f.addFLocat(loc);
				fg.addFile(f);
			}
			// if filegroup contains any files, add to filesection
			if (fg.getFiles().size() > 0)
				fs.addFileGrp(fg);
		}

		// add jhove files to filesection
		fg = fs.newFileGrp();
		fg.setUse("JHOVE"); //$NON-NLS-1$
		for (DigitalObject digobj : obj.getDigitalobjects()) {
			if (digobj.getTechMetadata() != null) {
				au.edu.apsr.mtk.base.File f = fg.newFile();
				f.setID("F-" + digobj.getOrder()); //$NON-NLS-1$
				f.setSize(FileUtils.sizeOf(digobj.getTechMetadata()));
				FLocat loc = createLocat(digobj.getTechMetadata(),
						prefs.getBasedirectory(), f);	
				f.addFLocat(loc);
				fg.addFile(f);
			}
			// if filegroup contains any files, add to filesection
			if (fg.getFiles().size() > 0)
				fs.addFileGrp(fg);
		}

		// add rdf nfo file to filesection
		fg = fs.newFileGrp();
		fg.setUse("NFO"); //$NON-NLS-1$
		au.edu.apsr.mtk.base.File f = fg.newFile();
		f.setID("F-NFO"); //$NON-NLS-1$
		f.setSize(FileUtils.sizeOf(obj.getNepomukFileOntology()));
		FLocat loc = createLocat(obj.getNepomukFileOntology(),
				prefs.getBasedirectory(), f);	
		f.addFLocat(loc);
		fs.addFileGrp(fg);

		mets.setFileSec(fs);

		StructMap sm = mets.newStructMap();

		Div d = sm.newDiv();
		sm.addDiv(d);

		mets.addStructMap(sm);

		mw.validate();

		return mw;
	}

	private DmdSec newDmdEntry(int id, String type, String lbl, File metadata)
			throws METSException, JDOMException, IOException {
		SAXBuilder parser = new SAXBuilder();
		org.jdom.Document modsdoc = parser.build(metadata);
		DmdSec dmd = mets.newDmdSec();
		dmd.setID(lbl + "-" + id); //$NON-NLS-1$
		MdWrap mdw = dmd.newMdWrap();
		mdw.setMDType(type);
		mdw.setLabel(lbl);
		DOMOutputter domOutputter = new DOMOutputter();
		org.w3c.dom.Document w3cDoc = domOutputter.output(modsdoc);
		mdw.setXmlData(w3cDoc.getDocumentElement());
		dmd.setMdWrap(mdw);
		return dmd;
	}

	private FLocat createLocat(File file, String basedir,
			au.edu.apsr.mtk.base.File f) throws METSException, IOException {
		String fileurl = ConfigUtils.createFileURL(file);
		FLocat loc = f.newFLocat();
		loc.setHref(fileurl);
		loc.setLocType("URL"); //$NON-NLS-1$		
		return loc;
	}

	/**
	 * Enriches the information package object by adding METS metadata
	 * 
	 * @param obj
	 * @return
	 * @throws ParserConfigurationException
	 * @throws JDOMException
	 * @throws IOException
	 * @throws SAXException
	 * @throws METSException
	 */
	public InformationPackageObject marshal(InformationPackageObject obj,
			UserPreferences prefs) throws JDOMException,
			ParserConfigurationException, IOException, METSException,
			SAXException {
		logger.info("METS Marshaller");
		obj.setMets(createMETS(obj, prefs));
		
		File metsfile = ConfigUtils.getAipFile(prefs.getBasedirectoryFile(),
				obj.getSubmittedFile(), ".mets.xml"); //$NON-NLS-1$
		obj.setMetsfileurl(ConfigUtils.createFileURL(metsfile));
		Document doc = obj.getMets().getMETSDocument();
		OutputFormat format = new OutputFormat(doc);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		FileOutputStream out = FileUtils.openOutputStream(metsfile);
		XMLSerializer serializer = new XMLSerializer(out, format);
		serializer.serialize(doc);
		out.close();
		return obj;
	}

}
