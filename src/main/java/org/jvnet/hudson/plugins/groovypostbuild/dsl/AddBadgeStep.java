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

import java.io.Serializable;

import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jvnet.hudson.plugins.groovypostbuild.GroovyPostbuildAction;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Run;

/**
 * Add a badge.
 */
public class AddBadgeStep extends Step {

  private final Badge badge;

  @DataBoundConstructor
  public AddBadgeStep(String icon, String text, String link) {
    this.badge = new Badge(icon, text, link);
  }

  public String getIcon() {
    return badge.getIcon();
  }
  public String getText() {
    return badge.getText();
  }
  public String getLink() {
    return badge.getLink();
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new Execution(badge, context);
  }

  protected Badge getBadge() {
    return badge;
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "addBadge";
    }

    @Override
    public String getDisplayName() {
      return "Add Badge";
    }

  }

  protected static class Badge implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String icon;
    private final String text;
    private final String link;

    private Badge(String icon, String text, String link) {
      this.icon = icon;
      this.text = text;
      this.link = link;
    }

    protected String getIcon() {
      return icon;
    }

    protected String getText() {
      return text;
    }

    protected String getLink() {
      return link;
    }
  }

  public static class Execution extends AbstractSynchronousStepExecution<Void> {

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final Badge badge;

    Execution(Badge badge, StepContext context) {
      super(context);
      this.badge = badge;
    }

    @Override
    protected Void run() throws Exception {
      getContext().get(Run.class).addAction(newBatchAction(badge));
      return null;
    }

    protected GroovyPostbuildAction newBatchAction(Badge badge) {
      return GroovyPostbuildAction.createBadge(badge.getIcon(), badge.getText(), badge.getLink());
    }

    private static final long serialVersionUID = 1L;

  }

}