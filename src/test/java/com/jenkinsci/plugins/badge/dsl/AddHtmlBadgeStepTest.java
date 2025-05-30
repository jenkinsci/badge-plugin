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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jenkinsci.plugins.badge.action.HtmlBadgeAction;
import hudson.markup.RawHtmlMarkupFormatter;
import hudson.model.BuildBadgeAction;
import java.util.List;
import java.util.UUID;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
@Deprecated(since = "2.0", forRemoval = true)
class AddHtmlBadgeStepTest {

    private static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void id() {
        AddHtmlBadgeStep step = new AddHtmlBadgeStep(null);
        assertNull(step.getId());

        String id = UUID.randomUUID().toString();
        step.setId(id);
        assertEquals(id, step.getId());
    }

    @Test
    void html() {
        AddHtmlBadgeStep step = new AddHtmlBadgeStep(null);
        assertNull(step.getHtml());

        String html = UUID.randomUUID().toString();
        step = new AddHtmlBadgeStep(html);
        assertEquals(html, step.getHtml());
    }

    @Test
    void deprecated() {
        AddHtmlBadgeStep step = new AddHtmlBadgeStep(null);
        assertTrue(step.getDescriptor().isAdvanced());
    }

    @Test
    void addHtmlBadge() throws Exception {
        String html = UUID.randomUUID().toString();
        testAddHtmlBadge(html, html);
    }

    @Test
    void addHtmlBadge_remove_script() throws Exception {
        r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
        String uuid = UUID.randomUUID().toString();
        String html = uuid + "<script>alert('exploit!');</script>";
        testAddHtmlBadge(html, uuid);
    }

    private static void testAddHtmlBadge(String html, String expected) throws Exception {
        WorkflowJob p = r.createProject(WorkflowJob.class);

        String script = "addHtmlBadge(\"" + html + "\")";

        p.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertEquals(1, badgeActions.size());

        HtmlBadgeAction action = (HtmlBadgeAction) badgeActions.get(0);
        assertEquals(expected, action.getHtml());
        assertEquals(html, action.getRawHtml());
    }
}
