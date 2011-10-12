/**
 * 
 */
package at.co.ait.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author dodo
 *
 */
public class DOM {
	private static final Logger logger = LoggerFactory
	.getLogger(DOM.class);

	public enum NS {
		marc("http://www.loc.gov/MARC21/slim"),
		dc("http://purl.org/dc/elements/1.1/"),
		rdf("http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
		xsl("http://www.w3.org/1999/XSL/Transform"),
		oai_dc("http://www.openarchives.org/OAI/2.0/oai_dc/"),
		;
		public final String URI; 
		public final String ALIAS; 
		private NS(String uri) {
			this.URI = uri;
			this.ALIAS = name();
		}
	}
	
	public static final NamespaceContext GLOBAL_NS_CTX = new NamespaceContext() {
		HashMap<String, String> aliasToURI = new HashMap<String, String>();
		HashMap<String, ArrayList<String>> uriToAlias = 
			new HashMap<String, ArrayList<String>>();
		{
			for(NS ns : NS.values()) {
				add(ns.name(), ns.URI);
			}
		}
		private void add(String alias, String uri) {
			aliasToURI.put(alias, uri);
			ArrayList<String> alia = uriToAlias.get(uri);
			if(alia == null) {
				alia = new ArrayList<String>(); 
				uriToAlias.put(uri, alia);
			}
			alia.add(alias);
		}
		public Iterator<?> getPrefixes(String namespaceURI) {
			ArrayList<String> alia = uriToAlias.get(namespaceURI);
			return alia == null? null : alia.iterator();
		}
		
		public String getPrefix(String namespaceURI) {
			ArrayList<String> alia = uriToAlias.get(namespaceURI);
			return alia == null? null : alia.get(0);
		}
		
		public String getNamespaceURI(String prefix) {
			return aliasToURI.get(prefix);
		}
	};
	
	public static DocumentBuilder getDocBuilder() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			return dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.warn("DOM config invalid.", e);
			throw new RuntimeException(e);
		}
	}
	
	public static XPath getXPath() {
		XPathFactory xpf = XPathFactory.newInstance();
		javax.xml.xpath.XPath xp = xpf.newXPath();
		xp.setNamespaceContext(GLOBAL_NS_CTX);
		return xp;
	}

	public static XPathExpression getXPath(String xpath) throws XPathExpressionException {
		XPathFactory xpf = XPathFactory.newInstance();
		javax.xml.xpath.XPath xp = xpf.newXPath();
		xp.setNamespaceContext(GLOBAL_NS_CTX);
		return xp.compile(xpath);
	}

	/** Errors will be logged.
	 * 
	 * @param content XML File.
	 * @return null when an error occured
	 */
	public static Document parse(File content) {
		try {
			return getDocBuilder().parse(content);
		} catch (SAXException e) {
			logger.warn(content.getAbsolutePath() + " XML error: " + e.getMessage());
		} catch (IOException e) {
			logger.warn(content.getAbsolutePath() + " read error: " + e.getMessage());
		}
		return null;
	}
	/** Errors will be logged.
	 * 
	 * @param content XML File.
	 * @return null when an error occured
	 */
	public static Document parse(InputStream content) {
		try {
			return getDocBuilder().parse(content);
		} catch (SAXException e) {
			logger.warn("(stream) XML error: " + e.getMessage());
		} catch (IOException e) {
			logger.warn("(stream) read error: " + e.getMessage());
		}
		return null;
	}
	/** Errors will be logged.
	 * 
	 * @param content XML string.
	 * @return null when an error occured
	 */
	public static Document parse(byte[] content) {
		try {
			return getDocBuilder().parse(new ByteArrayInputStream(content));
		} catch (SAXException e) {
			logger.warn(content + " XML error: " + e.getMessage());
		} catch (IOException e) {
			logger.warn(content + " read error: " + e.getMessage());
		}
		return null;
	}
	
	public static Transformer getTransformer(File xsl) {
		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			return tf.newTransformer(new StreamSource(xsl));
		} catch (TransformerConfigurationException e) {
			logger.warn("XSLT error: " + e.getMessage());
		}
		return null;
	}

	public static String docToString(Document metsDocument) {
		TransformerFactory tf = TransformerFactory.newInstance();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			tf.newTransformer().transform(new DOMSource(metsDocument), new StreamResult(out));
		} catch (TransformerConfigurationException e) {
			logger.warn("transform error: " + e.getMessage());
		} catch (TransformerException e) {
			logger.warn("transform error: " + e.getMessage());
		}
		return out.toString();
	}

	public static void save(Document dc, File output) throws IOException {
		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			tf.newTransformer().transform(new DOMSource(dc), new StreamResult(output));
		} catch (TransformerConfigurationException e) {
			logger.warn("transform error: " + e.getMessage());
		} catch (TransformerException e) {
			if(e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			}
			logger.warn("transform error: " + e.getMessage());
		}		
	}

	public static Node xpathSingle(String expression, Document source) throws XPathExpressionException {
		XPath xp = getXPath();
		NodeList nl = (NodeList) xp.evaluate(expression, source, XPathConstants.NODESET);
		if(nl.getLength() > 0) return nl.item(0);
		return null;
	}
}
