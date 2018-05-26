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
package com.jenkinsci.plugins.badge.dsl;

import com.jenkinsci.plugins.badge.action.BadgeAction;
import hudson.model.BuildBadgeAction;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddBadgeStepTest extends AbstractBadgeTest {

  @Test
  public void addBadge() throws Exception {
    addBadge(false);
  }

  @Test
  public void addBadge_in_node() throws Exception {
    addBadge(true);
  }

  private void addBadge(boolean inNode) throws Exception {
    String icon = UUID.randomUUID().toString();
    String text = UUID.randomUUID().toString();
    String link = UUID.randomUUID().toString();
    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");

    String script = "addBadge(icon:\"" + icon + "\",  text:\"" + text + "\",  link:\"" + link + "\")";
    if (inNode) {
      script = "node() {" + script + "}";
    }

    p.setDefinition(new CpsFlowDefinition(script, true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

    List<BuildBadgeAction> badgeActions = b.getBadgeActions();
    assertEquals(1, badgeActions.size());

    BadgeAction action = (BadgeAction) badgeActions.get(0);
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

    BadgeAction action = (BadgeAction) badgeActions.get(0);
    assertEquals(text, action.getText());
    assertTrue(action.getIconPath().endsWith(expectedIcon));
  }
}
