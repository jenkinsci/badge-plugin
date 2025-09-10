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
import static org.hamcrest.Matchers.nullValue;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.markup.MarkupFormatter;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
@Deprecated(since = "2.0", forRemoval = true)
class HtmlBadgeActionTest {

    private static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void html() {
        HtmlBadgeAction action = HtmlBadgeAction.createHtmlBadge(null);
        assertThat(action.getHtml(), emptyString());

        String html = UUID.randomUUID().toString();
        action = HtmlBadgeAction.createHtmlBadge(html);
        assertThat(action.getHtml(), is(html));

        MarkupFormatter formatter = new MarkupFormatter() {
            @Override
            public void translate(String markup, @NonNull Writer output) throws IOException {
                throw new IOException("Oh no!");
            }
        };
        r.jenkins.setMarkupFormatter(formatter);
        assertThat(
                action.getHtml(),
                is("<b><font color=\"var(--error-color)\">Error preparing HTML content for UI</font></b>"));
    }

    @Test
    void urlName() {
        HtmlBadgeAction action = HtmlBadgeAction.createHtmlBadge(null);
        assertThat(action.getUrlName(), emptyString());
    }

    @Test
    void displayName() {
        HtmlBadgeAction action = HtmlBadgeAction.createHtmlBadge(null);
        assertThat(action.getDisplayName(), emptyString());
    }

    @Test
    void iconFileName() {
        HtmlBadgeAction action = HtmlBadgeAction.createHtmlBadge(null);
        assertThat(action.getIconFileName(), nullValue());
    }
}
