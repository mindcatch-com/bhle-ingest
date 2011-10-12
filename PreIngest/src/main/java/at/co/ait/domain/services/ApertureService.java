/**
 * PreIngest - Metadata preparation tool before archival ingest.
 * Copyright (C) 2011 AIT Forschungsgesellschaft mbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package at.co.ait.domain.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerHandlerBase;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;

import at.co.ait.domain.oais.InformationPackageObject;
import at.co.ait.utils.ConfigUtils;

public class ApertureService {

	private Boolean suppressParentChildLinks = Boolean.FALSE;
	private ByteArrayOutputStream baos = null;  

	public InformationPackageObject process(InformationPackageObject obj) throws Exception {
		baos = new ByteArrayOutputStream (); 
        // start crawling and exit afterwards		
        doCrawling(obj.getSubmittedFile());
        String nfo = baos.toString("UTF-8");
        
		File output = ConfigUtils.getAipFile(obj.getPrefs().getBasedirectoryFile(),
				obj.getSubmittedFile(),".nfo.rdf.xml");
		FileUtils.writeStringToFile(output, nfo, "UTF-8");
        obj.setNepomukFileOntology(output);        
        return obj;
	}
	
    public void doCrawling(File rootFile) throws ModelException {
        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testsource");

        // create the data source
        FileSystemDataSource source = new FileSystemDataSource();
        source.setConfiguration(configuration);
        source.setRootFolder(rootFile.getAbsolutePath());
        source.setSuppressParentChildLinks(suppressParentChildLinks);
        source.setMaximumDepth(0);

        // setup a crawler that can handle this type of DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(new CrawlerHandler(baos));

        // start crawling
        crawler.crawl();
    }
    
    private class CrawlerHandler extends CrawlerHandlerBase {

		// our 'persistent' modelSet
        private ModelSet modelSet;

        public CrawlerHandler(ByteArrayOutputStream baos) throws ModelException {
        	// _must_ be set
        	super(new MagicMimeTypeIdentifier(),null,null);
            modelSet = RDF2Go.getModelFactory().createModelSet();
            modelSet.open();            
        }

        public void crawlStopped(Crawler crawler, ExitCode exitCode) {
            try {
                modelSet.writeTo(baos, Syntax.RdfXml);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }

            modelSet.close();
        }

        public RDFContainer getRDFContainer(URI uri) {
            // we create a new in-memory temporary model for each data source
            Model model = RDF2Go.getModelFactory().createModel(uri);
            model.open();
            return new RDFContainerImpl(model, uri);
        }

        public void objectNew(Crawler crawler, DataObject object) {
        	try {
				processBinary(crawler, object);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExtractorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SubCrawlerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
            // then we add this information to our persistent model
            modelSet.addModel(object.getMetadata().getModel());
            // don't forget to dipose of the DataObject
            object.dispose();
        }

        public void objectChanged(Crawler crawler, DataObject object) {
            // first we remove old information about the data object
            modelSet.removeModel(object.getID());
            // then we try to extract metadata and fulltext from the file
            try {
				processBinary(crawler, object);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExtractorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SubCrawlerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            // an then we add the information from the temporary model to our
            // 'persistent' model
            modelSet.addModel(object.getMetadata().getModel());
            // don't forget to dispose of the DataObject
            object.dispose();
        }

        public void objectRemoved(Crawler crawler, URI uri) {
            modelSet.removeModel(uri);
        }
    }

}
