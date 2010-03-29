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

import hudson.PluginWrapper;
import hudson.model.BuildBadgeAction;
import hudson.model.Hudson;

import java.io.File;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean(defaultVisibility=2)
public class GroovyPostbuildAction implements BuildBadgeAction {
    private final String iconPath;
    private final String text;
    private String color = "#000000";
    private String background = "#FFFF00";
    private String border = "1px";
    private String borderColor = "#C0C000";

    private GroovyPostbuildAction(String iconPath, String text) {
    	this.iconPath = iconPath;
    	this.text = text;
    }
    
    public static GroovyPostbuildAction createBadge(String icon, String text) {
    	return new GroovyPostbuildAction(getIconPath(icon), text);
    }
    
    public static GroovyPostbuildAction createShortText(String text) {
    	return new GroovyPostbuildAction(null, text);
    }
    
    public static GroovyPostbuildAction createShortText(String text, String color, String background, String border, String borderColor) {
    	GroovyPostbuildAction action =  new GroovyPostbuildAction(null, text);
    	action.color = color;
    	action.background = background;
    	action.border = border;
    	action.borderColor = borderColor;
    	return action;
    }
    
    public static GroovyPostbuildAction createInfoBadge(String text) {
    	return new GroovyPostbuildAction(getIconPath("info.gif"), text);
    }
    
    public static GroovyPostbuildAction createWarningBadge(String text) {
    	return new GroovyPostbuildAction(getIconPath("warning.gif"), text);
    }
    
    public static GroovyPostbuildAction createErrorBadge(String text) {
    	return new GroovyPostbuildAction(getIconPath("error.gif"), text);
    }
        
    /* Action methods */
    public String getUrlName() { return ""; }
    public String getDisplayName() { return ""; }
    public String getIconFileName() { return null; }

    @Exported public boolean isTextOnly() { return (iconPath == null); }
    @Exported public String getIconPath() { return iconPath; }
    @Exported public String getText() { return text; }
    @Exported public String getColor() { return color; }
    @Exported public String getBackground() { return background; }
    @Exported public String getBorder() { return border; }
    @Exported public String getBorderColor() { return borderColor; }

    public static String getIconPath(String icon) {
		if(icon == null) return null;
		if(icon.startsWith("/")) return icon;
		// Try plugin images dir, fallback to Hudson images dir
		PluginWrapper wrapper = Hudson.getInstance().getPluginManager().getPlugin(GroovyPostbuildPlugin.class);
		boolean pluginIconExists = (wrapper != null) && new File(wrapper.baseResourceURL.getPath() + "/images/" + icon).exists();
		return pluginIconExists ? "/plugin/" + wrapper.getShortName() + "/images/" + icon : Hudson.RESOURCE_PATH + "/images/16x16/" + icon;
    }
}
