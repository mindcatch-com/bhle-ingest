/*
 * 
 */
package at.co.ait;


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.co.ait.domain.services.TikaService;
import at.co.ait.utils.TikaUtils;

public class TestFileTypeDetectionService {

	private static final Logger logger = LoggerFactory.getLogger(TestDirectoryListing.class);
	private TikaService service = new TikaService();

	@Before
	public void setUp() throws Exception {		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIdentifyFile() {
		logger.info("Testing identify");
		assertEquals("image/tiff",TikaUtils.detectedMimeType(new File("C:\\ProjectData\\BHL-E-FTP\\bhle-mnhn\\PR 260 (Museum)\\ANNALES MUSEUM\\011802.DIR\\000001.TIF")));
		assertEquals("application/xml",TikaUtils.detectedMimeType(new File("C:\\ProjectData\\BHL-E-FTP\\bhle-mnhn\\PR 260 (Museum)\\ANNALES MUSEUM\\011802.DIR\\TDM\\011802.xml")));
	}

}
