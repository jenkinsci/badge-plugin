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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class AddWarningBadgeStepTest extends AddBadgeStepTest {

    @Override
    @Test
    void defaultConstructor() {
        AbstractAddBadgeStep step = new AddWarningBadgeStep();
        assertThat(step.getId(), nullValue());
        assertThat(step.getIcon(), is("symbol-warning plugin-ionicons-api"));
        assertThat(step.getText(), nullValue());
        assertThat(step.getCssClass(), nullValue());
        assertThat(step.getStyle(), is("color: var(--warning-color)"));
        assertThat(step.getLink(), nullValue());
        assertThat(step.getTarget(), nullValue());
    }

    @Override
    @Test
    void icon() {
        AbstractAddBadgeStep step = createStep("id", null, "text", "cssClass", "style", "link", "target");
        assertThat(step.getIcon(), is("symbol-warning plugin-ionicons-api"));

        step = createStep("id", "", "text", "cssClass", "style", "link", "target");
        assertThat(step.getIcon(), is("symbol-warning plugin-ionicons-api"));

        step = createStep("id", "icon", "text", "cssClass", "style", "link", "target");
        assertThat(step.getIcon(), is("symbol-warning plugin-ionicons-api"));
    }

    @Override
    @Test
    void cssClass() {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", null, "style", "link", "target");
        assertThat(step.getCssClass(), nullValue());

        step = createStep("id", "icon", "text", "", "style", "link", "target");
        assertThat(step.getCssClass(), nullValue());

        step = createStep("id", "icon", "text", "cssClass", "style", "link", "target");
        assertThat(step.getCssClass(), nullValue());
    }

    @Override
    @Test
    void style() {
        AbstractAddBadgeStep step = createStep("id", "icon", "text", "cssClass", null, "link", "target");
        assertThat(step.getStyle(), is("color: var(--warning-color)"));

        step = createStep("id", "icon", "text", "cssClass", "", "link", "target");
        assertThat(step.getStyle(), is("color: var(--warning-color)"));

        step = createStep("id", "icon", "text", "cssClass", "style", "link", "target");
        assertThat(step.getStyle(), is("color: var(--warning-color)"));
    }

    @Override
    protected AbstractAddBadgeStep createStep(
            String id, String icon, String text, String cssClass, String style, String link, String target) {
        return new AddWarningBadgeStep(id, text, link, target);
    }
}
