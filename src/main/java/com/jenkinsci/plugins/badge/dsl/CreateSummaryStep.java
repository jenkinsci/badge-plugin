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

import org.apache.commons.lang.StringUtils;
import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.inject.Named;

/**
 * Create a summary text.
 */
public class CreateSummaryStep extends Step {

  private final String icon;
  private String text;

  @DataBoundConstructor
  public CreateSummaryStep(@Named("icon") String icon) {
    this.icon = icon;
  }

  public String getIcon() {
    return icon;
  }

  public String getText() {
    return text;
  }

  @DataBoundSetter
  public void setText(String text) {
    this.text = text;
  }

  @Override
  public StepExecution start(StepContext context) {
    return new Execution(icon, text, context);
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "createSummary";
    }

    @Override
    public String getDisplayName() {
      return "Print Message";
    }

  }

  public static class Execution extends SynchronousStepExecution<BadgeSummaryAction> {

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final String icon;
    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final String text;

    Execution(String icon, String text, StepContext context) {
      super(context);
      this.icon = icon;
      this.text = text;
    }

    @Override
    protected BadgeSummaryAction run() throws Exception {
      BadgeSummaryAction action = new BadgeSummaryAction(icon);
      if (StringUtils.isNotBlank(text)) {
        action.appendText(text);
      }
      getContext().get(Run.class).addAction(action);
      return action;
    }

    private static final long serialVersionUID = 1L;

  }

}