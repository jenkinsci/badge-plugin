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
 * Create a short text.
 */
public class AddShortTextStep extends Step {

  private final ShortText shortText;

  @DataBoundConstructor
  public AddShortTextStep(String text, String color, String background, Integer border, String borderColor) {
    this.shortText = new ShortText(text, color, background, border, borderColor);
  }


  public String getText() {
    return shortText.getText();
  }

  public String getColor() {
    return shortText.getColor();
  }

  public String getBackground() {
    return shortText.getBackground();
  }

  public Integer getBorder() {
    return shortText.getBorder();
  }

  public String getBorderColor() {
    return shortText.getBorderColor();
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new Execution(shortText, context);
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "addShortText";
    }

    @Override
    public String getDisplayName() {
      return "Add Short Text";
    }

  }

  private static class ShortText implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String text;
    private final String color;
    private final String background;
    private final Integer border;
    private final String borderColor;

    public ShortText(String text, String color, String background, Integer border, String borderColor) {
      this.text = text;
      this.color = color;
      this.background = background;
      this.border = border;
      this.borderColor = borderColor;
    }

    private String getText() {
      return text;
    }

    private String getColor() {
      return color;
    }

    private String getBackground() {
      return background;
    }

    private Integer getBorder() {
      return border;
    }

    private String getBorderColor() {
      return borderColor;
    }

    private String getBorderString() {
      return border == null ? null : border + "px";
    }
  }

  public static class Execution extends AbstractSynchronousStepExecution<Void> {

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final ShortText shortText;

    Execution(ShortText shortText, StepContext context) {
      super(context);
      this.shortText = shortText;
    }

    @Override
    protected Void run() throws Exception {
      getContext().get(Run.class).addAction(GroovyPostbuildAction.createShortText(shortText.getText(),
          shortText.getColor(), shortText.getBackground(), shortText.getBorderString(),shortText.getBorderColor()));
      return null;
    }

    private static final long serialVersionUID = 1L;

  }

}