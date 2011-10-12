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

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import at.co.ait.domain.oais.DigitalObject;

public class ConvertImageService {
	private DigitalObject digitalobject;
	private String imPath="C:\\Utilities\\ImageMagick-6.6.7-Q16";
	private File inputimage;
	private File outputimage;
	
	public void transform() throws Exception {

		IMOperation op = new IMOperation();
		op.addImage(); //place holder for input file
		op.resize(800,600);
		op.addImage(); //place holder for output file
		
		ConvertCmd convert = new ConvertCmd();
		convert.setSearchPath(imPath);
		convert.run(op, 
				new Object[]{
				inputimage.getAbsolutePath(),
				outputimage.getAbsolutePath()});
	}
	
	public DigitalObject convert(DigitalObject digObj) {
		//TODO
		return digObj;
	}
	
	public DigitalObject getDigitalobject() {
		return digitalobject;
	}

	public void setDigitalobject(DigitalObject digitalobject) {
		this.inputimage = digitalobject.getSubmittedFile();
		this.digitalobject = digitalobject;
	}
	
	public File getOutputimage() {
		return outputimage;
	}

	public void setOutputimage(File outputimage) {
		this.outputimage = outputimage;
	}
	
	public void setOutputimage(String outputimagepath) {
		this.outputimage = new File(outputimagepath);
	}
	
}
