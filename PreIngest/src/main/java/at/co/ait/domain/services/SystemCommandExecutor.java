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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can be used to execute a system command from a Java application.
 * See the documentation for the public methods of this class for more
 * information.
 * 
 * Documentation for this class is available at this URL:
 * 
 * http://devdaily.com/java/java-processbuilder-process-system-exec
 *
 * 
 * Copyright 2010 alvin j. alexander, devdaily.com.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.

 * You should have received a copy of the GNU Lesser Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Please ee the following page for the LGPL license:
 * http://www.gnu.org/licenses/lgpl.txt
 * 
 */
public class SystemCommandExecutor
{
  private List<String> commandInformation;
  private String adminPassword;
  private ThreadedStreamHandler inputStreamHandler;
  private ThreadedStreamHandler errorStreamHandler;
  
	
	private static final Logger logger = LoggerFactory
			.getLogger(SystemCommandExecutor.class);
  
  /**
   * Pass in the system command you want to run as a List of Strings, as shown here:
   * 
   * List<String> commands = new ArrayList<String>();
   * commands.add("/sbin/ping");
   * commands.add("-c");
   * commands.add("5");
   * commands.add("www.google.com");
   * SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
   * commandExecutor.executeCommand();
   * 
   * Note: I've removed the other constructor that was here to support executing
   *       the sudo command. I'll add that back in when I get the sudo command
   *       working to the point where it won't hang when the given password is
   *       wrong.
   *
   * @param commandInformation The command you want to run.
   */
  public SystemCommandExecutor(final List<String> commandInformation)
  {
    if (commandInformation==null) throw new NullPointerException("The commandInformation is required.");
    this.commandInformation = commandInformation;
    this.adminPassword = null;
  }

  public int executeCommand()
  throws IOException, InterruptedException
  {
    int exitValue = -99;

    try
    {
      ProcessBuilder pb = new ProcessBuilder(commandInformation);
      Process process = pb.start();

      // you need this if you're going to write something to the command's input stream
      // (such as when invoking the 'sudo' command, and it prompts you for a password).
      OutputStream stdOutput = process.getOutputStream();
      
      // i'm currently doing these on a separate line here in case i need to set them to null
      // to get the threads to stop.
      // see http://java.sun.com/j2se/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
      InputStream inputStream = process.getInputStream();
      InputStream errorStream = process.getErrorStream();

      // these need to run as java threads to get the standard output and error from the command.
      // the inputstream handler gets a reference to our stdOutput in case we need to write
      // something to it, such as with the sudo command
      inputStreamHandler = new ThreadedStreamHandler(inputStream, stdOutput, adminPassword);
      errorStreamHandler = new ThreadedStreamHandler(errorStream);

      // TODO the inputStreamHandler has a nasty side-effect of hanging if the given password is wrong; fix it
      inputStreamHandler.start();
      errorStreamHandler.start();

      // TODO a better way to do this?
      exitValue = process.waitFor();
 
      // TODO a better way to do this?
      inputStreamHandler.interrupt();
      errorStreamHandler.interrupt();
      inputStreamHandler.join();
      errorStreamHandler.join();
    }
    catch (IOException e)
    {
      // TODO deal with this here, or just throw it?
      throw e;
    }
    catch (InterruptedException e)
    {
      // generated by process.waitFor() call
      // TODO deal with this here, or just throw it?
      throw e;
    }
    return exitValue;
  }

  /**
   * Get the standard output (stdout) from the command you just exec'd.
   */
  public String getStandardOutputFromCommand()
  {
    return inputStreamHandler.getOutputBuffer();
  }

  /**
   * Get the standard error (stderr) from the command you just exec'd.
   */
  public String getStandardErrorFromCommand()
  {
    return errorStreamHandler.getOutputBuffer();
  }


}







