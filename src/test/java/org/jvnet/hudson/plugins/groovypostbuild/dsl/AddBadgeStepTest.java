/*
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Serban Iordache
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
package org.jvnet.hudson.plugins.groovypostbuild.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildAction;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.model.BuildBadgeAction;

public class AddBadgeStepTest {

  @ClassRule
  public static BuildWatcher buildWatcher = new BuildWatcher();
  @Rule
  public JenkinsRule r = new JenkinsRule();

  @Test
  public void addBadge() throws Exception {
    String icon = UUID.randomUUID().toString();
    String text = UUID.randomUUID().toString();
    String link = UUID.randomUUID().toString();
    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
    p.setDefinition(new CpsFlowDefinition("addBadge(icon:\"" + icon + "\",  text:\"" + text + "\",  link:\"" + link + "\")", true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

    List<BuildBadgeAction> badgeActions = b.getBadgeActions();
    assertEquals(1, badgeActions.size());

    GroovyPostbuildAction action = (GroovyPostbuildAction) badgeActions.get(0);
    assertTrue(action.getIconPath().endsWith(icon));
    assertEquals(text, action.getText());
    assertEquals(link, action.getLink());
  }

  @Test
  public void addInfoBadge() throws Exception {
    addStatusBadge("addInfoBadge", "info.gif");
  }

  @Test
  public void addWarningBadge() throws Exception {
    addStatusBadge("addWarningBadge", "warning.gif");
  }

  @Test
  public void addErrorBadge() throws Exception {
    addStatusBadge("addErrorBadge", "error.gif");
  }

  private void addStatusBadge(String functionName, String expectedIcon) throws Exception {
    String text = UUID.randomUUID().toString();
    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
    p.setDefinition(new CpsFlowDefinition(functionName + "(\"" + text + "\")", true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

    List<BuildBadgeAction> badgeActions = b.getBadgeActions();
    assertEquals(1, badgeActions.size());

    GroovyPostbuildAction action = (GroovyPostbuildAction) badgeActions.get(0);
    assertEquals(text, action.getText());
    assertTrue(action.getIconPath().endsWith(expectedIcon));
  }
}
