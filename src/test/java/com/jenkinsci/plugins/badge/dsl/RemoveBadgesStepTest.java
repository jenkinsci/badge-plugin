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
class RemoveBadgesStepTest extends AbstractRemoveBadgeStepTest {

    @Test
    void removeById(JenkinsRule r) throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(badgeId);
        runRemoveJob(r, addStep, removeStep, 0);
    }

    @Test
    void removeAll(JenkinsRule r) throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(null);
        runRemoveJob(r, addStep, removeStep, 0);
    }

    @Test
    void removeInvalidId(JenkinsRule r) throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(UUID.randomUUID().toString());
        runRemoveJob(r, addStep, removeStep, 1);
    }

    protected void runRemoveJob(
            JenkinsRule r, AbstractAddBadgeStep addStep, AbstractRemoveBadgesStep removeStep, int expected)
            throws Exception {
        WorkflowJob project = r.jenkins.createProject(WorkflowJob.class, "project");

        String script = addStep.toString() + "\n";
        script += removeStep.toString();

        project.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun run = r.assertBuildStatusSuccess(project.scheduleBuild2(0));

        assertActionExists(run, expected);
    }

    protected void assertActionExists(WorkflowRun run, int expected) {
        List<BuildBadgeAction> badgeActions = run.getBadgeActions();
        assertEquals(expected, badgeActions.size());
    }

    protected AbstractAddBadgeStep createAddStep(String id) {
        return new AddBadgeStep(
                id, "symbol-rocket plugin-ionicons-api", "Test Text", "icon-md", "color: green", "https://jenkins.io");
    }

    @Override
    protected AbstractRemoveBadgesStep createRemoveStep(String id) {
        return new RemoveBadgesStep(id);
    }
}
