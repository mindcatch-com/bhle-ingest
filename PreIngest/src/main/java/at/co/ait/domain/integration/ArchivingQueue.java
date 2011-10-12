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

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ArchivingQueue contains all generated and ready-to-be ingested AIPs.
 * @author sprogerb
 *
 */
public class ArchivingQueue extends ArrayList<Map<String,String>> {
	
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(ArchivingQueue.class);

    private ObjectMapper m = new ObjectMapper();
    private JsonFactory jf = new JsonFactory();
    
    
    /**
     * Returns a JSON formatted String representation of the current class.
     * @param prettyPrint
     * @return
     */
    public String toJson(boolean prettyPrint) {
        StringWriter sw = new StringWriter();
        JsonGenerator jg = null;
		try {
			jg = jf.createJsonGenerator(sw);
		} catch (IOException e) {
			logger.error("IO Error during attaching StringWriter to JSONGenerator");			
		}
        if (prettyPrint) {
            jg.useDefaultPrettyPrinter();
        }
        try {
			m.writeValue(jg, this);
		} catch (JsonGenerationException e) {
			logger.error("Couldn't created JSON");
		} catch (JsonMappingException e) {
			logger.error("Couldn't map to JSON");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IO Error while writing JSON into StringWriter");
			e.printStackTrace();
		}
        return sw.toString();
    }
}