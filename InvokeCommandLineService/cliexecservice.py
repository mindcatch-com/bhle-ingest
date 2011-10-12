import cherrypy
import time
import sys
import commands
import os
import base64
import logging
import logging.config
from cherrypy import expose

class Scanner:

    LOGLEVEL=logging.DEBUG
    CHERRYPY_IP='127.0.0.1'
    CHERRYPY_PORT=9989
    JAVA=r'java -jar '
    COMMAND=r'C:\SourceCode\smt-cli\SMT-CLI.jar'
    
    # needed in case environment is windows
    JAVA = JAVA.replace('\\', '/')
    COMMAND = COMMAND.replace('\\', '/')
    
    CLI=JAVA+' '+COMMAND
    
    # create logger
    logger = logging.getLogger("PyCLIExecService")
    logger.setLevel(LOGLEVEL)
    # create console handler and set level to debug
    ch = logging.StreamHandler()
    ch.setLevel(LOGLEVEL)
    # create formatter
    formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
    # add formatter to ch
    ch.setFormatter(formatter)
    # add ch to logger
    logger.addHandler(ch)

    def __init__(self):
      # configure cherrypy
      cherrypy.config.update({'server.socket_host': self.CHERRYPY_IP, 
                              'server.socket_port': self.CHERRYPY_PORT, 
                             })

    @expose
    def execute(self, params): 
      params=base64.b64decode(params)
      self.logger.debug('received parameters \n %s'% params)
      response = "not processed"
      # needed in case environment is windows
      params.replace('\\', '/')
      cmd=self.CLI+' '+params
      self.logger.debug('compiled command: %s \n'% cmd)
      # in python 2.6 only commands.getstatusoutput has proven stable 
      status, output = commands.getstatusoutput(cmd)
      return output
      
cherrypy.quickstart(Scanner())