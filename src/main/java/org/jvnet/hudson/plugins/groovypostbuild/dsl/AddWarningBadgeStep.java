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

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildAction;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Add an error badge.
 */
public class AddWarningBadgeStep extends AddBadgeStep {

  @DataBoundConstructor
  public AddWarningBadgeStep(String text) {
    super(null, text, null);
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "addWarningBadge";
    }

    @Override
    public String getDisplayName() {
      return "Add Warning Badge";
    }
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new Execution(getBadge(), context) {

      @Override
      protected GroovyPostbuildAction newBatchAction(Badge badge) {
        return GroovyPostbuildAction.createWarningBadge(badge.getText());
      }
    };
  }

}