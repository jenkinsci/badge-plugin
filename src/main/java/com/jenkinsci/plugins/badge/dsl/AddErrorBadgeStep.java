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
import com.jenkinsci.plugins.badge.annotations.Param;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Add an error badge.
 */
public class AddErrorBadgeStep extends AddBadgeStep {

  @DataBoundConstructor
  public AddErrorBadgeStep(@Param(name = "text", description = "The text for this error badge") String text) {
    super(null, text);
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "addErrorBadge";
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "Add Error Badge";
    }
  }

  @Override
  public StepExecution start(StepContext context) {
    return new Execution(getBadge(), getId(), context) {

      @Override
      protected BadgeAction newBatchAction(Badge badge) {
        return BadgeAction.createErrorBadge(badge.getText(), badge.getLink());
      }
    };
  }

}