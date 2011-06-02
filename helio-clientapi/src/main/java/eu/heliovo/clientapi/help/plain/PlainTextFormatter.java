package eu.heliovo.clientapi.help.plain;

import java.text.BreakIterator;
import java.util.Locale;

import org.apache.log4j.Logger;

class PlainTextFormatter {

    /**
     * The logger for this class.
     */
    private static final Logger _LOGGER = Logger.getLogger(PlainTextFormatter.class);
    
    /**
     * OS dependent new line character
     */
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * The maximal line length
     */
    private static final int DEFAULT_MAX_LINE_LENGTH = 120;

    /**
     * Currently used max line length.
     */
    private int maxLineLength = DEFAULT_MAX_LINE_LENGTH;


    /**
     * Convert an APT formatted text to formatted plain text. 
     * @param aptText the text to parse and convert.
     * @return the structured plain text.
     */
    public String formatAPT(String aptText) {
        StringBuilder sb = new StringBuilder();
        
        // no need to visit any children of Entry. 
        
        return sb.toString();
    }
    
    /**
     * Indent string
     */
    private static final String INDENT = "  ";
    
    /**
     * Current indent
     */
    protected int indent = 0;
    
    /**
     * Indent strings to be reused.
     */
    private static String[] indentStrings = null;
    
    /**
     * Returns an indent string
     * @param i number of indents
     * @return a string containing the indent as spaces or tabs, depending on INDENT.
     */
    static synchronized String indent(int i) {
        // (re-)initialize indentStrings if required
        if (indentStrings == null || indentStrings.length <= i) {
            indentStrings = new String[i + 8];
            StringBuilder sb = new StringBuilder((1 + indentStrings.length) * INDENT.length());
            for (int j = 0; j < indentStrings.length; j++) {
                indentStrings[j] = sb.toString();
                sb.append(INDENT);
            }
        }
        
        return indentStrings[i];
    }

    /**
     * Add a newline to the StringBuilder but only if the last non empty characters are not already a newline.
     * @param sb the StringBuilder to append the new line to. 
     */
    public void addNewline(StringBuilder sb) {
        
        // remove any trailing spaces
        while (sb.length() >= 0) {
            char ch = sb.charAt(sb.length()-1);
            if (ch == ' ' || ch == '\t') {
                sb.deleteCharAt(sb.length() -1);
            } else {
                break;
            }
        }
        
        for (int i = sb.length()-1, j = NEWLINE.length()-1; i > 0 && j > 0; i--, j--) {
            if (sb.charAt(i) == NEWLINE.charAt(j)) {
                // we have a new line and nothing needs to be done.
            } else {
                // we have something else and we need to append a newline
                sb.append(NEWLINE);
                break;
            }
        }
    }
    
    /**
     * Convert a docbook fragment to a formatted text section.
     * This is done by using a stax parser that reads certain fragments.
     * @param aptText the docbook fragment to convert
     * @return the converted docbook fragment.
     */
    public String apt2PlainText(String aptText) {
    	// Kick off APT parser
    	
    	
    	// layout the text
        StringBuilder ret = new StringBuilder();
        
        return ret.toString();
    }


    /**
     * Collapse whitespace of a char sequence
     * @param in the string whose whitespace is to be collapsed
     * @param removeLeading if true removes the leading whitespace ohterwise it just collapses them.
     * @return the string with any leading whitespace removed, and any
     * internal sequence of or trailing whitespace characters replaced with a single space character.
    */
    private static CharSequence collapseWhitespace(CharSequence in, boolean removeLeading) {
        int len = in.length();
        if (len==0) {
            return in;
        }

        StringBuilder sb = new StringBuilder(len);
        boolean inWhitespace = removeLeading;
        for (int i = 0; i<len; i++) {
            char c = in.charAt(i);
            switch (c) {
                case '\n':
                case '\r':
                case '\t':
                case ' ':
                    if (inWhitespace) {
                        // remove the whitespace
                    } else {
                        sb.append(' ');
                        inWhitespace = true;
                    }
                    break;
                default:
                    sb.append(c);
                    inWhitespace = false;
                    break;
            }
        }
        return sb;
    }

    /**
     * Wrap a text into a paragraph of a given column length. 
     * @param text the text to wrap
     * @param indent the indent will be forwarded to {@link #indent(int)}.
     * @param indentFirstLine indent first line.
     * @param firstLinePos current position on first line.
     * @param wrapLength the maximal length of the line.
     * @return the wrapped text.
     */
    private String wrapText(String text, int indent, boolean indentFirstLine, int firstLinePos, int wrapLength) {
        //System.out.println("wrap text: '" + text + "', indent: " + indent + ", first line pos: " + firstLinePos);
        String ret;
        // Null or blank string should return an empty ("") string
        if (text == null || text.isEmpty()) {
            return "";
        }

        // indent first line
        if (indentFirstLine) {
            text = indent(indent) + text;
        }

        int indentLength = indent(indent).length();
        int stringLength = text.length();        

        if (firstLinePos + stringLength > wrapLength) {
            StringBuilder sb = new StringBuilder(stringLength
                    + ((stringLength / (wrapLength)) * 2 * NEWLINE.length() * indentLength));
     
            BreakIterator lineIterator = BreakIterator.getLineInstance(Locale.US);
            
            lineIterator.setText(text);
            int start = lineIterator.first();
            int linePos = firstLinePos;

            for (int end = lineIterator.next(); end != BreakIterator.DONE; start = end, end = lineIterator.next()) {
                // check if we are in wraplen
                if (linePos + (end - start)  < wrapLength) {
                    sb.append(text.substring(start, end));
                    linePos = linePos + (end-start);
                } else if (linePos == 0) { // check if one single term is longer than wrap line.
                    sb.append(NEWLINE);
                    // we do not truncate here
                    sb.append(indent(indent));
                    sb.append(text.substring(start, end));
                    linePos = indentLength + end-start; // will be > wrapLenght.
                } else {
                    // wrap
                    sb.append(NEWLINE);
                    sb.append(indent(indent));
                    sb.append(collapseWhitespace(text.substring(start, end), true));
                    linePos = indentLength + end-start;
                }
            }
            ret = sb.toString();
        } else {
            ret = text;
        }

        return ret;
    }    
}
