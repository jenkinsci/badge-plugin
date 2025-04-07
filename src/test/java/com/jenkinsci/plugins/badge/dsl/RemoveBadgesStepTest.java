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

import com.jenkinsci.plugins.badge.action.AbstractBadgeAction;
import java.util.List;
import java.util.UUID;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class RemoveBadgesStepTest extends AbstractRemoveBadgesStepTest {

    @Override
    @Test
    void defaultConstructor() {
        AbstractRemoveBadgesStep step = new RemoveBadgesStep();
        assertNull(step.getId());
    }

    @Test
    void removeByIdInScriptedPipeline() throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(badgeId);
        runRemoveJob(addStep, removeStep, 0, false, false);
    }

    @Test
    void removeByIdInScriptedPipelineInNode() throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(badgeId);
        runRemoveJob(addStep, removeStep, 0, true, false);
    }

    @Test
    void removeByIdInDeclarativePipeline() throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(badgeId);
        runRemoveJob(addStep, removeStep, 0, false, true);
    }

    @Test
    void removeAllInScriptedPipeline() throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(null);
        runRemoveJob(addStep, removeStep, 0, false, false);
    }

    @Test
    void removeAllInScriptedPipelineInNode() throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(null);
        runRemoveJob(addStep, removeStep, 0, true, false);
    }

    @Test
    void removeAllInDeclarativePipeline() throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(null);
        runRemoveJob(addStep, removeStep, 0, false, true);
    }

    @Test
    void removeInvalidIdInScriptedPipeline() throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(UUID.randomUUID().toString());
        runRemoveJob(addStep, removeStep, 1, false, false);
    }

    @Test
    void removeInvalidIdInScriptedPipelineInNode() throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(UUID.randomUUID().toString());
        runRemoveJob(addStep, removeStep, 1, true, false);
    }

    @Test
    void removeInvalidIdInDeclarativePipeline() throws Exception {
        String badgeId = UUID.randomUUID().toString();
        AbstractAddBadgeStep addStep = createAddStep(badgeId);
        AbstractRemoveBadgesStep removeStep = createRemoveStep(UUID.randomUUID().toString());
        runRemoveJob(addStep, removeStep, 1, false, true);
    }

    protected void runRemoveJob(
            AbstractAddBadgeStep addStep,
            AbstractRemoveBadgesStep removeStep,
            int expected,
            boolean inNode,
            boolean declarativePipeline)
            throws Exception {
        WorkflowJob project = r.createProject(WorkflowJob.class);

        String script = addStep.toString() + "\n";
        script += removeStep.toString();

        if (inNode) {
            script = "node() { " + script + " }";
        }

        if (declarativePipeline) {
            script =
                    """
            pipeline {
                agent any
                stages {
                    stage('Testing') {
                        steps {
                            script {
                                %s
                            }
                        }
                    }
                }
            }"""
                            .formatted(script);
        }

        project.setDefinition(new CpsFlowDefinition(script, true));
        WorkflowRun run = r.assertBuildStatusSuccess(project.scheduleBuild2(0));

        assertActionExists(run, expected);
    }

    protected void assertActionExists(WorkflowRun run, int expected) {
        List<AbstractBadgeAction> badgeActions = run.getActions(AbstractBadgeAction.class);
        assertEquals(expected, badgeActions.size());
    }

    protected AbstractAddBadgeStep createAddStep(String id) {
        return new AddBadgeStep(
                id,
                "symbol-rocket plugin-ionicons-api",
                "Test Text",
                "icon-md",
                "color: green",
                "https://jenkins.io",
                "_blank");
    }

    @Override
    protected AbstractRemoveBadgesStep createRemoveStep(String id) {
        return new RemoveBadgesStep(id);
    }
}
