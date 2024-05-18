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
        runJob(r, false);
    }

    @Test
    void addInNode(JenkinsRule r) throws Exception {
        runJob(r, true);
    }

    protected void runJob(JenkinsRule r, boolean inNode) throws Exception {
        String id = UUID.randomUUID().toString();
        String icon = "symbol-rocket plugin-ionicons-api";
        String text = "Test Text";
        String cssClass = "icon-md";
        String style = "color: green";
        String link = "https://jenkins.io";
        WorkflowJob project = r.jenkins.createProject(WorkflowJob.class, "project");

        String script = "addBadge id: '" + id + "', icon: '" + icon + "',  text: '" + text + "', cssClass: '" + cssClass
                + "', style: '" + style + "', link: '" + link + "'";
        if (inNode) {
            script = "node() { " + script + " }";
        }

        project.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun run = r.assertBuildStatusSuccess(project.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = run.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertEquals(id, action.getId());
        assertEquals(icon, action.getIcon());
        assertEquals(text, action.getText());
        assertEquals(cssClass, action.getCssClass());
        assertEquals(style, action.getStyle());
        assertEquals(link, action.getLink());
    }

    @Override
    protected AbstractAddBadgeStep createStep(
            String id, String icon, String text, String cssClass, String style, String link) {
        return new AddBadgeStep(id, icon, text, cssClass, style, link);
    }
}
