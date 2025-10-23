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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

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
        assertThat(action.getId(), notNullValue());
        assertThat(action.getId(), matchesPattern("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));

        action = createAction("id", "icon", "text", "cssClass", "style", "link", "target");
        assertThat(action.getId(), is("id"));

        action = createAction("", "icon", "text", "cssClass", "style", "link", "target");
        assertThat(action.getId(), emptyString());
    }

    @Test
    void icon() {
        AbstractBadgeAction action = createAction("id", null, "text", "cssClass", "style", "link", "target");
        assertThat(action.getIcon(), nullValue());

        action.setIcon("");
        assertThat(action.getIcon(), emptyString());

        action.setIcon("/relative/url/icon.png");
        assertThat(action.getIcon(), is("/relative/url/icon.png"));

        action.setIcon("symbol-rocket plugin-ionicons-api");
        assertThat(action.getIcon(), is("symbol-rocket plugin-ionicons-api"));

        action.setIcon("symbol-cube");
        assertThat(action.getIcon(), is("symbol-cube"));

        action.setIcon("icon-gear");
        assertThat(action.getIcon(), is("icon-gear"));

        action.setIcon("https://host.domain/icon.png");
        assertThat(action.getIcon(), is("https://host.domain/icon.png"));

        action.setIcon("completed.gif");
        assertThat(action.getIcon(), is("symbol-status-blue"));
        action.setIcon("db_in.gif");
        assertThat(action.getIcon(), is(Ionicons.getIconClassName("cloud-upload-outline")));
        action.setIcon("db_out.gif");
        assertThat(action.getIcon(), is(Ionicons.getIconClassName("cloud-download-outline")));
        action.setIcon("delete.gif");
        assertThat(action.getIcon(), is("symbol-trash"));
        action.setIcon("error.gif");
        assertThat(action.getIcon(), is("symbol-status-red"));
        action.setIcon("folder.gif");
        assertThat(action.getIcon(), is("symbol-folder"));
        action.setIcon("green.gif");
        assertThat(action.getIcon(), is(Emojis.getIconClassName("green_square")));
        action.setIcon("info.gif");
        assertThat(action.getIcon(), is("symbol-information-circle"));
        action.setIcon("red.gif");
        assertThat(action.getIcon(), is(Emojis.getIconClassName("red_square")));
        action.setIcon("save.gif");
        assertThat(action.getIcon(), is(Ionicons.getIconClassName("save-outline")));
        action.setIcon("success.gif");
        assertThat(action.getIcon(), is("symbol-status-blue"));
        action.setIcon("text.gif");
        assertThat(action.getIcon(), is("symbol-document-text"));
        action.setIcon("warning.gif");
        assertThat(action.getIcon(), is("symbol-status-yellow"));
        action.setIcon("yellow.gif");
        assertThat(action.getIcon(), is(Emojis.getIconClassName("yellow_square")));

        // does not exist in core
        action.setIcon("icon.png");
        assertThat(action.getIcon(), is("icon.png"));

        // core resource in 16x16
        action.setIcon("blue.gif");
        assertThat(action.getIcon(), is(Jenkins.RESOURCE_PATH + "/images/16x16/blue.gif"));

        // core resource in svgs
        action.setIcon("error.svg");
        assertThat(action.getIcon(), is(Jenkins.RESOURCE_PATH + "/images/svgs/error.svg"));

        // can not be validated
        action.setIcon("[/]");
        assertThat(action.getIcon(), is("[/]"));
    }

    @Test
    void text() {
        AbstractBadgeAction action = createAction("id", "icon", null, "cssClass", "style", "link", "target");
        assertThat(action.getText(), nullValue());

        action.setText("");
        assertThat(action.getText(), emptyString());

        action.setText("text");
        assertThat(action.getText(), is("text"));

        r.jenkins.setMarkupFormatter(new EscapedMarkupFormatter());
        action.setText("<p>Plain Text</p>");
        assertThat(action.getText(), is("&lt;p&gt;Plain Text&lt;/p&gt;"));

        action.setText("<script>alert('Plain Text')</script>");
        assertThat(action.getText(), is("&lt;script&gt;alert(&#039;Plain Text&#039;)&lt;/script&gt;"));

        r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
        action.setText("<p>Safe HTML</p><script>alert('Unsafe HTML')</script>");
        assertThat(action.getText(), is("<p>Safe HTML</p>"));

        action.setText("<script>alert('Unsafe HTML')</script>");
        assertThat(action.getText(), emptyString());

        MarkupFormatter formatter = new MarkupFormatter() {
            @Override
            public void translate(String markup, @NonNull Writer output) throws IOException {
                throw new IOException("Oh no!");
            }
        };
        r.jenkins.setMarkupFormatter(formatter);
        action.setText("text");
        assertThat(
                action.getText(),
                is("<b><font color=\"var(--error-color)\">Error preparing badge text for UI</font></b>"));
    }

    @Test
    void cssClass() {
        AbstractBadgeAction action = createAction("id", "icon", "text", null, "style", "link", "target");
        assertThat(action.getCssClass(), nullValue());

        action.setCssClass("");
        assertThat(action.getCssClass(), emptyString());

        action.setCssClass("cssClass");
        assertThat(action.getCssClass(), is("cssClass"));
    }

    @Test
    void style() {
        AbstractBadgeAction action = createAction("id", "icon", "text", "cssClass", null, "link", "target");
        assertThat(action.getStyle(), nullValue());

        action.setStyle("");
        assertThat(action.getStyle(), emptyString());

        action.setStyle("style");
        assertThat(action.getStyle(), is("style"));
    }

    @Test
    void link() {
        AbstractBadgeAction action = createAction("id", "icon", "text", "cssClass", "style", null, null);
        assertThat(action.getLink(), nullValue());

        action.setLink("");
        assertThat(action.getLink(), emptyString());

        action.setLink("link");
        assertThat(action.getLink(), nullValue());

        action.setLink("/relative/url");
        assertThat(action.getLink(), is("/relative/url"));

        action.setLink("https://host.domain");
        assertThat(action.getLink(), is("https://host.domain"));

        action.setLink("mailto:foo@bar.com");
        assertThat(action.getLink(), is("mailto:foo@bar.com"));
    }

    @Test
    void target() {
        AbstractBadgeAction action = createAction("id", "icon", "text", "cssClass", "style", "link", null);
        assertThat(action.getTarget(), nullValue());

        action.setTarget("");
        assertThat(action.getTarget(), emptyString());

        action.setTarget("target");
        assertThat(action.getTarget(), is("target"));
    }

    @Test
    void iconFileName() {
        AbstractBadgeAction action = createAction(null, null, null, null, null, null, null);
        assertThat(action.getIconFileName(), is(getIconFileName()));
    }

    @Test
    void displayName() {
        AbstractBadgeAction action = createAction(null, null, null, null, null, null, null);
        assertThat(action.getDisplayName(), is(getDisplayName()));
    }

    @Test
    void urlName() {
        AbstractBadgeAction action = createAction(null, null, null, null, null, null, null);
        assertThat(action.getUrlName(), is(getUrlName()));
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
