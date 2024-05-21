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

import hudson.model.Action;
import hudson.model.BuildBadgeAction;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 *  @deprecated replaced by {@link BadgeAction}.
 */
@ExportedBean(defaultVisibility = 2)
@Deprecated(since = "2.0", forRemoval = true)
public class HtmlBadgeAction implements BuildBadgeAction, Action, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(HtmlBadgeAction.class.getName());

    private String id;
    private final String html;

    private HtmlBadgeAction(String html) {
        this.html = html;
    }

    public static HtmlBadgeAction createHtmlBadge(String html) {
        return new HtmlBadgeAction(html);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exported
    public String getId() {
        return id;
    }

    @Exported
    public String getHtml() {
        try {
            return Jenkins.get().getMarkupFormatter().translate(html);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error preparing HTML content for UI", ex);
            return "<b><font color=\"var(--error-color)\">Error preparing HTML content for UI</font></b>";
        }
    }

    public String getRawHtml() {
        return html;
    }

    @Override
    public String getUrlName() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public String getIconFileName() {
        return null;
    }
}
