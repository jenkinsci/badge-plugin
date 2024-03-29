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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.Serializable;

/**
 * Create a short text.
 */
public class AddShortTextStep extends Step {

  private final ShortText shortText;

  @DataBoundConstructor
  public AddShortTextStep(@Param(name = "text", description = "The text to add fot this badge") String text) {
    this.shortText = new ShortText(text);
  }

  public String getText() {
    return shortText.getText();
  }

  public String getColor() {
    return shortText.getColor();
  }

  @DataBoundSetter
  @OptionalParam(description = "The color for this short text")
  public void setColor(String color) {
    this.shortText.setColor(color);
  }

  public String getBackground() {
    return shortText.getBackground();
  }

  @DataBoundSetter
  @OptionalParam(description = "The background-color for this short text")
  public void setBackground(String background) {
    this.shortText.setBackground(background);
  }

  public Integer getBorder() {
    return shortText.getBorder();
  }

  @DataBoundSetter
  @OptionalParam(description = "The border width for this short text")
  public void setBorder(Integer border) {
    this.shortText.setBorder(border);
  }

  public String getBorderColor() {
    return shortText.getBorderColor();
  }

  @DataBoundSetter
  @OptionalParam(description = "The order color for this short text")
  public void setBorderColor(String borderColor) {
    this.shortText.setBorderColor(borderColor);
  }

  @DataBoundSetter
  @OptionalParam(description = "The link for this short text")
  public void setLink(String link) {
    this.shortText.setLink(link);
  }

  public String getLink() {
    return this.shortText.getLink();
  }

  @Override
  public StepExecution start(StepContext context) {
    return new Execution(shortText, context);
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "addShortText";
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "Add Short Text";
    }

  }

  private static class ShortText implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String text;
    private String color;
    private String background;
    private Integer border;
    private String borderColor;
    private String link;

    public ShortText(String text) {
      this.text = text;
    }

    private String getText() {
      return text;
    }

    private String getColor() {
      return color;
    }

    public void setColor(String color) {
      this.color = color;
    }

    private String getBackground() {
      return background;
    }

    public void setBackground(String background) {
      this.background = background;
    }

    private Integer getBorder() {
      return border;
    }

    public void setBorder(Integer border) {
      this.border = border;
    }

    private String getBorderColor() {
      return borderColor;
    }

    public void setBorderColor(String borderColor) {
      this.borderColor = borderColor;
    }

    private String getBorderString() {
      return border == null ? null : border + "px";
    }

    public String getLink() {
      return link;
    }

    public void setLink(String link) {
      this.link = link;
    }
  }

  public static class Execution extends SynchronousStepExecution<Void> {

    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
    private transient final ShortText shortText;

    Execution(ShortText shortText, StepContext context) {
      super(context);
      this.shortText = shortText;
    }

    @Override
    protected Void run() throws Exception {
      getContext().get(Run.class).addAction(BadgeAction.createShortText(shortText.getText(),
          shortText.getColor(), shortText.getBackground(), shortText.getBorderString(), shortText.getBorderColor(), shortText.link));
      return null;
    }

    private static final long serialVersionUID = 1L;

  }

}