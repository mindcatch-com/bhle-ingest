/*
 * 
 */
package at.co.ait;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import at.co.ait.domain.services.DirectoryListingService;

public class TestDirectoryListing {
	
	//unused: private static final Logger logger = LoggerFactory.getLogger(TestDirectoryListing.class);
	private DirectoryListingService dirlist = new DirectoryListingService();

	@Before
	public void setUp() throws Exception {		
		dirlist.setBasedir(new File("C:\\ProjectData\\BHL-E-FTP\\bhle-csic"));
	}

	@After
	public void tearDown() throws Exception {
	}


}
