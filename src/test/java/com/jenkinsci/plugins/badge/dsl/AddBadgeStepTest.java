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

import com.jenkinsci.plugins.badge.action.BadgeAction;
import hudson.model.BuildBadgeAction;
import hudson.model.Result;
import java.util.List;
import java.util.UUID;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

class AddBadgeStepTest extends AbstractBadgeTest {

    @Test
    void addBadge(JenkinsRule r) throws Exception {
        addBadge(r, false);
    }

    @Test
    void addBadge_in_node(JenkinsRule r) throws Exception {
        addBadge(r, true);
    }

    @Test
    void addInfoBadge(JenkinsRule r) throws Exception {
        addStatusBadge(r, "addInfoBadge", "symbol-information-circle plugin-ionicons-api", false);
        addStatusBadge(r, "addInfoBadge", "symbol-information-circle plugin-ionicons-api", true);
    }

    @Test
    void addWarningBadge(JenkinsRule r) throws Exception {
        addStatusBadge(r, "addWarningBadge", "symbol-warning plugin-ionicons-api", false);
        addStatusBadge(r, "addWarningBadge", "symbol-warning plugin-ionicons-api", true);
    }

    @Test
    void addErrorBadge(JenkinsRule r) throws Exception {
        addStatusBadge(r, "addErrorBadge", "symbol-remove-circle plugin-ionicons-api", false);
        addStatusBadge(r, "addErrorBadge", "symbol-remove-circle plugin-ionicons-api", true);
    }

    @Test
    void addBadge_invalid_link(JenkinsRule r) throws Exception {
        String icon = UUID.randomUUID().toString();
        String text = UUID.randomUUID().toString();
        String link = "javascript:" + UUID.randomUUID();
        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");

        String script = "addBadge(icon:\"" + icon + "\",  text:\"" + text + "\",  link:\"" + link + "\")";
        p.setDefinition(new CpsFlowDefinition(script, true));
        r.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0));
    }

    @Test
    void addBadge_invalid_text(JenkinsRule r) throws Exception {
        String icon = UUID.randomUUID().toString();
        String textPrefix = UUID.randomUUID().toString();
        String text = textPrefix + "');alert('foo";
        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");

        String script = "addBadge(icon: '" + icon + "',  text:\"" + text + "\")";
        p.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertTrue(action.getIconPath().endsWith(icon));
        assertEquals(textPrefix + "&#039;);alert(&#039;foo", action.getText());
    }

    private void addBadge(JenkinsRule r, boolean inNode) throws Exception {
        String icon = UUID.randomUUID().toString();
        String text = UUID.randomUUID().toString();
        String link = "https://" + UUID.randomUUID();
        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");

        String script = "addBadge icon: '" + icon + "',  text: '" + text + "',  link: '" + link + "'";
        if (inNode) {
            script = "node() {" + script + "}";
        }

        p.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertTrue(action.getIconPath().endsWith(icon));
        assertEquals(text, action.getText());
        assertEquals(link, action.getLink());
    }

    private void addStatusBadge(JenkinsRule r, String functionName, String expectedIcon, boolean withLink)
            throws Exception {
        String text = UUID.randomUUID().toString();
        String link = "mailto://" + UUID.randomUUID();

        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, text);
        String script = functionName + " text: '" + text + "'";
        if (withLink) {
            script += ",  link: '" + link + "'";
        }

        p.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertEquals(text, action.getText());
        assertTrue(action.getIconPath().endsWith(expectedIcon));
        if (withLink) {
            assertEquals(link, action.getLink());
        } else {
            assertNull(action.getLink());
        }
    }
}
