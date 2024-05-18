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
package com.jenkinsci.plugins.badge.dsl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
abstract class AbstractAddBadgeStepTest {

    @Test
    void id(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractAddBadgeStep step = createStep(null, "icon", "text", "cssClass", "style", "link");
        assertNull(step.getId());

        step = createStep("id", "icon", "text", "cssClass", "style", "link");
        assertEquals("id", step.getId());

        step = createStep("", "icon", "text", "cssClass", "style", "link");
        assertEquals("", step.getId());
    }

    @Test
    void icon(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractAddBadgeStep step = createStep("id", null, "text", "cssClass", "style", "link");
        assertNull(step.getIcon());

        step = createStep("id", "", "text", "cssClass", "style", "link");
        assertEquals("", step.getIcon());

        step = createStep("id", "icon", "text", "cssClass", "style", "link");
        assertEquals("icon", step.getIcon());
    }

    @Test
    void text(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractAddBadgeStep step = createStep("id", "icon", null, "cssClass", "style", "link");
        assertNull(step.getText());

        step = createStep("id", "icon", "", "cssClass", "style", "link");
        assertEquals("", step.getText());

        step = createStep("id", "icon", "text", "cssClass", "style", "link");
        assertEquals("text", step.getText());
    }

    @Test
    void cssClass(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", null, "style", "link");
        assertNull(step.getCssClass());

        step = createStep("id", "icon", "text", "", "style", "link");
        assertEquals("", step.getCssClass());

        step = createStep("id", "icon", "text", "cssClass", "style", "link");
        assertEquals("cssClass", step.getCssClass());
    }

    @Test
    void style(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", "cssClass", null, "link");
        assertNull(step.getStyle());

        step = createStep("id", "icon", "text", "cssClass", "", "link");
        assertEquals("", step.getStyle());

        step = createStep("id", "icon", "text", "cssClass", "style", "link");
        assertEquals("style", step.getStyle());
    }

    @Test
    void link(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", "cssClass", "style", null);
        assertNull(step.getLink());

        step = createStep("id", "icon", "text", "cssClass", "style", "");
        assertEquals("", step.getLink());

        step = createStep("id", "icon", "text", "cssClass", "style", "link");
        assertEquals("link", step.getLink());
    }

    protected abstract AbstractAddBadgeStep createStep(
            String id, String icon, String text, String cssClass, String style, String link);
}
