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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;

/**
 * Common action for build summaries.
 */
public class BadgeSummaryAction extends AbstractBadgeAction {

    private static final long serialVersionUID = 1L;

    public BadgeSummaryAction(String id, String icon, String text, String cssClass, String style, String link) {
        super(id, icon, text, cssClass, style, link);
    }

    @Override
    public String getDisplayName() {
        return "Badge Summary Action";
    }

    @Whitelisted
    @Deprecated(since = "2.0", forRemoval = true)
    public void appendText(String text) {
        appendText(text, false);
    }

    @Whitelisted
    @Deprecated(since = "2.0", forRemoval = true)
    public void appendText(String text, boolean escapeHtml) {
        if (escapeHtml) {
            text = StringEscapeUtils.escapeHtml(text);
        }
        setText(StringUtils.defaultString(getText()) + text);
    }

    @Whitelisted
    @Deprecated(since = "2.0", forRemoval = true)
    public void appendText(String text, boolean escapeHtml, boolean bold, boolean italic, String color) {
        String startTags = "";
        String closeTags = "";
        if (bold) {
            startTags += "<b>";
            closeTags += "</b>";
        }
        if (italic) {
            startTags += "<i>";
            closeTags += "</i>";
        }
        if (color != null) {
            startTags += "<font color=\"" + StringEscapeUtils.escapeHtml(color) + "\">";
            closeTags += "</font>";
        }
        if (escapeHtml) {
            text = StringEscapeUtils.escapeHtml(text);
        }
        setText(StringUtils.defaultString(getText()) + startTags + text + closeTags);
    }

    // LEGACY CODE
    @Deprecated(since = "2.0", forRemoval = true)
    private transient String summaryText;

    /**
     * @deprecated kept for backwards compatibility.
     * Translates pre 2.0 build.xml to latest format for backwards compatibility.
     * @return this instance
     */
    @Override
    @Deprecated(since = "2.0", forRemoval = true)
    protected Object readResolve() {
        super.readResolve();

        // field renamed
        if (summaryText != null) {
            setText(summaryText);
        }

        return this;
    }
}
