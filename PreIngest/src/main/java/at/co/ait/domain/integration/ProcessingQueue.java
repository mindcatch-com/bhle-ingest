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
package at.co.ait.domain.integration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import at.co.ait.domain.oais.InformationPackageObject;
import at.co.ait.utils.DateUtils;

public class ProcessingQueue extends LinkedHashMap<Integer,LinkedHashMap<String,String>> {
	
	private static final long serialVersionUID = 1L;
	
	//TODO: make this queue persistent so that folders can be loaded again on restart
	//TODO: implement spring event so that folders are loaded again on restart
	
	//unused: private static final Logger logger = LoggerFactory.getLogger(ProcessingQueue.class);
	
	public void handle(Object payload, String username) {
		// folder is entering integration queue
		if (payload instanceof File) 
		{
			String path = ((File) payload).getAbsolutePath();
			Integer key = ((File) payload).hashCode();
			Long foldersize = FileUtils.sizeOfDirectory((File) payload);
			LinkedHashMap<String,String> line = new LinkedHashMap<String,String>();
			line.put("date",DateUtils.now());
			line.put("path",path);
			// KB, MB, GB of files in folder
			line.put("size",FileUtils.byteCountToDisplaySize(foldersize));
			// number of files in folder
			File[] files = ((File) payload).listFiles();
			line.put("files",String.valueOf(files.length));
			line.put("user",username);			
			put(key, line);
		}
		// folder is exiting integration queue (as InformationPackageObject)
		else
		{
			Integer key = ((InformationPackageObject) payload).getSubmittedFile().hashCode();
			remove(key);
		}		
	}	
	
	public List<Map<String,String>> getQueue() {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		for (LinkedHashMap<String,String> line : this.values()) {
			list.add(line);
		}
		return list;
	}
	
}
