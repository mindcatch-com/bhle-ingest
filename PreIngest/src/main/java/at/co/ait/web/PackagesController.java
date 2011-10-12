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
package at.co.ait.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import at.co.ait.domain.integration.ProcessingQueue;
import at.co.ait.domain.oais.LogGenericObject;

@Controller
@RequestMapping(value="/packages/*")
public class PackagesController {	
	
	//unused: private static final Logger logger = LoggerFactory.getLogger(PackagesController.class);	
	private @Autowired LogGenericObject loggenericobject;
	private @Autowired ProcessingQueue packagequeue;
	
	@RequestMapping(value="monitor")
	public void monitorHandler() {
	}
    
	@RequestMapping(value="monitor/json", method=RequestMethod.GET, headers="Accept=application/json")
	public @ResponseBody Map<String,List<Map<String,String>>> getObserver(
			@RequestParam String show) {
		Map<String,List<Map<String,String>>> map = 
			new HashMap<String,List<Map<String,String>>>();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>(loggenericobject.getBag());
		Collections.reverse(list);
		map.put("Result",list);
		return map;
	}
	
	@RequestMapping(value="queue")
	public void queueHandler() {
	}
	
	@RequestMapping(value="queue/json", method=RequestMethod.GET, headers="Accept=application/json")
	public @ResponseBody Map<String,Object> getQueue(@RequestParam String show) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("Result",packagequeue.getQueue());
		return map;
	}

}