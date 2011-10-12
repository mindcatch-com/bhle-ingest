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

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is intended to be used with the SystemCommandExecutor class to let
 * users execute system commands from Java applications.
 * 
 * This class is based on work that was shared in a JavaWorld article named
 * "When System.exec() won't". That article is available at this url:
 * 
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 * 
 * Documentation for this class is available at this URL:
 * 
 * http://devdaily.com/java/java-processbuilder-process-system-exec
 * 
 * 
 * Copyright 2010 alvin j. alexander, devdaily.com.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Please see the following page for the LGPL license:
 * http://www.gnu.org/licenses/lgpl.txt
 * 
 */
class ThreadedStreamHandler extends Thread {
	private static final Logger logger = LoggerFactory
			.getLogger(ThreadedStreamHandler.class);

	InputStream inputStream;
	String adminPassword;
	OutputStream outputStream;
	PrintWriter printWriter;
	StringBuilder outputBuffer = new StringBuilder();
	private boolean sudoIsRequested = false;

	/**
	 * A simple constructor for when the sudo command is not necessary. This
	 * constructor will just run the command you provide, without running sudo
	 * before the command, and without expecting a password.
	 * 
	 * @param inputStream
	 * @param streamType
	 */
	ThreadedStreamHandler(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * Use this constructor when you want to invoke the 'sudo' command. The
	 * outputStream must not be null. If it is, you'll regret it. :)
	 * 
	 * TODO this currently hangs if the admin password given for the sudo
	 * command is wrong.
	 * 
	 * @param inputStream
	 * @param streamType
	 * @param outputStream
	 * @param adminPassword
	 */
	ThreadedStreamHandler(InputStream inputStream, OutputStream outputStream,
			String adminPassword) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.printWriter = new PrintWriter(outputStream);
		this.adminPassword = adminPassword;
		this.sudoIsRequested = true;
	}

	public void run() {
		// on mac os x 10.5.x, when i run a 'sudo' command, i need to write
		// the admin password out immediately; that's why this code is
		// here.
		if (sudoIsRequested) {
			// doSleep(500);
			printWriter.println(adminPassword);
			printWriter.flush();
		}

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				logger.debug("STDOUT/ERR: "	+ line);
				outputBuffer.append(line + "\n");
			}
		} catch (IOException ioe) {
			// TODO handle this better
			ioe.printStackTrace();
		} catch (Throwable t) {
			// TODO handle this better
			t.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				// ignore this one
			}
		}
	}

	private void doSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	public String getOutputBuffer() {
		String out = outputBuffer.toString();
		// clear StringBuilder once it's been read
		this.outputBuffer = new StringBuilder();
		return out;
	}

}
