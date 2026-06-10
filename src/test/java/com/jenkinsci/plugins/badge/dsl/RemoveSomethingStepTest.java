/*
 * The MIT License
 *
 * Copyright (c) 2026, Badge Plugin Authors
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
import static org.hamcrest.Matchers.is;

import com.jenkinsci.plugins.badge.action.AbstractBadgeAction;
import com.jenkinsci.plugins.badge.action.BadgeAction;
import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import java.util.List;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Make sure that the removeBadges step removes only badges and
 * does not remove summaries, and vice versa for removeSummaries.
 */
@WithJenkins
public class RemoveSomethingStepTest {
    @Test
    public void shouldRemoveOnlyBadges(JenkinsRule jenkins) throws Exception {
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "job");
        job.setDefinition(new CpsFlowDefinition("""
            def idB = addBadge(id: 'b', text: 'Test Badge')
            def idS = addSummary(id: 's', text: 'Test Summary')
            removeBadges()
            try {
                echo "Badge object: ${idB}"
                echo "Badge ID (query): ${idB.getId()}"
                echo "Badge text: ${idB.getText()}"
            } catch (Exception e) {
                echo "Error probing Badge (expected): ${e}"
            }
            try {
                echo "Summary object: ${idS}"
                echo "Summary ID (query): ${idS.getId()}"
                echo "Summary text: ${idS.getText()}"
            } catch (Exception e) {
                echo "Error probing Summary (unexpected): ${e}"
            }
        """, true));

        WorkflowRun run = job.scheduleBuild2(0).get();
        jenkins.assertBuildStatusSuccess(run);

        // jenkins.assertLogContains("Error probing Badge (expected)", run);

        jenkins.assertLogContains("Summary ID (query): s", run);
        jenkins.assertLogContains("Summary text: Test Summary", run);
        jenkins.assertLogNotContains("Error probing Summary (unexpected)", run);

        List<AbstractBadgeAction> abstractBadgeActions = run.getActions(AbstractBadgeAction.class);
        assertThat(abstractBadgeActions.size(), is(1));

        List<BadgeAction> badgeActions = run.getActions(BadgeAction.class);
        assertThat(badgeActions.size(), is(0));

        List<BadgeSummaryAction> summaryActions = run.getActions(BadgeSummaryAction.class);
        assertThat(summaryActions.size(), is(1));
    }

    @Test
    public void shouldRemoveOnlySummaries(JenkinsRule jenkins) throws Exception {
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "job");
        job.setDefinition(new CpsFlowDefinition("""
            def idB = addBadge(id: 'b', text: 'Test Badge')
            def idS = addSummary(id: 's', text: 'Test Summary')
            removeSummaries()
            try {
                echo "Badge object: ${idB}"
                echo "Badge ID (query): ${idB.getId()}"
                echo "Badge text: ${idB.getText()}"
            } catch (Exception e) {
                echo "Error probing Badge (unexpected): ${e}"
            }
            try {
                echo "Summary object: ${idS}"
                echo "Summary ID (query): ${idS.getId()}"
                echo "Summary text: ${idS.getText()}"
            } catch (Exception e) {
                echo "Error probing Summary (expected): ${e}"
            }
        """, true));

        WorkflowRun run = job.scheduleBuild2(0).get();
        jenkins.assertBuildStatusSuccess(run);

        // jenkins.assertLogContains("Error probing Summary (expected)", run);

        jenkins.assertLogContains("Badge ID (query): b", run);
        jenkins.assertLogContains("Badge text: Test Badge", run);
        jenkins.assertLogNotContains("Error probing Badge (unexpected)", run);

        List<AbstractBadgeAction> abstractBadgeActions = run.getActions(AbstractBadgeAction.class);
        assertThat(abstractBadgeActions.size(), is(1));

        List<BadgeAction> badgeActions = run.getActions(BadgeAction.class);
        assertThat(badgeActions.size(), is(1));

        List<BadgeSummaryAction> summaryActions = run.getActions(BadgeSummaryAction.class);
        assertThat(summaryActions.size(), is(0));
    }
}
