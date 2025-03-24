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

import io.jenkins.plugins.emoji.symbols.Emojis;
import io.jenkins.plugins.ionicons.Ionicons;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class BadgeSummaryActionTest extends AbstractBadgeActionTest {

    @Override
    @Test
    void icon() {
        AbstractBadgeAction action = createAction("id", null, "text", "cssClass", "style", "link");
        assertEquals(Jenkins.RESOURCE_PATH + "/images/16x16/empty.png", action.getIcon());

        action.setIcon("");
        assertEquals(Jenkins.RESOURCE_PATH + "/images/16x16/empty.png", action.getIcon());

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

    @Override
    protected AbstractBadgeAction createAction(
            String id, String icon, String text, String cssClass, String style, String link) {
        return new BadgeSummaryAction(id, icon, text, cssClass, style, link);
    }

    @Override
    protected String getDisplayName() {
        return "Badge Summary Action";
    }
}
