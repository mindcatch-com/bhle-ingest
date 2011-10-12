/*
 * This class is vastly based on code from http://code.google.com/p/vitalopensource/
 * which is published under the Mozilla Public License 1.1.
 * 
 * It extracts metadata from any file using all JHOVE modules.
 * 
 */

package at.co.ait.domain.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.co.ait.domain.oais.DigitalObject;
import at.co.ait.utils.ConfigUtils;
import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.ErrorMessage;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.JhoveException;
import edu.harvard.hul.ois.jhove.Module;
import edu.harvard.hul.ois.jhove.OutputHandler;
import edu.harvard.hul.ois.jhove.RepInfo;
import edu.harvard.hul.ois.jhove.handler.XmlHandler;

public class JHOVE1Service {
	// Log4j instance.
	private static final Logger logger = LoggerFactory.getLogger(JHOVE1Service.class);
	
    private List<Module> m_module_list = null;

    // JHOVE API
    private JhoveBase m_jhove = null;
    private App m_application = null;

    // JHOVE Application Options
    private static final String ApplicationName = "Jhove";
    private static final int [] ApplicationDate = {2011, 1, 1};
    private static final String Release = "1.6";
    private static final String Usage = "";
    private static final String Rights = "Copyright 2004-2006 by the President " +
    "and Fellows of Harvard College. Released under the GNU Lesser General " +
    "Public License.";
   
    private static final String[] Modules = {
        "edu.harvard.hul.ois.jhove.module.PdfModule",
        "edu.harvard.hul.ois.jhove.module.Jpeg2000Module",
        "edu.harvard.hul.ois.jhove.module.JpegModule",
        "edu.harvard.hul.ois.jhove.module.TiffModule",
        "edu.harvard.hul.ois.jhove.module.XmlModule",
        "edu.harvard.hul.ois.jhove.module.AsciiModule",
    	"edu.harvard.hul.ois.jhove.module.Utf8Module"};
    
    // Removed:  "edu.harvard.hul.ois.jhove.module.BytestreamModule"
    // FIXXME:  Removed: "edu.harvard.hul.ois.jhove.module.HtmlModule" because of blocking issue with 
    //			lestrematodes00vercRMCA_0002.tif
    // TODO: switch to JHOVE2??

    private static final String EXCEPTION_JAVA_VERSION = "Java 1.4.x is required for the JHOVE API.";

