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

import com.jenkinsci.plugins.badge.action.BadgeAction;
import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import hudson.markup.RawHtmlMarkupFormatter;
import hudson.model.BuildBadgeAction;
import java.util.List;
import java.util.UUID;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
@Deprecated(since = "2.0", forRemoval = true)
class LegacyPipelineTest {

    @Test
    void color(JenkinsRule r) throws Exception {
        WorkflowRun run = runJon(r, "addBadge(color: 'red')");

        List<BuildBadgeAction> badgeActions = run.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertEquals("color: red;", action.getStyle());

        run = runJon(r, "addBadge(color: 'jenkins-!-color-red')");

        badgeActions = run.getBadgeActions();
        assertEquals(1, badgeActions.size());

        action = (BadgeAction) badgeActions.get(0);
        assertEquals("color: var(--red);", action.getStyle());

        run = runJon(r, "addBadge(color: 'jenkins-!-warning-color')");

        badgeActions = run.getBadgeActions();
        assertEquals(1, badgeActions.size());

        action = (BadgeAction) badgeActions.get(0);
        assertEquals("color: var(--warning-color);", action.getStyle());

        run = runJon(r, "addBadge(color: null)");

        badgeActions = run.getBadgeActions();
        assertEquals(1, badgeActions.size());

        action = (BadgeAction) badgeActions.get(0);
        assertNull(action.getStyle());
    }

    @Test
    void appendText(JenkinsRule r) throws Exception {
        WorkflowRun run = runJon(r, "createSummary(text: 'Test Text')");

        List<BadgeSummaryAction> actions = run.getActions(BadgeSummaryAction.class);
        assertEquals(1, actions.size());

        BadgeSummaryAction action = actions.get(0);
        assertEquals("Test Text", action.getText());

        run = runJon(r, "def summary = createSummary(text: 'Test Text')\n" + "summary.appendText(' More Text', true)");

        actions = run.getActions(BadgeSummaryAction.class);
        assertEquals(1, actions.size());

        action = actions.get(0);
        assertEquals("Test Text More Text", action.getText());

        run = runJon(r, "def summary = createSummary(text: 'Test Text')\n" + "summary.appendText(' More Text', false)");

        actions = run.getActions(BadgeSummaryAction.class);
        assertEquals(1, actions.size());

        action = actions.get(0);
        assertEquals("Test Text More Text", action.getText());

        run = runJon(
                r,
                "def summary = createSummary(text: 'Test Text')\n"
                        + "summary.appendText(' More Text', false, false, false, null)");

        actions = run.getActions(BadgeSummaryAction.class);
        assertEquals(1, actions.size());

        action = actions.get(0);
        assertEquals("Test Text More Text", action.getText());

        r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
        run = runJon(
                r,
                "def summary = createSummary(text: 'Test Text')\n"
                        + "summary.appendText(' More Text', true, true, true, 'red')");

        actions = run.getActions(BadgeSummaryAction.class);
        assertEquals(1, actions.size());

        action = actions.get(0);
        assertEquals("Test Text<b><i> More Text</i></b>", action.getText());
    }

    private static WorkflowRun runJon(JenkinsRule r, String script) throws Exception {
        WorkflowJob project =
                r.jenkins.createProject(WorkflowJob.class, UUID.randomUUID().toString());
        project.setDefinition(new CpsFlowDefinition(script, true));
        return r.assertBuildStatusSuccess(project.scheduleBuild2(0));
    }
}
