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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import sun.security.krb5.internal.crypto.Des;

import java.io.Serializable;

/**
 * Add a badge.
 */
public class AddBadgeStep extends AbstractStep {

  private final Badge badge;

  @DataBoundConstructor
  public AddBadgeStep(@Param(name = "icon", description = "The icon for this badge") String icon,
                      @Param(name = "text", description = "The text for this badge") String text) {
    this.badge = new Badge(icon, text);
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

  @DataBoundSetter
  @OptionalParam(description = "The link to be added to this badge")
  public void setLink(String link) {
    badge.setLink(link);
  }

  @Override
  public StepExecution start(StepContext context) {
    return new Execution(badge, getId(), context);
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
    private String link;

    private Badge(String icon, String text) {
      this.icon = icon;
      this.text = text;
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

    public void setLink(String link) {
      this.link = link;
    }
  }

  public static class Execution extends SynchronousStepExecution<Void> {

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final Badge badge;
    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final String id;

    Execution(Badge badge, String id, StepContext context) {
      super(context);
      this.badge = badge;
      this.id = id;
    }

    @Override
    protected Void run() throws Exception {
      BadgeAction action = newBatchAction(badge);
      action.setId(id);
      getContext().get(Run.class).addAction(action);
      return null;
    }

    protected BadgeAction newBatchAction(Badge badge) {
      return BadgeAction.createBadge(badge.getIcon(), badge.getText(), badge.getLink());
    }

    private static final long serialVersionUID = 1L;

  }

}