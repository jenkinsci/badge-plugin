/*
 * The MIT License
 *
 * Copyright (c) 2024, Badge Plugin Authors
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
package com.jenkinsci.plugins.badge.action;

import hudson.markup.RawHtmlMarkupFormatter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import org.apache.commons.lang.StringEscapeUtils;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean(defaultVisibility = 2)
public class BadgeSummaryAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(BadgeSummaryAction.class.getName());

    private final String iconPath;
    private String summaryText = "";

    public BadgeSummaryAction(String iconPath) {
        this.iconPath = iconPath;
    }

    /* Action methods */
    public String getUrlName() {
        return "";
    }

    public String getDisplayName() {
        return "";
    }

    public String getIconFileName() {
        return null;
    }

    @Exported
    public String getIconPath() {
        return iconPath;
    }

    public String getRawText() {
        return summaryText;
    }

    @Exported
    public String getText() {
        try {
            return Jenkins.get().getMarkupFormatter().translate(summaryText);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error preparing summary text for ui", e);
            return "<b><font color=\"red\">ERROR</font></b>";
        }
    }

    @Whitelisted
    public void appendText(String text) {
        appendText(text, false);
    }

    @Whitelisted
    public void appendText(String text, boolean escapeHtml) {
        if (escapeHtml) {
            text = StringEscapeUtils.escapeHtml(text);
        }
        summaryText += text;
    }

    @Whitelisted
    public void appendText(String text, boolean escapeHtml, boolean bold, boolean italic, String color) {
        String closeTags = "";
        if (bold) {
            summaryText += "<b>";
            closeTags += "</b>";
        }
        if (italic) {
            summaryText += "<i>";
            closeTags += "</i>";
        }
        if (color != null) {
            String cls = getJenkinsColorClass(color);
            if (cls != null) {
                summaryText += "<span class=\"" + StringEscapeUtils.escapeHtml(cls) + "\">";
                closeTags += "</span>";
            } else {
                summaryText += "<font color=\"" + StringEscapeUtils.escapeHtml(color) + "\">";
                closeTags += "</font>";
            }
        }
        if (escapeHtml) {
            text = StringEscapeUtils.escapeHtml(text);
        }
        summaryText += text + closeTags;
    }
}
