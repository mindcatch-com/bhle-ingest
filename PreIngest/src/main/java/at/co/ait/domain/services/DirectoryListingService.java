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
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;

import at.co.ait.utils.ConfigUtils;
import at.co.ait.web.common.UserPreferences;

public class DirectoryListingService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private File basedir = null; 
	private Map<Integer,String> visitedFilesAndFolders = new HashMap<Integer,String>();
	private UserPreferences pref;
	//unused: private static final Logger logger = LoggerFactory.getLogger(DirectoryListingService.class);
	
	public void init() throws MalformedURLException, IOException
	{
		pref = (UserPreferences) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Resource res = new UrlResource(pref.getBasedirectory());
		setBasedir(res.getFile());
		visitedFilesAndFolders.put(basedir.hashCode(),
    			basedir.getAbsolutePath());
		addFolderContent(basedir);
	}
	
	public File getBasedir() {		
		return basedir;
	}
	
	public void setBasedir(File basedir) {
		this.basedir = basedir;
	}
	
	public File getFileByKey(Integer key) {
		return new File(visitedFilesAndFolders.get(key));
	}
	
	/**
	 * Read all files and folders from a directory.
	 * @param dir AbsolutePath of directory.
	 * @return Collection of Files.
	 */
	public Collection<File> readFilesAndFoldersFromDir(String dir) {
		File[] filesAndFolders = (new File(dir)).listFiles();
		Collection<File> coll = new ArrayList<File>();
		for (File f : filesAndFolders) {
			coll.add(f);
		}
		return coll;
	}
	
	public void addFolderContent(File folder) {
		File[] filesAndFolders = folder.listFiles();
		if(filesAndFolders == null) {
			throw new RuntimeException("Not a folder or does not exist: " + folder.getAbsolutePath());
		}
		for (File f : filesAndFolders) {			
			if (f.isDirectory()) {
			visitedFilesAndFolders.put(f.hashCode(),
        			f.getAbsolutePath());
			}
		}
	}
	
	/**
	 * A directory tree contains a list of files and folders where each
	 * list's entry contains a map with specific entries used by Dynatree.
	 * @param key 32-bit Integer hashcode of a fileobject which returns the selected directory
	 * @return list of maps which can be serialized into JSON
	 */
	public List<Map<String,Object>> buildJSONMsgFromDir(String key) {
		String dir;		
		// lookup key in a list of all files and folders to retrieve the
		// corresponding folder
		if (key==null) {
			dir=visitedFilesAndFolders.get(basedir.hashCode());
		} else {			
			dir=visitedFilesAndFolders.get(Integer.valueOf(key));
		}
		// add new folders to visitedFilesAndFolders
		addFolderContent(new File(dir));
		
		// build list of files and folders to be returned
		List<Map<String,Object>> elements = new ArrayList<Map<String,Object>>();		
		Collection<File> files = readFilesAndFoldersFromDir(dir);
		ArrayList<File> filelist = new ArrayList<File>(files);
		Collections.sort(filelist);
		for (File file : filelist) {
			Map<String,Object> map = new HashMap<String, Object>();
			// subtract the basedir from the current dir
			map.put("title", StringUtils.remove(file.toString(), dir + File.separator));
			// assign folder properties for dynatree
			if(file.isDirectory()) {
				map.put("isFolder", file.isDirectory());	
				map.put("isLazy", true);
				if(file.getName().equals(ConfigUtils.TMP_PROCESSING)) {
					map.put("unselectable", true);
					map.put("hideCheckbox", true);	
				}
			} else {	
				map.put("unselectable", true);
				map.put("hideCheckbox", true);				
			}
			// assign 32-bit integer hashcode of absolute pathname
			map.put("key", String.valueOf(file.hashCode()));
			elements.add(map);
		}
		return elements;
	}
	

	
}
