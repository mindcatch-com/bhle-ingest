/*
 * 
 */
package at.co.ait;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.marc.Record;
import org.w3c.dom.Document;

import at.co.ait.utils.DOM;

public class TestMarcXML {
	public static void main(String[] args) {
		File in = new File("/home/dodo/Documents/sts/user-ait020/marctest/bilderatlasmw4_meta.mrc");

		InputStream input;
		try {
			input = new FileInputStream(in);
		    MarcReader reader = new MarcStreamReader(input);
		    DOMResult result = new DOMResult();
		    if(true) {
		    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
		    	MarcWriter writer = new MarcXmlWriter(bout, "UTF-8");

			    while (reader.hasNext()) {
			        Record record = reader.next();
			        writer.write(record);
		        }
	            writer.close();
	            
	            Document doc = DOM.parse(bout.toByteArray());
	            Transformer tr = TransformerFactory.newInstance().newTransformer();
	            tr.transform(new DOMSource(doc), new StreamResult(System.out));
		    }
		    if(false) {
		    	// that produces a very strage error..
			    MarcXmlWriter writer = new MarcXmlWriter(result);
			    writer.setConverter(new AnselToUnicode());
	
			    while (reader.hasNext()) {
		                Record record = (Record) reader.next();
		                writer.write(record);
			    }
		        writer.close();
		    }
	        input.close();

	        Document doc = (Document) result.getNode();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
