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

import com.jenkinsci.plugins.badge.action.AbstractBadgeAction;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class RemoveBadgesStepTest extends AbstractBadgeTest {

  @Test
  public void removeBadges_by_id() throws Exception {
    removeBadges("addInfoBadge(text: 'a'", "removeBadges(id:'a')", "b");
  }

  @Test
  public void removeBadges_all() throws Exception {
    removeBadges("addInfoBadge(text: 'a'", "removeBadges()");
  }

  @Test
  public void removeHtmlBadges_by_id() throws Exception {
    removeBadges("addHtmlBadge(html: 'a'", "removeHtmlBadges(id:'a')", "b");
  }

  @Test
  public void removeHtmlBadges_all() throws Exception {
    removeBadges("addHtmlBadge(html: 'a'", "removeHtmlBadges()");
  }

  private void removeBadges(String badgeScriptPrefix, String removeScript, String... remainingBadgeIds) throws Exception {
    WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
    String script = badgeScriptPrefix + ", id: 'a')\n" +
        badgeScriptPrefix + ", id: 'b')\n" +
        removeScript;

    p.setDefinition(new CpsFlowDefinition(script, true));
    WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

    List<AbstractBadgeAction> badgeActions = b.getActions(AbstractBadgeAction.class);

    assertEquals(remainingBadgeIds.length, badgeActions.size());

    for (int i = 0; i < remainingBadgeIds.length; i++) {
      assertEquals(remainingBadgeIds[i], badgeActions.get(i).getId());
    }

  }
}
