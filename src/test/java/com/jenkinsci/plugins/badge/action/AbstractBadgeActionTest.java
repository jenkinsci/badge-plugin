/*
 * The MIT License
 *
 * Copyright (c) 2025, Badge Plugin Authors
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
import io.jenkins.plugins.emoji.symbols.Emojis;
import io.jenkins.plugins.ionicons.Ionicons;
import java.io.IOException;
import java.io.Writer;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
abstract class AbstractBadgeActionTest {

    protected static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    @Deprecated
    abstract void deprecatedConstructor();

    @Test
    void id() {
        AbstractBadgeAction action = createAction(null, "icon", "text", "cssClass", "style", "link", "target");
        assertNotNull(action.getId());
        assertTrue(action.getId().matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));

        action = createAction("id", "icon", "text", "cssClass", "style", "link", "target");
        assertEquals("id", action.getId());

        action = createAction("", "icon", "text", "cssClass", "style", "link", "target");
        assertEquals("", action.getId());
    }

    @Test
    void icon() {
        AbstractBadgeAction action = createAction("id", null, "text", "cssClass", "style", "link", "target");
        assertNull(action.getIcon());

        action.setIcon("");
        assertEquals("", action.getIcon());

        action.setIcon("icon.png");
        assertEquals(Jenkins.RESOURCE_PATH + "/images/16x16/icon.png", action.getIcon());

        action.setIcon("/relative/url/icon.png");
        assertEquals("/relative/url/icon.png", action.getIcon());

        action.setIcon("symbol-rocket plugin-ionicons-api");
        assertEquals("symbol-rocket plugin-ionicons-api", action.getIcon());

        action.setIcon("symbol-cube");
        assertEquals("symbol-cube", action.getIcon());

        action.setIcon("icon-gear");
        assertEquals("icon-gear", action.getIcon());

        action.setIcon("https://host.domain/icon.png");
        assertEquals("https://host.domain/icon.png", action.getIcon());

        action.setIcon("completed.gif");
        assertEquals("symbol-status-blue", action.getIcon());
        action.setIcon("db_in.gif");
        assertEquals(Ionicons.getIconClassName("cloud-upload-outline"), action.getIcon());
        action.setIcon("db_out.gif");
        assertEquals(Ionicons.getIconClassName("cloud-download-outline"), action.getIcon());
        action.setIcon("delete.gif");
        assertEquals("symbol-trash", action.getIcon());
        action.setIcon("error.gif");
        assertEquals("symbol-status-red", action.getIcon());
        action.setIcon("folder.gif");
        assertEquals("symbol-folder", action.getIcon());
        action.setIcon("green.gif");
        assertEquals(Emojis.getIconClassName("green_square"), action.getIcon());
        action.setIcon("info.gif");
        assertEquals("symbol-information-circle", action.getIcon());
        action.setIcon("red.gif");
        assertEquals(Emojis.getIconClassName("red_square"), action.getIcon());
        action.setIcon("save.gif");
        assertEquals(Ionicons.getIconClassName("save-outline"), action.getIcon());
        action.setIcon("success.gif");
        assertEquals("symbol-status-blue", action.getIcon());
        action.setIcon("text.gif");
        assertEquals("symbol-document-text", action.getIcon());
        action.setIcon("warning.gif");
        assertEquals("symbol-status-yellow", action.getIcon());
        action.setIcon("yellow.gif");
        assertEquals(Emojis.getIconClassName("yellow_square"), action.getIcon());

        action.setIcon("blue.gif");
        assertEquals(Jenkins.RESOURCE_PATH + "/images/16x16/blue.gif", action.getIcon());
    }

    @Test
    void text() {
        AbstractBadgeAction action = createAction("id", "icon", null, "cssClass", "style", "link", "target");
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
    void cssClass() {
        AbstractBadgeAction action = createAction("id", "icon", "text", null, "style", "link", "target");
        assertNull(action.getCssClass());

        action.setCssClass("");
        assertEquals("", action.getCssClass());

        action.setCssClass("cssClass");
        assertEquals("cssClass", action.getCssClass());
    }

    @Test
    void style() {
        AbstractBadgeAction action = createAction("id", "icon", "text", "cssClass", null, "link", "target");
        assertNull(action.getStyle());

        action.setStyle("");
        assertEquals("", action.getStyle());

        action.setStyle("style");
        assertEquals("style", action.getStyle());
    }

    @Test
    void link() {
        AbstractBadgeAction action = createAction("id", "icon", "text", "cssClass", "style", null, null);
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
    void target() {
        AbstractBadgeAction action = createAction("id", "icon", "text", "cssClass", "style", "link", null);
        assertNull(action.getTarget());

        action.setTarget("");
        assertEquals("", action.getTarget());

        action.setTarget("_blank");
        assertEquals("_blank", action.getTarget());
    }

    @Test
    void iconFileName() {
        AbstractBadgeAction action = createAction(null, null, null, null, null, null, null);
        assertEquals(getIconFileName(), action.getIconFileName());
    }

    @Test
    void displayName() {
        AbstractBadgeAction action = createAction(null, null, null, null, null, null, null);
        assertEquals(getDisplayName(), action.getDisplayName());
    }

    @Test
    void urlName() {
        AbstractBadgeAction action = createAction(null, null, null, null, null, null, null);
        assertEquals(getUrlName(), action.getUrlName());
    }

    protected abstract AbstractBadgeAction createAction(
            String id, String icon, String text, String cssClass, String style, String link, String target);

    protected abstract String getDisplayName();

    protected String getIconFileName() {
        return null;
    }

    protected String getUrlName() {
        return "";
    }
}
