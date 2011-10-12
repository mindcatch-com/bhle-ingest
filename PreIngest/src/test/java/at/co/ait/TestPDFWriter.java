/*
 * 
 */
package at.co.ait;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFImageWriter;

public class TestPDFWriter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 0) args = new String[] { "/home/dodo/tmp/pdf/test.pdf" };
		
		for(String pdfFileName : args) {
			PDDocument doc;
			try {
				doc = PDDocument.load(pdfFileName);
				PDFImageWriter imageWriter = new PDFImageWriter();
				int resolution = 96; // DPI
				String outputPrefix = pdfFileName.replace(".pdf", "_pdf_");
		        boolean success = imageWriter.writeImage(doc, "png", "",
		                1, Integer.MAX_VALUE, outputPrefix, BufferedImage.TYPE_INT_RGB, resolution);
		        if (!success)
		        {
		            System.err.println( "Error: no writer found for image format 'png'" );
		            System.exit(1);
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
