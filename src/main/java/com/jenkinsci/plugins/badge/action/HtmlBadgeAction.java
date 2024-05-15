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

import com.jenkinsci.plugins.badge.BadgePlugin;
import hudson.markup.RawHtmlMarkupFormatter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean(defaultVisibility = 2)
public class HtmlBadgeAction extends AbstractBadgeAction {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(HtmlBadgeAction.class.getName());

    private final String html;

    private HtmlBadgeAction(String html) {
        this.html = html;
    }

    public static HtmlBadgeAction createHtmlBadge(String html) {
        return new HtmlBadgeAction(html);
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

    public String getRawHtml() {
        return html;
    }

    @Exported
    public String getHtml() {
        if (BadgePlugin.get().isDisableFormatHTML()) {
            return html;
        }
        try {
            return RawHtmlMarkupFormatter.INSTANCE.translate(html);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error preparing html content for ui", e);
            return "<b><font color=\"red\">ERROR</font></b>";
        }
    }
}
