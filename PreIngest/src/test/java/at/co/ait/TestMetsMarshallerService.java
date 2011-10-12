/*
 * 
 */
package at.co.ait;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.co.ait.domain.services.MetsMarshallerService;

public class TestMetsMarshallerService {
	
	private static final Logger logger = LoggerFactory.getLogger(TestDirectoryListing.class);
	private MetsMarshallerService packager = new MetsMarshallerService();

	@Before
	public void setUp() throws Exception {
		packager = new MetsMarshallerService();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPreparePackage() {
//		packager.preparePackage("C:/ProjectData/BHL-E-FTP/bhle-csic/v002/full/data");
		fail("Not yet implemented");
	}
	
	@Test
	public void testCreatePackage() {
//		packager.preparePackage("C:/ProjectData/BHL-E-FTP/bhle-csic/v002/full/data");
//		packager.createPackage("C:/ProjectData/BHL-Tests/archive");
		fail("Not yet implemented");
	}

}
