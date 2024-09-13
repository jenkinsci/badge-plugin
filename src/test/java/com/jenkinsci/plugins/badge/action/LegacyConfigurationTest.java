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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import hudson.XmlFile;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
@Deprecated(since = "2.0", forRemoval = true)
class LegacyConfigurationTest {

    @Test
    void badgeAction(@SuppressWarnings("unused") JenkinsRule r) throws Exception {
        BadgeAction action = readConfiguration("badge-action.xml");
        assertNotNull(action.getIcon());
        assertNotNull(action.getStyle());

        action = readConfiguration("badge-action-null.xml");
        assertNull(action.getIcon());
        assertNull(action.getStyle());

        action = readConfiguration("badge-action-borderColor-null.xml");
        assertNotNull(action.getStyle());

        action = readConfiguration("badge-action-jenkins.xml");
        assertNotNull(action.getStyle());

        action = readConfiguration("badge-action-jenkins-color.xml");
        assertNotNull(action.getStyle());
    }

    @Test
    void badgeSummaryAction(@SuppressWarnings("unused") JenkinsRule r) throws Exception {
        BadgeSummaryAction action = readConfiguration("summary-action.xml");
        assertNotNull(action.getText());

        action = readConfiguration("summary-action-null.xml");
        assertNull(action.getText());
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractBadgeAction> T readConfiguration(String file) throws Exception {
        return (T) new XmlFile(new File("src/test/resources/legacy-configuration", file)).read();
    }
}
