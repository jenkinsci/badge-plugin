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

import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Test;

import java.util.List;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateSummaryStepTest extends AbstractBadgeTest {

  @Test
  public void createSummary_plain() throws Exception {
    String text = randomUUID().toString();
    BadgeSummaryAction action = createSummary("summary.appendText('" + text + "')");
    assertEquals(text, action.getText());
  }

  @Test
  public void createSummary_html_unescaped() throws Exception {
    String text = randomUUID().toString();
    BadgeSummaryAction action = createSummary("summary.appendText('<li>" + text + "</li>', false)");
    assertEquals("<li>" + text + "</li>", action.getText());
  }

  @Test
  public void createSummary_html_unescaped_remove_script() throws Exception {
    String text = randomUUID().toString();
    String html = "<li>" + text + "</li><script>alert(\"exploit!\");</script>";
    BadgeSummaryAction action = createSummary("summary.appendText('" + html + "', false);");
    assertEquals("<li>" + text + "</li>", action.getText());
    assertEquals(html, action.getRawText());
  }

  @Test
  public void createSummary_html_escaped() throws Exception {
    String text = randomUUID().toString();
    BadgeSummaryAction action = createSummary("summary.appendText('<li>" + text + "</li>', true)");
    assertEquals("&lt;li&gt;" + text + "&lt;/li&gt;", action.getText());
  }

  @Test
  public void createSummary_all() throws Exception {
    String text = randomUUID().toString();
    BadgeSummaryAction action = createSummary("summary.appendText('" + text + "', false, true, true, 'grey')");
    assertEquals("<b><i><font color=\"grey\">" + text + "</font></i></b>", action.getText());
  }

  private BadgeSummaryAction createSummary(String script) throws Exception {
    String icon = randomUUID().toString();

    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
    p.setDefinition(new CpsFlowDefinition("def summary = createSummary(\"" + icon + "\")\n" + script, true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
    List<BadgeSummaryAction> summaryActions = b.getActions(BadgeSummaryAction.class);
    assertEquals(1, summaryActions.size());

    BadgeSummaryAction action = summaryActions.get(0);
    assertTrue(action.getIconPath().endsWith(icon));
    return action;
  }

  @Test
  public void createSummary_with_text() throws Exception {
    String icon = randomUUID().toString();
    String text = randomUUID().toString();

    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
    p.setDefinition(new CpsFlowDefinition("def summary = createSummary(icon:\"" + icon + "\", text:\"" + text + "\")", true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
    List<BadgeSummaryAction> summaryActions = b.getActions(BadgeSummaryAction.class);
    assertEquals(1, summaryActions.size());

    BadgeSummaryAction action = summaryActions.get(0);
    assertTrue(action.getIconPath().endsWith(icon));
    assertEquals(text, action.getText());
  }
}