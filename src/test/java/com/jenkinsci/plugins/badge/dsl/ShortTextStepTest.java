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
import hudson.model.Action;
import hudson.model.BuildBadgeAction;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ShortTextStepTest extends AbstractBadgeTest {

  @Test
  public void addShortText() throws Exception {
    String text = UUID.randomUUID().toString();
    String color = UUID.randomUUID().toString();
    String background = UUID.randomUUID().toString();
    Integer border = new Random().nextInt();
    String borderColor = UUID.randomUUID().toString();
    String link = UUID.randomUUID().toString();

    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
    p.setDefinition(new CpsFlowDefinition("addShortText(text:\"" + text + "\",color:\"" + color + "\", background:\"" + background + "\", border:" + border + ", borderColor:\""
        + borderColor + "\", link:\"" + link + "\")", true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

    List<? extends Action> allActions = b.getAllActions();
    List<BuildBadgeAction> badgeActions = b.getBadgeActions();
    assertEquals(1, badgeActions.size());

    BadgeAction action = (BadgeAction) badgeActions.get(0);
    assertEquals(text, action.getText());
    assertEquals(color, action.getColor());
    assertEquals(background, action.getBackground());
    assertEquals(borderColor, action.getBorderColor());
    assertEquals(border + "px", action.getBorder());
    assertNull(action.getIconPath());
    assertEquals(link, action.getLink());
  }

  @Test
  public void addShortText_minimal() throws Exception {
    String text = UUID.randomUUID().toString();

    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
    p.setDefinition(new CpsFlowDefinition("addShortText(text:\"" + text + "\")", true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

    List<BuildBadgeAction> badgeActions = b.getBadgeActions();
    assertEquals(1, badgeActions.size());

    BadgeAction action = (BadgeAction) badgeActions.get(0);
    assertEquals(text, action.getText());
    assertNull(action.getColor());
    assertNull(action.getBackground());
    assertNull(action.getBorderColor());
    assertNull(action.getBorder());
    assertNull(action.getIconPath());
    assertNull(action.getLink());
  }
}
