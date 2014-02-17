/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
import java.util.regex.*;
import javax.swing.text.*;

/* Example:

        // Set editor kit
        jtextpane.setEditorKitForContentType("text/xml", new XmlEditorKit());
        jtextpane.setContentType("text/xml");

 */

public class XmlEditorKit extends StyledEditorKit {
	 
    private static final long serialVersionUID = 2969169649596107757L;
    private ViewFactory xmlViewFactory;
    
    private static HashMap<Pattern, Color> patternColors;
    private static String TAG_PATTERN = "(</?[a-z]*)\\s?>?";
    private static String TAG_END_PATTERN = "(/>)";
    private static String TAG_ATTRIBUTE_PATTERN = "\\s([a-zA-Z0-9]*)\\s*?\\=\\s*?\\\"";
    private static String TAG_ATTRIBUTE_VALUE = "[a-zA-Z0-9]*\\=(\"[^\"]*\")";
    private static String TAG_COMMENT = "(<!--.*-->)";
    private static String TAG_CDATA_START = "(\\<!\\[CDATA\\[).*";
    private static String TAG_CDATA_END = ".*(]]>)";
    
    //public static String TAG_HIGHLIGHTED = ".*hwnd=\"([^\"]*)\".*";
    public static String TAG_HIGHLIGHTED = "";
    public static Color TAG_HIGHLIGHTED_COLOR = new Color(170, 255, 48);

    public static int HIGHLIGHTED_START = 0;
    public static int HIGHLIGHTED_END = 0;
    public static Color HIGHLIGHTED_COLOR = new Color(240, 255, 112);

    static {
        // NOTE: the order is important!
    	patternColors = new HashMap<Pattern, Color>();
        patternColors.put(Pattern.compile(TAG_CDATA_START), new Color(128, 128, 128));
        patternColors.put(Pattern.compile(TAG_CDATA_END), new Color(128, 128, 128));
        patternColors.put(Pattern.compile(TAG_PATTERN), new Color(63, 127, 127));
        patternColors.put(Pattern.compile(TAG_ATTRIBUTE_PATTERN), new Color(127, 0, 127));
        patternColors.put(Pattern.compile(TAG_END_PATTERN), new Color(63, 127, 127));
        patternColors.put(Pattern.compile(TAG_ATTRIBUTE_VALUE), new Color(42, 0, 255));
        patternColors.put(Pattern.compile(TAG_COMMENT), new Color(63, 95, 191));
    }
 
    
    class XmlView extends PlainView {
    	 
     
        public XmlView(Element element) {
     
            super(element);
     
            // Set tabsize to 4 (instead of the default 8)
            getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
        }
     
