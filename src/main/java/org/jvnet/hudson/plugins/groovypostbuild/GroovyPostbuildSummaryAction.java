/*
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Serban Iordache
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jvnet.hudson.plugins.groovypostbuild;

import hudson.model.Action;

import org.apache.commons.lang.StringEscapeUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean(defaultVisibility=2)
public class GroovyPostbuildSummaryAction implements Action {
    private final String iconPath;
    private final StringBuilder textBuilder = new StringBuilder();

    public GroovyPostbuildSummaryAction(String iconPath) {
    	this.iconPath = iconPath;
    }
    
    /* Action methods */
    public String getUrlName() { return ""; }
    public String getDisplayName() { return ""; }
    public String getIconFileName() { return null; }

    @Exported public String getIconPath() { return iconPath; }
    @Exported public String getText() { return textBuilder.toString(); }

    public void appendText(String text, boolean escapeHtml) {
    	if(escapeHtml) {
        	text = StringEscapeUtils.escapeHtml(text);
    	}
    	textBuilder.append(text);
    }
    
    public void appendText(String text, boolean escapeHtml, boolean bold, boolean italic, String color) {
    	if(bold) {
    		textBuilder.append("<b>");
    	}
    	if(italic) {
    		textBuilder.append("<i>");
    	}
    	if(color != null) {
    		textBuilder.append("<font color=\"" + color + "\">");
    	}
    	if(escapeHtml) {
        	text = StringEscapeUtils.escapeHtml(text);
    	}
    	textBuilder.append(text);
    	if(color != null) {
    		textBuilder.append("</font>");
    	}
    	if(italic) {
    		textBuilder.append("</i>");
    	}
    	if(bold) {
    		textBuilder.append("</b>");
    	}
    }
}
