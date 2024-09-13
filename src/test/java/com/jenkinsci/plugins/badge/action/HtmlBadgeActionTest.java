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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.markup.MarkupFormatter;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
@Deprecated(since = "2.0", forRemoval = true)
class HtmlBadgeActionTest {

    @Test
    void html(JenkinsRule r) {
        HtmlBadgeAction action = HtmlBadgeAction.createHtmlBadge(null);
        assertEquals("", action.getHtml());

        String html = UUID.randomUUID().toString();
        action = HtmlBadgeAction.createHtmlBadge(html);
        assertEquals(html, action.getHtml());

        MarkupFormatter formatter = new MarkupFormatter() {
            @Override
            public void translate(String markup, @NonNull Writer output) throws IOException {
                throw new IOException("Oh no!");
            }
        };
        r.jenkins.setMarkupFormatter(formatter);
        assertEquals(
                "<b><font color=\"var(--error-color)\">Error preparing HTML content for UI</font></b>",
                action.getHtml());
    }

    @Test
    void urlName(@SuppressWarnings("unused") JenkinsRule r) {
        HtmlBadgeAction action = HtmlBadgeAction.createHtmlBadge(null);
        assertEquals("", action.getUrlName());
    }

    @Test
    void displayName(@SuppressWarnings("unused") JenkinsRule r) {
        HtmlBadgeAction action = HtmlBadgeAction.createHtmlBadge(null);
        assertEquals("", action.getDisplayName());
    }

    @Test
    void iconFileName(@SuppressWarnings("unused") JenkinsRule r) {
        HtmlBadgeAction action = HtmlBadgeAction.createHtmlBadge(null);
        assertNull(action.getIconFileName());
    }
}