        @Override
        protected int drawUnselectedText(Graphics graphics, int x, int y, int p0, int p1) throws BadLocationException {
        	try {
     
            Document doc = getDocument();
            String text = doc.getText(p0, p1 - p0);
     
            Segment segment = getLineBuffer();
            int initialXpos = x;
     
            SortedMap<Integer, Integer> startMap = new TreeMap<Integer, Integer>();
            SortedMap<Integer, Color> colorMap = new TreeMap<Integer, Color>();
     
            // Match all regexes on this snippet, store positions
            for (Map.Entry<Pattern, Color> entry : patternColors.entrySet()) {
     
                Matcher matcher = entry.getKey().matcher(text);
     
                while (matcher.find()) {
                    startMap.put(matcher.start(1), matcher.end(1));
                    colorMap.put(matcher.start(1), entry.getValue());
                }
            }
                 
            // TODO: check the map for overlapping parts
             
            int i = 0;
            //add tag highlighted background colors
            if (!TAG_HIGHLIGHTED.isEmpty()) {
	            Matcher highlightMatcher = Pattern.compile(TAG_HIGHLIGHTED).matcher(text);
	            while(highlightMatcher.find()) {
	            	int start = highlightMatcher.start(1);
	            	int end = highlightMatcher.end(1);
	                if (i < start) {
	                	graphics.setColor(Color.black);
	                	doc.getText(p0 + i, start - i, segment);
	                	x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
		            }
	                graphics.setColor(TAG_HIGHLIGHTED_COLOR);
	                i = end;
	                doc.getText(p0 + start, i - start, segment);
	            	int width = Utilities.getTabbedTextWidth(segment, graphics.getFontMetrics(), x, this, p0 + start);
	            	//graphics.drawLine(x, y, width, y);graphics.getFontMetrics()
	            	graphics.fillRect(x, y - graphics.getFontMetrics().getHeight() + 2, width, graphics.getFontMetrics().getHeight());
	            	graphics.setColor(Color.black);
	                doc.getText(p0 + start, i - start, segment);
	                x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
	            }
            }
            x = initialXpos;
            i=0;
            //add highlighted background colors based on position
            //String textx = doc.getText(p0, p1 - p0);
            if ((HIGHLIGHTED_START < p1 && HIGHLIGHTED_START >= p0) || (HIGHLIGHTED_END <= p1 && HIGHLIGHTED_END > p0) || ( HIGHLIGHTED_START < p1 && HIGHLIGHTED_END > p0)) {
            	//select whole line
            	int start = 0;
            	int end = text.length();
            	// test to see if only partial line is needed.
            	if (HIGHLIGHTED_START > p0)
            		start = HIGHLIGHTED_START - p0;
            	if (HIGHLIGHTED_END < p1)
            		end -=  p1 - HIGHLIGHTED_END;
                if (i < start) { // fill in normal color if start highlight isn't at the beginning
                	graphics.setColor(Color.black);
                	doc.getText(p0 + i, start - i, segment);
                	x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
	            }
                graphics.setColor(HIGHLIGHTED_COLOR);// fill in the highlight color
                i = end;
                if (i - start > 0) {
	                doc.getText(p0 + start, i - start, segment);
	            	int width = Utilities.getTabbedTextWidth(segment, graphics.getFontMetrics(), x, this, p0 + start);
	            	//graphics.drawLine(x, y, width, y);graphics.getFontMetrics()
	            	graphics.fillRect(x, y - graphics.getFontMetrics().getHeight() + 2, width, graphics.getFontMetrics().getHeight());
	            	graphics.setColor(Color.black);
	                doc.getText(p0 + start, i - start, segment);
	                x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
                }
                //else
                //	System.out.println("invalid highlighting " + (i - start) + " is <= 0 (" +  p0 + "-" + p1 + "=" + (p1 - p0) +") " + start + ", " + end + " len=" + text.length());
            }
            
            x = initialXpos;
            i=0;
            // Color the parts of xml foreground font
            for (Map.Entry<Integer, Integer> entry : startMap.entrySet()) {
                int start = entry.getKey();
                int end = entry.getValue();
     
                if (i < start) {
                    graphics.setColor(Color.black);
                    doc.getText(p0 + i, start - i, segment);
                    x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
                }
     
                graphics.setColor(colorMap.get(start));
                i = end;
                doc.getText(p0 + start, i - start, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
            }
            
  
            // Paint possible remaining text black
            if (i < text.length()) {
                graphics.setColor(Color.black);
                doc.getText(p0 + i, text.length() - i, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
            return x;
        }
    }
    
    
    class XmlViewFactory extends Object implements ViewFactory {
        /**
         * @see javax.swing.text.ViewFactory#create(javax.swing.text.Element)
         */
        public View create(Element element) {
     
            return new XmlView(element);
        }
    }
    
    public XmlEditorKit() {
        xmlViewFactory = new XmlViewFactory();
    }
     
    @Override
    public ViewFactory getViewFactory() {
        return xmlViewFactory;
    }
 
    @Override
    public String getContentType() {
        return "text/xml";
    }
}
