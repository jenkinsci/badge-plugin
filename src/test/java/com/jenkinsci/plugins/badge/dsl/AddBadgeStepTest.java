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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.jenkinsci.plugins.badge.action.BadgeAction;
import hudson.model.BuildBadgeAction;
import java.util.List;
import java.util.UUID;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

class AddBadgeStepTest extends AbstractAddBadgeStepTest {

    @Test
    void add(JenkinsRule r) throws Exception {
        AbstractAddBadgeStep step = createStep(
                UUID.randomUUID().toString(),
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io");
        runAddJob(r, step, false);
    }

    @Test
    void addInNode(JenkinsRule r) throws Exception {
        AbstractAddBadgeStep step = createStep(
                UUID.randomUUID().toString(),
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io");
        runAddJob(r, step, true);
    }

    @Test
    void modify(JenkinsRule r) throws Exception {
        AbstractAddBadgeStep step = createStep(
                UUID.randomUUID().toString(),
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io");
        runModifyJob(r, step, false);
    }

    @Test
    void modifyInNode(JenkinsRule r) throws Exception {
        AbstractAddBadgeStep step = createStep(
                UUID.randomUUID().toString(),
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io");
        runModifyJob(r, step, true);
    }

    protected void runAddJob(JenkinsRule r, AbstractAddBadgeStep step, boolean inNode) throws Exception {
        WorkflowJob project = r.jenkins.createProject(WorkflowJob.class, "project");

        String script = step.toString();
        if (inNode) {
            script = "node() { " + script + " }";
        }

        project.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun run = r.assertBuildStatusSuccess(project.scheduleBuild2(0));

        assertFields(step, run);
    }

    protected void runModifyJob(JenkinsRule r, AbstractAddBadgeStep step, boolean inNode) throws Exception {
        WorkflowJob project = r.jenkins.createProject(WorkflowJob.class, "project");

        String actualText = step.getText();
        step.setText(UUID.randomUUID().toString());
        assertNotEquals(actualText, step.getText());

        String script = "def badge = " + step + "\n";
        script += "badge.setText('" + actualText + "')";
        if (inNode) {
            script = "node() { " + script + " }";
        }

        project.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun run = r.assertBuildStatusSuccess(project.scheduleBuild2(0));

        step.setText(actualText);
        assertEquals(actualText, step.getText());

        assertFields(step, run);
    }

    protected void assertFields(AbstractAddBadgeStep step, WorkflowRun run) {
        List<BuildBadgeAction> badgeActions = run.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertEquals(step.getId(), action.getId());
        assertEquals(step.getIcon(), action.getIcon());
        assertEquals(step.getText(), action.getText());
        assertEquals(step.getCssClass(), action.getCssClass());
        assertEquals(step.getStyle(), action.getStyle());
        assertEquals(step.getLink(), action.getLink());
    }

    @Override
    protected AbstractAddBadgeStep createStep(
            String id, String icon, String text, String cssClass, String style, String link) {
        return new AddBadgeStep(id, icon, text, cssClass, style, link);
    }
}
