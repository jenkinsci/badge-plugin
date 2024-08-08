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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

class AddWarningBadgeStepTest extends AddBadgeStepTest {

    @Override
    @Test
    void icon(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractAddBadgeStep step = createStep("id", null, "text", "cssClass", "style", "link");
        assertEquals("symbol-warning plugin-ionicons-api", step.getIcon());

        step = createStep("id", "", "text", "cssClass", "style", "link");
        assertEquals("symbol-warning plugin-ionicons-api", step.getIcon());

        step = createStep("id", "icon", "text", "cssClass", "style", "link");
        assertEquals("symbol-warning plugin-ionicons-api", step.getIcon());
    }

    @Override
    @Test
    void cssClass(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", null, "style", "link");
        assertNull(step.getCssClass());

        step = createStep("id", "icon", "text", "", "style", "link");
        assertNull(step.getCssClass());

        step = createStep("id", "icon", "text", "cssClass", "style", "link");
        assertNull(step.getCssClass());
    }

    @Override
    @Test
    void style(@SuppressWarnings("unused") JenkinsRule r) {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", "cssClass", null, "link");
        assertEquals("color: var(--warning-color)", step.getStyle());

        step = createStep("id", "icon", "text", "cssClass", "", "link");
        assertEquals("color: var(--warning-color)", step.getStyle());

        step = createStep("id", "icon", "text", "cssClass", "style", "link");
        assertEquals("color: var(--warning-color)", step.getStyle());
    }

    @Override
    protected AbstractAddBadgeStep createStep(
            String id, String icon, String text, String cssClass, String style, String link) {
        return new AddWarningBadgeStep(id, text, link);
    }
}
