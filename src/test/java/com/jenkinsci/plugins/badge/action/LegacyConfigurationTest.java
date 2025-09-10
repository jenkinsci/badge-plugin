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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import hudson.XmlFile;
import java.io.File;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
@Deprecated(since = "2.0", forRemoval = true)
class LegacyConfigurationTest {

    @SuppressWarnings("unused")
    private static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void badgeAction() throws Exception {
        BadgeAction action = readConfiguration("badge-action.xml");
        assertThat(action.getIcon(), notNullValue());
        assertThat(action.getStyle(), notNullValue());

        action = readConfiguration("badge-action-null.xml");
        assertThat(action.getIcon(), nullValue());
        assertThat(action.getStyle(), nullValue());

        action = readConfiguration("badge-action-borderColor-null.xml");
        assertThat(action.getStyle(), notNullValue());

        action = readConfiguration("badge-action-jenkins.xml");
        assertThat(action.getStyle(), notNullValue());

        action = readConfiguration("badge-action-jenkins-color.xml");
        assertThat(action.getStyle(), notNullValue());
    }

    @Test
    void badgeSummaryAction() throws Exception {
        BadgeSummaryAction action = readConfiguration("summary-action.xml");
        assertThat(action.getText(), notNullValue());

        action = readConfiguration("summary-action-null.xml");
        assertThat(action.getText(), nullValue());
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractBadgeAction> T readConfiguration(String file) throws Exception {
        return (T) new XmlFile(new File("src/test/resources/legacy-configuration", file)).read();
    }
}
