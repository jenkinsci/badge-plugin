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
import static org.junit.jupiter.api.Assertions.assertAll;

import com.jenkinsci.plugins.badge.action.BadgeAction;
import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import hudson.markup.RawHtmlMarkupFormatter;
import hudson.model.BuildBadgeAction;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
@Deprecated(since = "2.0", forRemoval = true)
class LegacyPipelineTest {

    private static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void color() {
        List<String> colors = Arrays.asList(
                "blue", "brown", "cyan", "green", "indigo", "orange", "pink", "purple", "red", "yellow", "white",
                "black");
        Stream<List<String>> palette =
                colors.stream().map(c -> Arrays.asList("'" + c + "'", "color: var(--" + c + ");"));
        Stream<List<String>> paletteLight =
                colors.stream().map(c -> Arrays.asList("'light-" + c + "'", "color: var(--light-" + c + ");"));
        Stream<List<String>> paletteDark =
                colors.stream().map(c -> Arrays.asList("'dark-" + c + "'", "color: var(--dark-" + c + ");"));

        List<String> semantics = Arrays.asList(
                "accent", "text", "error", "warning", "success", "destructive", "build", "danger", "info");
        Stream<List<String>> semantic =
                semantics.stream().map(c -> Arrays.asList("'" + c + "'", "color: var(--" + c + "-color);"));

        Stream<List<String>> other = Stream.of(
                Arrays.asList("'light-'", "color: light-;"),
                Arrays.asList("'dark-'", "color: dark-;"),
                Arrays.asList("'#ff0000'", "color: #ff0000;"),
                Arrays.asList("'tortoise'", "color: tortoise;"),
                Arrays.asList("'jenkins-!-color-red'", "color: var(--red);"),
                Arrays.asList("'jenkins-!-warning-color'", "color: var(--warning-color);"),
                Arrays.asList("''", "color: ;"),
                Arrays.asList("null", null));

        assertAll("palette", colorTests(palette));
        assertAll("palette-light", colorTests(paletteLight));
        assertAll("palette-dark", colorTests(paletteDark));
        assertAll("semantic", colorTests(semantic));
        assertAll("other", colorTests(other));
    }

    private static Stream<Executable> colorTests(Stream<List<String>> tests) {
        return tests.map(params -> () -> {
            WorkflowRun run = runJob("addBadge(color: " + params.get(0) + ")");

            List<BuildBadgeAction> badgeActions = run.getBadgeActions();
            assertThat(badgeActions, hasSize(1));

            BadgeAction action = (BadgeAction) badgeActions.get(0);
            assertThat(action.getStyle(), is(params.get(1)));
        });
    }

    @Test
    void appendText() throws Exception {
        WorkflowRun run = runJob("createSummary(text: 'Test Text')");

        List<BadgeSummaryAction> actions = run.getActions(BadgeSummaryAction.class);
        assertThat(actions, hasSize(1));

        BadgeSummaryAction action = actions.get(0);
        assertThat(action.getText(), is("Test Text"));

        run = runJob("def summary = createSummary(text: 'Test Text')\n" + "summary.appendText(' More Text', true)");

        actions = run.getActions(BadgeSummaryAction.class);
        assertThat(actions, hasSize(1));

        action = actions.get(0);
        assertThat(action.getText(), is("Test Text More Text"));

        run = runJob("def summary = createSummary(text: 'Test Text')\n" + "summary.appendText(' More Text', false)");

        actions = run.getActions(BadgeSummaryAction.class);
        assertThat(actions, hasSize(1));

        action = actions.get(0);
        assertThat(action.getText(), is("Test Text More Text"));

        run = runJob("""
                        def summary = createSummary(text: 'Test Text')
                        summary.appendText(' More Text', false, false, false, null)
                        """);

        actions = run.getActions(BadgeSummaryAction.class);
        assertThat(actions, hasSize(1));

        action = actions.get(0);
        assertThat(action.getText(), is("Test Text More Text"));

        r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
        run = runJob("""
                        def summary = createSummary(text: 'Test Text')
                        summary.appendText(' More Text', true, true, true, 'red')
                        """);

        actions = run.getActions(BadgeSummaryAction.class);
        assertThat(actions, hasSize(1));

        action = actions.get(0);
        assertThat(action.getText(), is("Test Text<b><i> More Text</i></b>"));
    }

    private static WorkflowRun runJob(String script) throws Exception {
        WorkflowJob project = r.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(script, true));
        return r.assertBuildStatusSuccess(project.scheduleBuild2(0));
    }
}
