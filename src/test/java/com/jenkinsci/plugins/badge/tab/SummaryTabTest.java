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
package com.jenkinsci.plugins.badge.tab;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import com.jenkinsci.plugins.badge.dsl.AddSummaryStep;
import java.util.UUID;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class SummaryTabTest {

    protected static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void defaultConstructor() throws Exception {
        assertThrows(NullPointerException.class, () -> new SummaryTab(null));

        WorkflowRun run = runJob();
        assertDoesNotThrow(() -> new SummaryTab(run));
    }

    @Test
    void actions() throws Exception {
        WorkflowRun run = runJob();
        SummaryTab tab = new SummaryTab(run);
        assertThat(tab.getActions(), hasSize(1));
        assertThat(tab.getActions(), hasItem(run.getAction(BadgeSummaryAction.class)));
    }

    @Test
    void iconFileName() throws Exception {
        WorkflowRun run = runJob();
        SummaryTab tab = new SummaryTab(run);
        assertThat(tab.getIconFileName(), is("symbol-list"));
    }

    @Test
    void displayName() throws Exception {
        WorkflowRun run = runJob();
        SummaryTab tab = new SummaryTab(run);
        assertThat(tab.getDisplayName(), is("Summary"));
    }

    @Test
    void urlName() throws Exception {
        WorkflowRun run = runJob();
        SummaryTab tab = new SummaryTab(run);
        assertThat(tab.getUrlName(), is("summary"));
    }

    private WorkflowRun runJob() throws Exception {
        WorkflowJob project = r.createProject(WorkflowJob.class);

        AddSummaryStep step = new AddSummaryStep();
        step.setId(UUID.randomUUID().toString());
        step.setIcon("symbol-rocket plugin-ionicons-api");
        step.setText("Test Text");
        step.setCssClass("icon-md");
        step.setStyle("color: green");
        step.setLink("https://jenkins.io");
        step.setTarget("_blank");
        String script = """
                            pipeline {
                                agent any
                                stages {
                                    stage('Testing') {
                                        steps {
                                            %s
                                        }
                                    }
                                }
                            }
                            """.formatted(step.toString());

        project.setDefinition(new CpsFlowDefinition(script, true));
        return r.assertBuildStatusSuccess(project.scheduleBuild2(0));
    }
}
