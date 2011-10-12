/**
 * 
 */
package at.co.ait.utils;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFImageWriter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dodo
 *
 */
public class PDFPageImageWriter extends PDFImageWriter {

	public void pageToImage(PDPage page, File target, String imageFormat, int resolution) throws IOException {
		int imageType = BufferedImage.TYPE_INT_RGB;
        BufferedImage image = page.convertToImage(imageType, resolution);
        ImageOutputStream output = ImageIO.createImageOutputStream( target );
        boolean foundWriter = false;
        Iterator<ImageWriter> writerIter = ImageIO.getImageWritersByFormatName( imageFormat );
        while( writerIter.hasNext() && !foundWriter )
        {
        	ImageWriter imageWriter = null;
            try
            {
            	imageWriter = (ImageWriter)writerIter.next();
                ImageWriteParam writerParams = imageWriter.getDefaultWriteParam();
                if( writerParams.canWriteCompressed() )
                {
                    writerParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    if(imageFormat.toLowerCase().startsWith("tif")) {
                    	writerParams.setCompressionType("PackBits");
                    } else {
                    	writerParams.setCompressionQuality(1.0f);
                    }
                }
                IIOMetadata meta = createMetadata( image, imageWriter, writerParams, resolution);
                imageWriter.setOutput( output );
                imageWriter.write( null, new IIOImage( image, null, meta ), writerParams );
                foundWriter = true;
            }
            catch( IIOException io )
            {
                throw new IOException( io.getMessage() );
            }
            finally
            {
                if( imageWriter != null )
                {
                    imageWriter.dispose();
                }
            }
        }
	}
	

    private static IIOMetadata createMetadata(RenderedImage image, ImageWriter imageWriter,
            ImageWriteParam writerParams, int resolution)
    {
        ImageTypeSpecifier type;
        if (writerParams.getDestinationType() != null)
        {
            type = writerParams.getDestinationType();
        }
        else
        {
            type = ImageTypeSpecifier.createFromRenderedImage( image );
        }
        IIOMetadata meta = imageWriter.getDefaultImageMetadata( type, writerParams );
        return (addResolution(meta, resolution) ? meta : null);
    }

    private static final String STANDARD_METADATA_FORMAT = "javax_imageio_1.0";

    private static boolean addResolution(IIOMetadata meta, int resolution)
    {
        if (meta.isStandardMetadataFormatSupported())
        {
            IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(STANDARD_METADATA_FORMAT);
            IIOMetadataNode dim = getChildNode(root, "Dimension");
            IIOMetadataNode child;
            child = getChildNode(dim, "HorizontalPixelSize");
            if (child == null)
            {
                child = new IIOMetadataNode("HorizontalPixelSize");
                dim.appendChild(child);
            }
            child.setAttribute("value",
                    Double.toString(resolution / 25.4));
            child = getChildNode(dim, "VerticalPixelSize");
            if (child == null)
            {
                child = new IIOMetadataNode("VerticalPixelSize");
                dim.appendChild(child);
            }
            child.setAttribute("value",
                    Double.toString(resolution / 25.4));
            try
            {
                meta.mergeTree(STANDARD_METADATA_FORMAT, root);
            }
            catch (IIOInvalidTreeException e)
            {
                throw new RuntimeException("Cannot update image metadata: "
                        + e.getMessage());
            }
            return true;
        }
        return false;
    }
    
    private static IIOMetadataNode getChildNode(Node n, String name)
    {
        NodeList nodes = n.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node child = nodes.item(i);
            if (name.equals(child.getNodeName()))
            {
                return (IIOMetadataNode)child;
            }
        }
        return null;
    }
}
