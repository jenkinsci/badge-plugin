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
import hudson.model.Result;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

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
    String link = "https://" + UUID.randomUUID();
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
    addStatusBadge("addInfoBadge", "info.gif", false);
    addStatusBadge("addInfoBadge", "info.gif", true);
  }

  @Test
  public void addWarningBadge() throws Exception {
    addStatusBadge("addWarningBadge", "warning.gif", false);
    addStatusBadge("addWarningBadge", "warning.gif", true);
  }

  @Test
  public void addErrorBadge() throws Exception {
    addStatusBadge("addErrorBadge", "error.gif", false);
    addStatusBadge("addErrorBadge", "error.gif", true);
  }

  private void addStatusBadge(String functionName, String expectedIcon, boolean withLink) throws Exception {
    String text = UUID.randomUUID().toString();
    String link = "mailto://" + UUID.randomUUID();

    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, text);
    String script = functionName + "(text:\"" + text + "\"";
    if (withLink) {
      script += ",  link:\"" + link + "\"";
    }
    script += ")";

    p.setDefinition(new CpsFlowDefinition(script, true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

    List<BuildBadgeAction> badgeActions = b.getBadgeActions();
    assertEquals(1, badgeActions.size());

    BadgeAction action = (BadgeAction) badgeActions.get(0);
    assertEquals(text, action.getText());
    assertTrue(action.getIconPath().endsWith(expectedIcon));
    if (withLink) {
      assertEquals(link, action.getLink());
    } else {
      assertNull(action.getLink());
    }
  }

  @Test
  public void addBadge_invalid_link() throws Exception {
    String icon = UUID.randomUUID().toString();
    String text = UUID.randomUUID().toString();
    String link = "javascript:" + UUID.randomUUID();
    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");

    String script = "addBadge(icon:\"" + icon + "\",  text:\"" + text + "\",  link:\"" + link + "\")";
    p.setDefinition(new CpsFlowDefinition(script, true));
    r.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0));
  }

  @Test
  public void addBadge_invalid_text() throws Exception {
    String icon = UUID.randomUUID().toString();
    String textPrefix = UUID.randomUUID().toString();
    String text = textPrefix + "');alert('foo";
    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");

    String script = "addBadge(icon:\"" + icon + "\",  text:\"" + text + "\")";
    p.setDefinition(new CpsFlowDefinition(script, true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

    List<BuildBadgeAction> badgeActions = b.getBadgeActions();
    assertEquals(1, badgeActions.size());

    BadgeAction action = (BadgeAction) badgeActions.get(0);
    assertTrue(action.getIconPath().endsWith(icon));
    assertEquals(textPrefix + "&#39;);alert(&#39;foo", action.getText());
  }
}
