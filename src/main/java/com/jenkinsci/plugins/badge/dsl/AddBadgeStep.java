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
import com.jenkinsci.plugins.badge.annotations.OptionalParam;
import com.jenkinsci.plugins.badge.annotations.Param;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Add a badge.
 */
public class AddBadgeStep extends AbstractAddBadgeStep {

  @DataBoundConstructor
  public AddBadgeStep(@Param(name = "icon", description = "The icon for this badge") String icon,
                      @Param(name = "text", description = "The text for this badge") String text) {
    super(icon, text);
  }

  public String getColor() {
    return getBadge().getColor();
  }

  @DataBoundSetter
  @OptionalParam(name = "color", description = "The Jenkins palette/semantic color name of the badge icon symbol")
  public void setColor(String color) {
    getBadge().setColor(color);
  }

  @Override
  public StepExecution start(StepContext context) {
    return new Execution(getBadge(), getId(), context) {

      @Override
      protected BadgeAction newBatchAction(Badge badge) throws IllegalArgumentException {
        return BadgeAction.createBadge(badge.getIcon(), badge.getColor(), badge.getText(), badge.getLink());
      }
    };
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "addBadge";
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "Add Badge";
    }

  }

}
