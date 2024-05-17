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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jenkinsci.plugins.badge.BadgePlugin;
import hudson.model.Hudson;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class BadgeActionTest {

    @Test
    void getIconPath(@SuppressWarnings("unused") JenkinsRule r) {
        assertNull(BadgeAction.getIconPath(null));
        assertEquals("/icon.png", BadgeAction.getIconPath("/icon.png"));
        assertEquals("http://foo.com/icon.png", BadgeAction.getIconPath("http://foo.com/icon.png"));
        assertEquals("https://foo.com/icon.png", BadgeAction.getIconPath("https://foo.com/icon.png"));

        assertEquals(Hudson.RESOURCE_PATH + "/images/16x16/http.png", BadgeAction.getIconPath("http.png"));

        assertEquals(
                "symbol-information-circle-outline plugin-ionicons-api",
                BadgeAction.getIconPath("symbol-information-circle-outline plugin-ionicons-api"));
    }

    @Test
    void getIconClass(@SuppressWarnings("unused") JenkinsRule r) {
        BadgeAction action = BadgeAction.createBadge("info.gif", "text");
        assertEquals("icon-sm", action.getIconClass());
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "text");
        assertEquals("icon-sm", action.getIconClass());
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "#000000", "", null);
        assertEquals("icon-sm", action.getIconClass());
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "blue", "", null);
        assertEquals("icon-sm jenkins-!-color-blue", action.getIconClass());
        action = BadgeAction.createBadge("/foo/symbol-star.gif", "blue", "", null);
        assertEquals("icon-sm jenkins-!-color-blue", action.getIconClass());
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "jenkins-!-color-red", "", null);
        assertEquals("icon-sm jenkins-!-color-red", action.getIconClass());
        // teal is not in the palette
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "teal", "", null);
        assertEquals("icon-sm", action.getIconClass());
    }

    @Test
    void getIconColorStyle(@SuppressWarnings("unused") JenkinsRule r) {
        BadgeAction action = BadgeAction.createBadge("info.gif", "text");
        assertNull(action.getIconColorStyle());
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "#000000", "", null);
        assertEquals("#000000", action.getIconColorStyle());
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "var(--yellow)", "", null);
        assertEquals("var(--yellow)", action.getIconColorStyle());
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "jenkins-!-color-blue", "", null);
        assertNull(action.getIconColorStyle());
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "blue", "", null);
        assertNull(action.getIconColorStyle());
        action = BadgeAction.createBadge("symbol-star plugin-ionicons-api", "teal", "", null);
        assertEquals("teal", action.getIconColorStyle());
    }

    @Test
    void createInfoBadge(@SuppressWarnings("unused") JenkinsRule r) {
        BadgeAction action = BadgeAction.createInfoBadge("This is an info badge");
        assertNull(action.getIconFileName());
        assertFalse(action.isTextOnly());
        assertEquals("", action.getDisplayName());
        assertEquals("", action.getUrlName());
    }

    @Test
    void createWarningBadge(@SuppressWarnings("unused") JenkinsRule r) {
        BadgeAction action = BadgeAction.createWarningBadge("This is a warning badge");
        assertNull(action.getIconFileName());
        assertFalse(action.isTextOnly());
        assertEquals("", action.getDisplayName());
        assertEquals("", action.getUrlName());
    }

    @Test
    void createErrorBadge(@SuppressWarnings("unused") JenkinsRule r) {
        BadgeAction action = BadgeAction.createErrorBadge("This is an error badge");
        assertNull(action.getIconFileName());
        assertFalse(action.isTextOnly());
        assertEquals("", action.getDisplayName());
        assertEquals("", action.getUrlName());
    }

    @Test
    void createShortText(@SuppressWarnings("unused") JenkinsRule r) {
        BadgeAction action = BadgeAction.createShortText("This is a short text badge");
        assertNull(action.getIconFileName());
        assertTrue(action.isTextOnly());
        assertEquals("", action.getDisplayName());
        assertEquals("", action.getUrlName());
    }

    @Test
    void createShortTextWithAllArgs(@SuppressWarnings("unused") JenkinsRule r) {
        BadgeAction action = BadgeAction.createShortText("Short text badge", "red", "black", "blue", "green", "/link");
        assertEquals("/link", action.getLink());
        assertNull(action.getIconFileName());
        assertTrue(action.isTextOnly());
    }

    @Test
    void createBadgeWithInvalidLink(@SuppressWarnings("unused") JenkinsRule r) {
        String invalidLink = "invalid-link";

        // Exception thrown for invalid link by default
        assertThrows(IllegalArgumentException.class, () -> {
            BadgeAction.createBadge("info.gif", "Link in badge", invalidLink);
        });

        boolean defaultValue = BadgePlugin.get().isDisableFormatHTML();

        // Exception not thrown if HTMl formatting is disabled
        BadgePlugin.get().setDisableFormatHTML(true);
        BadgeAction action = BadgeAction.createBadge("info.gif", "Link in badge", invalidLink);
        assertEquals(invalidLink, action.getLink());

        // Restore the default, do not disable HTML formatting
        BadgePlugin.get().setDisableFormatHTML(defaultValue);
    }

    @Test
    void createBadgeWithNullLink(@SuppressWarnings("unused") JenkinsRule r) {
        BadgeAction action = BadgeAction.createBadge("info.gif", "Link in badge", null);
        assertNull(action.getLink());
    }

    @Test
    void getTextWithHTMLFormatDisabled(@SuppressWarnings("unused") JenkinsRule r) {
        boolean defaultValue = BadgePlugin.get().isDisableFormatHTML();
        String expectedText = "Error badge text";
        String javaScript = "<script>alert('abc');</script>";

        // JavaScript discarded when formatHTML is enabled (the default)
        BadgeAction action = BadgeAction.createErrorBadge(expectedText + javaScript);
        assertEquals(expectedText, action.getText());

        // JavaScript retained when formatHTML is disabled
        BadgePlugin.get().setDisableFormatHTML(true);
        assertEquals(expectedText + javaScript, action.getText());

        // Restore the default, do not disable HTML formatting
        BadgePlugin.get().setDisableFormatHTML(defaultValue);
    }
}
