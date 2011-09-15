package eu.heliovo.clientapi.linkprovider.impl;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 * Collection of common MimeType constants.
 * @author MarcoSoldati
 *
 */
public class MimeTypeConstants {

    /**
     * Mime type text/html.
     */
    public static final MimeType TEXT_HTML = createMimeType("text/html");

    /**
     * Mime type image/gif
     */
    public static final MimeType IMAGE_GIF = createMimeType("image/gif");
    
    /**
     * Mime type image/jpeg
     */
    public static final MimeType IMAGE_JPEG = createMimeType("image/jpeg");
    
    /**
     * Mime type image/png
     */
    public static final MimeType IMAGE_PNG = createMimeType("image/png");
    
    /**
     * Mime type image/svg+xml
     */
    public static final MimeType IMAGE_SVG_XML = createMimeType("image/svg+xml");

    /**
     * Mime type image/tiff
     */
    public static final MimeType IMAGE_TIFF = createMimeType("image/tiff");
    
    /**
     * Create the mime type instance and handle errors.
     * @param mimeType the mime type to parse
     * @return the mimetype instance
     */
    private static MimeType createMimeType(String mimeType) {
        try {
            return new MimeType(mimeType);
        } catch (MimeTypeParseException e) {
            throw new IllegalArgumentException("Unable to parse mime type " + mimeType + ". Cause: " + e.getMessage(), e);
        }
    }
}
