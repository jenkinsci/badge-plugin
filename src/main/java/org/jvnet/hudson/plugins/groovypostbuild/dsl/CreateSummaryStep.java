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

import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildSummaryAction;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Run;

/**
 * Create a summary text.
 */
public class CreateSummaryStep extends Step {

  private final String icon;

  @DataBoundConstructor
  public CreateSummaryStep(String icon) {
    this.icon = icon;
  }

  public String getIcon() {
    return icon;
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new Execution(icon, context);
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

  public static class Execution extends AbstractSynchronousStepExecution<GroovyPostbuildSummaryAction> {

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final String icon;

    Execution(String icon, StepContext context) {
      super(context);
      this.icon = icon;
    }

    @Override
    protected GroovyPostbuildSummaryAction run() throws Exception {
      GroovyPostbuildSummaryAction action = new GroovyPostbuildSummaryAction(icon);
      getContext().get(Run.class).addAction(action);
      return action;
    }

    private static final long serialVersionUID = 1L;

  }

}