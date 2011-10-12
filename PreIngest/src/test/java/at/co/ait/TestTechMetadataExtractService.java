/*
 * 
 */
package at.co.ait;


import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.co.ait.domain.services.JHOVE1Service;

public class TestTechMetadataExtractService {

	JHOVE1Service md = new JHOVE1Service();
	private static final Logger logger = LoggerFactory.getLogger(JHOVE1Service.class);

	@Before
	public void setUp() throws Exception {
		md = new JHOVE1Service();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void TestGetDocument() {
		String[] result = new String[3];
		try
		{
			result[0] = md.getDocument(new File("C:/ProjectData/BHL-Tests/archive/99/CSIC000151989_0001.tiff"));
			result[1] = md.getDocument(new File("C:/ProjectData/BHL-Tests/archive/99/Fauna Iberica vol 0 Marc21.txt"));
			result[2] = md.getDocument(new File("C:/ProjectData/BHL-Tests/archive/99/CSIC000420361.xml"));			
		}
		catch (Exception e)
		{			
			e.printStackTrace();	
		}
		logger.info(result[0]);
		logger.info(result[1]);
		logger.info(result[2]);
		fail("Not yet implemented");
	}

}
