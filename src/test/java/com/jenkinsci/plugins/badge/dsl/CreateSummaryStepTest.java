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

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import hudson.markup.RawHtmlMarkupFormatter;
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
class CreateSummaryStepTest {

    private static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void id() {
        CreateSummaryStep step = new CreateSummaryStep(null);
        assertThat(step.getId(), nullValue());

        String id = UUID.randomUUID().toString();
        step.setId(id);
        assertThat(step.getId(), is(id));
    }

    @Test
    void icon() {
        CreateSummaryStep step = new CreateSummaryStep(null);
        assertThat(step.getIcon(), nullValue());

        String icon = UUID.randomUUID().toString();
        step = new CreateSummaryStep(icon);
        assertThat(step.getIcon(), is(icon));
    }

    @Test
    void text() {
        CreateSummaryStep step = new CreateSummaryStep(null);
        assertThat(step.getText(), nullValue());

        String text = UUID.randomUUID().toString();
        step.setText(text);
        assertThat(step.getText(), is(text));
    }

    @Test
    void deprecated() {
        CreateSummaryStep step = new CreateSummaryStep(null);
        assertThat(step.getDescriptor().isAdvanced(), is(true));
    }

    @Test
    void createSummary_plain() throws Exception {
        String text = randomUUID().toString();
        BadgeSummaryAction action = createSummary("summary.appendText('" + text + "')");
        assertThat(action.getText(), is(text));
    }

    @Test
    void createSummary_html_unescaped() throws Exception {
        r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
        String text = randomUUID().toString();
        BadgeSummaryAction action = createSummary("summary.appendText('<ul><li>" + text + "</li></ul>', false)");
        assertThat(action.getText(), is("<ul><li>" + text + "</li></ul>"));
    }

    @Test
    void createSummary_html_unescaped_remove_script() throws Exception {
        r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
        String text = randomUUID().toString();
        String html = "<ul><li>" + text + "</li></ul><script>alert(\"exploit!\");</script>";
        BadgeSummaryAction action = createSummary("summary.appendText('" + html + "', false);");
        assertThat(action.getText(), is("<ul><li>" + text + "</li></ul>"));
    }

    @Test
    void createSummary_html_escaped() throws Exception {
        r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
        String text = randomUUID().toString();
        BadgeSummaryAction action = createSummary("summary.appendText('<ul><li>" + text + "</li></ul>', true)");
        assertThat(action.getText(), is("&lt;ul&gt;&lt;li&gt;" + text + "&lt;/li&gt;&lt;/ul&gt;"));
    }

    @Test
    void createSummary_all() throws Exception {
        r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
        String text = randomUUID().toString();
        BadgeSummaryAction action = createSummary("summary.appendText('" + text + "', false, true, true, 'grey')");
        assertThat(action.getText(), is("<b><i>" + text + "</i></b>"));
    }

    @Test
    void createSummary_with_text() throws Exception {
        String icon = randomUUID().toString();
        String text = randomUUID().toString();

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition(
                "def summary = createSummary(icon:\"" + icon + "\", text:\"" + text + "\")", true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        List<BadgeSummaryAction> summaryActions = b.getActions(BadgeSummaryAction.class);
        assertThat(summaryActions, hasSize(1));

        BadgeSummaryAction action = summaryActions.get(0);
        assertThat(action.getIcon(), endsWith(icon));
        assertThat(action.getText(), is(text));
    }

    private static BadgeSummaryAction createSummary(String script) throws Exception {
        String icon = randomUUID().toString();

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("def summary = createSummary(\"" + icon + "\")\n" + script, true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        List<BadgeSummaryAction> summaryActions = b.getActions(BadgeSummaryAction.class);
        assertThat(summaryActions, hasSize(1));

        BadgeSummaryAction action = summaryActions.get(0);
        assertThat(action.getIcon(), endsWith(icon));
        return action;
    }
}
