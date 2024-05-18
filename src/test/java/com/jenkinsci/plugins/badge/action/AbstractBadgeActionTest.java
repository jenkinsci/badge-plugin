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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.markup.EscapedMarkupFormatter;
import hudson.markup.MarkupFormatter;
import hudson.markup.RawHtmlMarkupFormatter;
import java.io.IOException;
import java.io.Writer;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
abstract class AbstractBadgeActionTest {

    @Test
    void id(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractBadgeAction action = createAction(null, "icon", "text", "cssClass", "style", "link");
        assertNotNull(action.getId());
        assertTrue(action.getId().matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));

        action = createAction("id", "icon", "text", "cssClass", "style", "link");
        assertEquals("id", action.getId());

        action = createAction("", "icon", "text", "cssClass", "style", "link");
        assertEquals("", action.getId());
    }

    @Test
    void icon(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractBadgeAction action = createAction("id", null, "text", "cssClass", "style", "link");
        assertNull(action.getIcon());

        action.setIcon("");
        assertEquals("", action.getIcon());

        action.setIcon("icon.png");
        assertEquals(Jenkins.RESOURCE_PATH + "/images/16x16/icon.png", action.getIcon());

        action.setIcon("/relative/url/icon.png");
        assertEquals("/relative/url/icon.png", action.getIcon());

        action.setIcon("symbol-rocket plugin-ionicons-api");
        assertEquals("symbol-rocket plugin-ionicons-api", action.getIcon());

        action.setIcon("https://host.domain/icon.png");
        assertEquals("https://host.domain/icon.png", action.getIcon());

        action.setIcon("info.gif");
        assertEquals("/plugin/badge/images/info.gif", action.getIcon());

        action.setIcon("blue.gif");
        assertEquals(Jenkins.RESOURCE_PATH + "/images/16x16/blue.gif", action.getIcon());
    }

    @Test
    void text(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractBadgeAction action = createAction("id", "icon", null, "cssClass", "style", "link");
        assertNull(action.getText());

        action.setText("");
        assertEquals("", action.getText());

        action.setText("text");
        assertEquals("text", action.getText());

        r.jenkins.setMarkupFormatter(new EscapedMarkupFormatter());
        action.setText("<p>Plain Text</p>");
        assertEquals("&lt;p&gt;Plain Text&lt;/p&gt;", action.getText());

        action.setText("<script>alert('Plain Text')</script>");
        assertEquals("&lt;script&gt;alert(&#039;Plain Text&#039;)&lt;/script&gt;", action.getText());

        r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
        action.setText("<p>Safe HTML</p><script>alert('Unsafe HTML')</script>");
        assertEquals("<p>Safe HTML</p>", action.getText());

        action.setText("<script>alert('Unsafe HTML')</script>");
        assertEquals("", action.getText());

        MarkupFormatter formatter = new MarkupFormatter() {
            @Override
            public void translate(String markup, @NonNull Writer output) throws IOException {
                throw new IOException("Oh no!");
            }
        };
        r.jenkins.setMarkupFormatter(formatter);
        action.setText("text");
        assertEquals(
                "<b><font color=\"var(--error-color)\">Error preparing badge text for UI</font></b>", action.getText());
    }

    @Test
    void cssClass(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractBadgeAction action = createAction("id", "icon", "text", null, "style", "link");
        assertNull(action.getCssClass());

        action.setCssClass("");
        assertEquals("", action.getCssClass());

        action.setCssClass("cssClass");
        assertEquals("cssClass", action.getCssClass());
    }

    @Test
    void style(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractBadgeAction action = createAction("id", "icon", "text", "cssClass", null, "link");
        assertNull(action.getStyle());

        action.setStyle("");
        assertEquals("", action.getStyle());

        action.setStyle("style");
        assertEquals("style", action.getStyle());
    }

    @Test
    void link(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractBadgeAction action = createAction("id", "icon", "text", "cssClass", "style", null);
        assertNull(action.getLink());

        action.setLink("");
        assertEquals("", action.getLink());

        action.setLink("link");
        assertNull(action.getLink());

        action.setLink("/relative/url");
        assertEquals("/relative/url", action.getLink());

        action.setLink("https://host.domain");
        assertEquals("https://host.domain", action.getLink());

        action.setLink("mailto:foo@bar.com");
        assertEquals("mailto:foo@bar.com", action.getLink());
    }

    @Test
    void iconFileName(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractBadgeAction action = createAction(null, null, null, null, null, null);
        assertEquals(getIconFileName(), action.getIconFileName());
    }

    @Test
    void displayName(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractBadgeAction action = createAction(null, null, null, null, null, null);
        assertEquals(getDisplayName(), action.getDisplayName());
    }

    @Test
    void urlName(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractBadgeAction action = createAction(null, null, null, null, null, null);
        assertEquals(getUrlName(), action.getUrlName());
    }

    protected abstract AbstractBadgeAction createAction(
            String id, String icon, String text, String cssClass, String style, String link);

    protected abstract String getDisplayName();

    protected String getIconFileName() {
        return null;
    }

    protected String getUrlName() {
        return "";
    }
}
