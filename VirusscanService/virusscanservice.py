import cherrypy
import time
import sys
import random
import pyclamd
import os
import base64
import logging
import logging.config
from cherrypy import expose

class Scanner:

    CLAMAVD_IP='127.0.0.1'
    CLAMAVD_PORT=3310
    LOGLEVEL=logging.DEBUG
    CHERRYPY_IP='127.0.0.1'
    CHERRYPY_PORT=9999
    
    # create logger
    logger = logging.getLogger("PyStompClamAV")
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
      # connect to clamav daemon socket
      pyclamd.init_network_socket(self.CLAMAVD_IP, self.CLAMAVD_PORT)
      self.logger.debug("connected to ClamAV daemon")

    @expose
    def index(self):
      return "Hello World!"

    @expose
    def scan(self, message): 
      message=base64.b64decode(message)
      self.logger.debug('received message\n %s'% message)
      response = "not processed"
      # test for clam daemon
      if pyclamd.ping():
        # message contains directory
        if os.path.isdir(message):          
          self.logger.debug('received directory\n %s'% message)
          path=message
          # scan files inside directory
          dirList=os.listdir(path)
          for fname in dirList:
            self.logger.debug('contains file %s \n'% path+os.sep+fname)
            if os.path.isfile(path+os.sep+fname):
              self.logger.debug('scanning file %s \n'% fname)
              # call pyclamd with absolute path of file
              response = path+os.sep+fname+","+str(pyclamd.scan_file(path+os.sep+fname))
            # message contains single file
        if os.path.isfile(message):
          self.logger.debug('received file\n %s'% message)
          fname=message
          # call pyclamd with absolute path of file
          response = fname+","+str(pyclamd.scan_file(fname))
      return response
      
cherrypy.quickstart(Scanner())