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
package at.co.ait.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class ConfigUtils {
	public static final String TMP_PROCESSING = ".aip";
	private static final String WEBAPP_ROOT = Configuration
	.getString("ConfigUtils.0"); //$NON-NLS-1$
	private static final String DATA_ROOT = Configuration
	.getString("DataRoot"); //$NON-NLS-1$

	
	public static File getAipFile(File basedir, File file, String postfix) {
		String outprefix = file.getName();
		File indir = file.isDirectory()? file : file.getParentFile();
		if(file.isDirectory()) {
			// add parent folder to name.
			for(File parentdir = indir.getParentFile();
					parentdir != null && !parentdir.equals(basedir);
					parentdir = parentdir.getParentFile()) {
				outprefix = parentdir.getName() + "_" + outprefix;
			}
		}
		// check if we are already in the aip directory.
		File aip;
		if(indir.getName().equals(TMP_PROCESSING)) {
			aip = indir;
		} else {
			aip = new File(indir, TMP_PROCESSING);
		}
		
		File basename = new File(aip, outprefix + postfix);
		return basename;
	}
	
	public static String createFileURL(File file) throws IOException {
		File[] files = new File[1];
		files[0] = file;
		String fileurl = WEBAPP_ROOT
				+ StringUtils.remove(FileUtils.toURLs(files)[0].toString(),
						DATA_ROOT);
		return fileurl;
	}

}
