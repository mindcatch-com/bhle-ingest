package at.co.ait.domain.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import at.co.ait.domain.integration.ILoadingGateway;
import at.co.ait.domain.oais.DigitalObject;
import at.co.ait.domain.oais.LogGenericObject;
import at.co.ait.utils.ConfigUtils;
import at.co.ait.web.common.UserPreferences;

public class PreparePackageService {
	
	private static final Logger logger = LoggerFactory
	.getLogger(PreparePackageService.class);
	
	private @Autowired ILoadingGateway loading;
	private @Autowired LogGenericObject loggenericobject;
	private DigitalObject digitalobject;

	public List<DigitalObject> prepare(File folderFileObj, UserPreferences prefs) throws IOException {
		List<DigitalObject> objlist = new ArrayList<DigitalObject>();
		List<File> files = Arrays.asList(folderFileObj.listFiles());
		Collections.sort(files);
	
		DigitalObject prev = null;
		for (File fileObj : files) {
			logger.debug(fileObj.getName());
			DigitalObject obj = new DigitalObject(loggenericobject);
			// warn: prefs needs to be the first setter
			obj.setPrefs(prefs);			
			obj.setFolder(folderFileObj);
			obj.setPrev(prev);
			obj.setSubmittedFile(fileObj);			
			obj.setFileurl(ConfigUtils.createFileURL(fileObj));
			objlist.add(obj);
			prev = obj;
		}	
		return objlist;
	}
}