    /**
     * Private constructor.  Initializes the logger and loads modules.
     */
    public JHOVE1Service()
    {
            String version = System.getProperty("java.vm.version");
            if(version.compareTo("1.4.0") < 0)
                    throw new RuntimeException(EXCEPTION_JAVA_VERSION);

            m_module_list = new LinkedList<Module>();

            // Set the log level for the JHOVE package.
            java.util.logging.Logger.getLogger("edu.harvard.hul.ois.jhove").setLevel(java.util.logging.Level.INFO);

            // Set up a placeholder JhoveBase instance.
            App m_application = new App(ApplicationName, Release, ApplicationDate, Usage, Rights);
            try
            {
                    // Create base JHOVE object, We won't actually use this class but a
                    // quirk of the architecture requires that it be passed to the Modules.
                    m_jhove = new JhoveBase();
                    m_jhove.setLogLevel("INFO");
                    m_jhove.setChecksumFlag(false);
                    m_jhove.setShowRawFlag(false);
                    m_jhove.setSignatureFlag(false);                    
            }
            catch(JhoveException e)
            {
                    throw new RuntimeException(e);
            }

            // Initialize the collection of Modules.
            for(int i = 0; i < Modules.length; i++)
            {
                    try
                    {
                            Class<?> module_class = Class.forName(Modules[i]);
                            Module module = (Module)module_class.newInstance();
                            module.init(null);
                            module.setDefaultParams(new LinkedList<Object>());
                            m_module_list.add(module);
                    }
                    catch(ClassNotFoundException e)
                    {
                    	logger.error("Cannot initialize module: " + Modules[i], e);
                    }
                    catch(InstantiationException e)
                    {
                    	logger.error("Cannot initialize module: " + Modules[i], e);
                    } 
                    catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
                    	logger.error("Cannot initialize module: " + Modules[i], e);
					} 
                    catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Cannot initialize module: " + Modules[i], e);
					}
            }
    }
    
    /**
     * Get a JHOVE Document from a {@link File}
     * @param file  a resource {@link File}
     * @return a JDOM Document
     * @throws Exception 
     * @throws JDOMException
     */
    public String getDocument(File file) throws Exception
    {
            // Create the RepInfo instance.
            RepInfo representation = new RepInfo(file.toString());
            if(!file.exists())
            {
                    representation.setMessage(new ErrorMessage("Content not found."));
                    representation.setWellFormed(RepInfo.FALSE);
            }
            else if(!file.isFile() || !file.canRead())
            {
                    representation.setMessage(new ErrorMessage("Content cannot be read."));
                    representation.setWellFormed(RepInfo.FALSE);
            }
            else
            {
                    representation.setSize(file.length());
                    representation.setLastModified(new Date(file.lastModified()));
                    populateRepresentation(representation, file);
            }
            return getDocumentFromRepresentation(representation);
    }
    
    /**
     * Enriches the digtal object by adding technical metadata
     * @param obj DigitalObject is getting enriched by technical metadata.
     * @return
     * @throws Exception 
     */
    public DigitalObject enrich(DigitalObject obj) throws Exception {
    	try {
    		logger.debug("JHOVE starting on " + obj.getSubmittedFile().getName());
			File output = ConfigUtils.getAipFile(obj.getPrefs().getBasedirectoryFile(),
					obj.getSubmittedFile(),".jhove.xml");
			if (!output.exists()) {
				FileUtils.writeStringToFile(output, this.getDocument(obj.getSubmittedFile()), "UTF-8");
				logger.debug("JHOVE successful on " + obj.getSubmittedFile().getName());
			}
			obj.setTechMetadata(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return obj;
    }
    
    /**
     * Populate the representation information for JHOVE Document from a specific File.
     * @param representation a RepInfo
     * @param file  a File containing the representation information
     * @throws Exception 
     */
    private void populateRepresentation(RepInfo representation, File file) throws Exception
    {
            // Iterate through the modules and process.
            Iterator<Module> iterator = m_module_list.iterator();
            while(iterator.hasNext())
            {
                    Module module = (Module)iterator.next();
                    module.setBase(m_jhove);
                    module.setVerbosity(Module.MAXIMUM_VERBOSITY);

                    // m_logger.info(module.toString());
                    RepInfo persistent = (RepInfo)representation.clone();

                    if(module.hasFeature("edu.harvard.hul.ois.jhove.canValidate"))
                    {
                            try
                            {
                                    module.applyDefaultParams();
                                    if(module.isRandomAccess())
                                    {
                                            RandomAccessFile ra_file = new RandomAccessFile(file, "r");
                                            module.parse(ra_file, persistent);
                                            ra_file.close();
                                    }
                                    else
                                    {
                                            InputStream stream = new FileInputStream(file);
                                            int parse = module.parse(stream, persistent, 0);
                                            while(parse != 0)
                                            {
                                                    stream.close();
                                                    stream = new FileInputStream(file);
                                                    parse = module.parse(stream, persistent, parse);
                                            }
                                            stream.close();
                                    }

                                    if(persistent.getWellFormed() == RepInfo.TRUE)
                                            representation.copy(persistent);
                                    else
                                            representation.setSigMatch(persistent.getSigMatch());
                            }
                            catch(Exception e)
                            {
                                    continue;
                            }
                    }
            }
    }
    
    /**
     * Get a JHOVE Document from a representation information.
     * @param _representation a representation information for a JHOVE Document
     * @return a JHOVE Document
     * @throws IOException
     * @throws JDOMException
     */
    private String getDocumentFromRepresentation(RepInfo _representation) throws IOException
    {
            // Create a PrintWriter we can extract a String from.
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(bytes, true);

            // Get a JHOVE XML OutputHandler we can use to handle the output.
            OutputHandler output_handler = getOutputHandler();
            output_handler.setWriter(writer);

            // Process the RepInfo instance with JHOVE's OutputHandler (in our case,
            // the XmlHandler).
            output_handler.setApp(new App(ApplicationName, Release, ApplicationDate, Usage, Rights));
            output_handler.setBase(m_jhove);

            // Header, Content, Footer.
            output_handler.showHeader();
            _representation.show(output_handler);
            output_handler.showFooter();  


            // Create an InputStream from the contents of the XML handler and
            // return a JDOM Document.
            String _return = bytes.toString();

            writer.close();
            bytes.close();

            return _return;
    }
    
    /**
     * Get a JHOVE XML OutputHandler to handle the output.
     * @return an OutputHandler
     */
    private OutputHandler getOutputHandler()
    {
            OutputHandler _return = new XmlHandler();
            try
            {
                    _return.setDefaultParams(new java.util.ArrayList<Object>());
                    _return.setApp(m_application);
                    _return.setBase(m_jhove);                    
            }
            catch(Exception e)
            {
                    throw new RuntimeException(e);
            }
            return _return;
    }

}
