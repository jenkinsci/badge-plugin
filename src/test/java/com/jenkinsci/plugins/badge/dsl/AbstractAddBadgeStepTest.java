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
package com.jenkinsci.plugins.badge.dsl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
abstract class AbstractAddBadgeStepTest {

    protected static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    abstract void defaultConstructor();

    @Test
    void id() {
        AbstractAddBadgeStep step = createStep(null, "icon", "text", "cssClass", "style", "link", "target");
        assertThat(step.getId(), nullValue());

        step = createStep("id", "icon", "text", "cssClass", "style", "link", "target");
        assertThat(step.getId(), is("id"));

        step = createStep("", "icon", "text", "cssClass", "style", "link", "target");
        assertThat(step.getId(), emptyString());
    }

    @Test
    void icon() {
        AbstractAddBadgeStep step = createStep("id", null, "text", "cssClass", "style", "link", "target");
        assertThat(step.getIcon(), nullValue());

        step.setIcon("");
        assertThat(step.getIcon(), emptyString());

        step.setIcon("icon");
        assertThat(step.getIcon(), is("icon"));
    }

    @Test
    void text() {
        AbstractAddBadgeStep step = createStep("id", "icon", null, "cssClass", "style", "link", "target");
        assertThat(step.getText(), nullValue());

        step.setText("");
        assertThat(step.getText(), emptyString());

        step.setText("text");
        assertThat(step.getText(), is("text"));
    }

    @Test
    void cssClass() {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", null, "style", "link", "target");
        assertThat(step.getCssClass(), nullValue());

        step.setCssClass("");
        assertThat(step.getCssClass(), emptyString());

        step.setCssClass("cssClass");
        assertThat(step.getCssClass(), is("cssClass"));
    }

    @Test
    void style() {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", "cssClass", null, "link", "target");
        assertThat(step.getStyle(), nullValue());

        step.setStyle("");
        assertThat(step.getStyle(), emptyString());

        step.setStyle("style");
        assertThat(step.getStyle(), is("style"));
    }

    @Test
    void link() {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", "cssClass", "style", null, "target");
        assertThat(step.getLink(), nullValue());

        step.setLink("");
        assertThat(step.getLink(), emptyString());

        step.setLink("link");
        assertThat(step.getLink(), is("link"));
    }

    @Test
    void target() {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", "cssClass", "style", "link", null);
        assertThat(step.getTarget(), nullValue());

        step.setTarget("");
        assertThat(step.getTarget(), emptyString());

        step.setTarget("target");
        assertThat(step.getTarget(), is("target"));
    }

    @Test
    void string() {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", "cssClass", "style", "link", "target");
        assertThat(step.toString(), notNullValue());
        assertThat(step.toString(), startsWith(step.getDescriptor().getFunctionName()));

        step = createStep(null, null, null, null, null, null, null);
        assertThat(step.toString(), notNullValue());
        assertThat(step.toString(), is(step.getDescriptor().getFunctionName() + "()"));
    }

    protected abstract AbstractAddBadgeStep createStep(
            String id, String icon, String text, String cssClass, String style, String link, String target);
}
