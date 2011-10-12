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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.co.ait.domain.oais.DigitalObject;
import at.co.ait.utils.ConfigUtils;
import at.co.ait.utils.Configuration;
import at.co.ait.web.common.UserPreferences;

public class SMTService extends ProcessbuilderService {

	private static final Logger logger = LoggerFactory
			.getLogger(SMTService.class);

	private List<String> commands;
	private UserPreferences prefs;

	public DigitalObject map(DigitalObject obj, String params)
			throws MalformedURLException, IOException, InterruptedException {

		String tmpfile = ConfigUtils.getAipFile(obj.getPrefs().getBasedirectoryFile(), obj.getSubmittedFile(),
				Configuration.getString("SMTService.tmpfilepostifx")).getAbsolutePath(); //$NON-NLS-1$
		String srcfile = obj.getSubmittedFile().getAbsolutePath();
		URL smtexec = new URL(Configuration.getString("SMTService.smt")); //$NON-NLS-1$

		commands = new ArrayList<String>();
		commands.add(Configuration.getString("SMTService.javaexec")); //$NON-NLS-1$
		commands.add(Configuration.getString("SMTService.javaparam")); //$NON-NLS-1$
		commands.add(smtexec.getPath());
		for (String param : params.split(Configuration
				.getString("SMTService.smtparam_splitstring"))) { //$NON-NLS-1$
			commands.add(param);
		}
		commands.add(Configuration.getString("SMTService.smtparam_inputfile")); //$NON-NLS-1$
		commands.add(srcfile);
		commands.add(Configuration.getString("SMTService.smtparam_outputfile")); //$NON-NLS-1$
		commands.add(tmpfile);
		File out = new File(tmpfile);
		if (!out.exists()) {
			process(commands);
			obj.setSmtServiceLog(stdout.toString());
			logger.debug("SMT: " + out.getName() + "STDOUT: " + stdout.toString());
			logger.debug("SMT: " + out.getName() + "STDERR: " + stderr.toString());
		}
		obj.setSmtoutput(out);		
		return obj;
	}

}
