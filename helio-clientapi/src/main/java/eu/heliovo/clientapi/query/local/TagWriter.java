package eu.heliovo.clientapi.query.local;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

import eu.heliovo.shared.util.AssertUtil;

/**
 * Template class that provides a base language for classes that output VoTable to a given writer.
 * The goal of this class is to make the VoTable transformation code as readable as possible.
 * @author marco soldati at fhnw ch
 *
 */
class TagWriter {

	/**
     * OS dependent new line character
     */
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * The writer for the starTable text
     */
    private Writer outWriter;

    /**
     * Base processor
     * @param outWriter the writer used as out.
     */
    public TagWriter(Writer outWriter) {
    	AssertUtil.assertArgumentNotNull(outWriter, "outWriter");
    	this.outWriter = outWriter;
    }

    public Writer getOut() {
        return outWriter;
    }

    /**
     * Write a complete tag with text content.
     * @param tagName name of the tag
     * @param content the textual content to write.
     * @return 'this' to join several statements.
     */
    public TagWriter tag(String tagName, String content) {
        openTag(tagName);
        text(content);
        closeTag(tagName);
        return this;
    }

    /**
     * Write a complete tag with text content.
     * @param tagName name of the tag
     * @param attributes use empty map if no attributes are required.
     * @param content the textual content to write.
     * @return 'this' to join several statements.
     */
    public TagWriter tag(String tagName, Map<String, String> attributes, String content) {
        openTag(tagName, attributes);
        text(content);
        closeTag(tagName);
        return this;
    }

    /**
     * Write an opening tag, make sure to call {@link #closeTag(String)} afterwards.
     * @param tagName name of the tag
     * @return 'this' to join several statements.
     */
    public TagWriter openTag(String tagName) {
        return openTag(tagName, Collections.<String, String> emptyMap());
    }

    /**
     * Write an opening tag, make sure to call {@link #closeTag(String)} afterwards.
     * @param tagName name of the tag, must not be null.
     * @param attributes use empty map if no attributes are required.
     * @return 'this' to join several statements.
     */
    public TagWriter openTag(String tagName, Map<String, String> attributes) {
        append("<");
        append(tagName);
        appendAttributes(attributes);
        append(">");
        return this;
    }

    /**
     * Close a previously opened tag
     * @param tagName the tag name.
     * @return 'this' to join several statements.
     */
    public TagWriter closeTag(String tagName) {
        append("</").append(tagName).append(">");
        return this;
    }

    /**
     * Render a tag without a content element, ie. &lt;br/&gt;.
     * @param tagName name of the tag.
     * @param attributes use empty map if no attributes are required.
     * @return 'this' to join several statements.
     */
    public TagWriter emptyTag(String tagName, Map<String, String> attributes) {
        append("<");
        append(tagName);
        appendAttributes(attributes);
        append("/>");
        return this;
    }

    /**
     * Append the attributes to an already opened tag.
     * @param attributes use empty map if no attributes are required.
     */
    private void appendAttributes(Map<String, String> attributes) {
        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            append(" ");
            append(attr.getKey());
            append("=\"");
            append(attr.getValue());
            append("\"");
        }
    }

    /**
     * Append floating text to the writer.
     * @param text the text to append.
     * @return 'this' to join several statements.
     */
    public TagWriter text(String text) {
        append(text);
        return this;
    }

    /**
     * Append a new line to the output writer
     * @return 'this' to join several statements.
     */
    public TagWriter newLine() {
        append(NEWLINE);
        return this;
    }

    /**
     * Append a text to 'out' and wrap any IOException that my occur into a DocbookTransformationException
     * @param text the text to append.
     * @return 'this' to join several statements.
     */
    private TagWriter append(String text) {
        try {
            getOut().append(text);
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return this;
    }
}
