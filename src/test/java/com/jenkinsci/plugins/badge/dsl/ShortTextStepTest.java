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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.jenkinsci.plugins.badge.action.BadgeAction;
import hudson.model.BuildBadgeAction;
import java.util.List;
import java.util.Random;
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
class ShortTextStepTest {

    private static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void text() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertThat(step.getText(), nullValue());

        String text = UUID.randomUUID().toString();
        step = new AddShortTextStep(text);
        assertThat(step.getText(), is(text));
    }

    @Test
    void color() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertThat(step.getColor(), nullValue());

        step.setColor("red");
        assertThat(step.getColor(), is("red"));
    }

    @Test
    void background() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertThat(step.getBackground(), nullValue());

        step.setBackground("red");
        assertThat(step.getBackground(), is("red"));
    }

    @Test
    void border() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertThat(step.getBorder(), nullValue());

        step.setBorder(1);
        assertThat(step.getBorder(), is(1));
    }

    @Test
    void borderColor() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertThat(step.getBorderColor(), nullValue());

        step.setBorderColor("red");
        assertThat(step.getBorderColor(), is("red"));
    }

    @Test
    void link() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertThat(step.getLink(), nullValue());

        step.setLink("https://jenkins.io");
        assertThat(step.getLink(), is("https://jenkins.io"));
    }

    @Test
    void deprecated() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertThat(step.getDescriptor().isAdvanced(), is(true));
    }

    @Test
    void addShortText() throws Exception {
        String text = UUID.randomUUID().toString();
        String color = UUID.randomUUID().toString();
        String background = UUID.randomUUID().toString();
        int border = new Random().nextInt();
        String borderColor = UUID.randomUUID().toString();
        String link = "http://" + UUID.randomUUID();

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition(
                "addShortText(text:\"" + text + "\", color:\"" + color + "\", background:\"" + background
                        + "\", border:" + border + ", borderColor:\"" + borderColor + "\", link:\"" + link + "\")",
                true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertThat(badgeActions, hasSize(1));

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertThat(action.getText(), is(text));
        assertThat(action.getIcon(), nullValue());
        assertThat(action.getLink(), is(link));
    }

    @Test
    void jenkinsColorStyle() throws Exception {
        String text = UUID.randomUUID().toString();
        String color = "jenkins-!-color-red";
        int border = 1;

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition(
                "addShortText(text:\"" + text + "\", color:\"" + color + "\", border:\"" + border + "\")", true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertThat(badgeActions, hasSize(1));

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertThat(action.getText(), is(text));
        assertThat(action.getStyle(), is("border: 1px solid ;color: var(---red);"));
    }

    @Test
    void jenkinsWarningStyle() throws Exception {
        String text = UUID.randomUUID().toString();
        String color = "jenkins-!-warning-color";
        int border = 1;

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition(
                "addShortText(text:\"" + text + "\", color:\"" + color + "\", border:\"" + border + "\")", true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertThat(badgeActions, hasSize(1));

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertThat(action.getText(), is(text));
        assertThat(action.getStyle(), is("border: 1px solid ;color: var(--warning-color);"));
    }

    @Test
    void addShortText_minimal() throws Exception {
        String text = UUID.randomUUID().toString();

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("addShortText(text:\"" + text + "\")", true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertThat(badgeActions, hasSize(1));

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertThat(action.getText(), is(text));
        assertThat(action.getIcon(), nullValue());
        assertThat(action.getLink(), nullValue());
    }
}
