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
package com.jenkinsci.plugins.badge;

import com.jenkinsci.plugins.badge.action.BadgeAction;
import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

@Extension
public class BadgePlugin extends GlobalConfiguration {

  /** @return the singleton instance */
  public static BadgePlugin get() {
    return GlobalConfiguration.all().get(BadgePlugin.class);
  }

  /** Default short text color */
  public static final String SHORT_TEXT_DEFAULT_COLOR = "#000000";
  /** Default short text background color */
  public static final String SHORT_TEXT_DEFAULT_BACKGROUND= "#FFFF00";
  /** Default short border */
  public static final String SHORT_TEXT_DEFAULT_BORDER = "1px";
  /** Default short border color */
  public static final String SHORT_TEXT_DEFAULT_BORDER_COLOR = "#C0C000";

  private boolean disableFormatHTML;
  private String shortTextDefaultColor = SHORT_TEXT_DEFAULT_COLOR;
  private String shortTextDefaultBackgroundColor = SHORT_TEXT_DEFAULT_BACKGROUND;
  private String shortTextDefaultBorder = SHORT_TEXT_DEFAULT_BORDER;
  private String shortTextDefaultBorderColor = SHORT_TEXT_DEFAULT_BORDER_COLOR;



  public BadgePlugin() {
    // When Jenkins is restarted, load any saved configuration from disk.
    load();
  }

  /** @return the whether HTML formatting is disabled or not */
  public boolean isDisableFormatHTML() {
    return disableFormatHTML;
  }

  /**
   * Together with {@link #isDisableFormatHTML}, binds to entry in {@code config.jelly}.
   * @param disableFormatHTML the new value of this field
   */
  @DataBoundSetter
  public void setDisableFormatHTML(boolean disableFormatHTML) {
    this.disableFormatHTML = disableFormatHTML;
    save();
  }

  /** @return the default color for short text badges */
  public String getShortTextDefaultColor() {
    return shortTextDefaultColor;
  }

  /**
   * Together with {@link #getShortTextDefaultColor}, binds to entry in {@code config.jelly}.
   * @param shortTextDefaultColor the new value of this field
   */
  @DataBoundSetter
  public void setShortTextDefaultColor(String shortTextDefaultColor) {
    if (isNullOrEmpty(shortTextDefaultColor)) {
      this.shortTextDefaultColor = SHORT_TEXT_DEFAULT_BACKGROUND;
    } else {
      this.shortTextDefaultColor = shortTextDefaultColor;
    }
    this.shortTextDefaultColor = shortTextDefaultColor;
    save();
  }

  /** @return the default background color for short text badges */
  public String getShortTextDefaultBackgroundColor() {
    return shortTextDefaultBackgroundColor;
  }

  /**
   * Together with {@link #getShortTextDefaultBackgroundColor}, binds to entry in {@code config.jelly}.
   * @param shortTextDefaultBackgroundColor the new value of this field
   */
  @DataBoundSetter
  public void setShortTextDefaultBackgroundColor(String shortTextDefaultBackgroundColor) {
    if (isNullOrEmpty(shortTextDefaultBackgroundColor)) {
      this.shortTextDefaultBackgroundColor = SHORT_TEXT_DEFAULT_BACKGROUND;
    } else {
      this.shortTextDefaultBackgroundColor = shortTextDefaultBackgroundColor;
    }
    save();
  }

  /** @return the default border for short text badges */
  public String getShortTextDefaultBorder() {
    return shortTextDefaultBorder;
  }

  /**
   * Together with {@link #getShortTextDefaultBorder}, binds to entry in {@code config.jelly}.
   * @param shortTextDefaultBorder the new value of this field
   */
  @DataBoundSetter
  public void setShortTextDefaultBorder(String shortTextDefaultBorder) {
    if (isNullOrEmpty(shortTextDefaultBorder)) {
      this.shortTextDefaultBorder = SHORT_TEXT_DEFAULT_BORDER;
    } else {
      this.shortTextDefaultBorder = shortTextDefaultBorder;
    }
    save();
  }

  /** @return the default border color for short text badges */
  public String getShortTextDefaultBorderColor() {
    return shortTextDefaultBorderColor;
  }

  /**
   * Together with {@link #getShortTextDefaultColor}, binds to entry in {@code config.jelly}.
   * @param shortTextDefaultBorderColor the new value of this field
   */
  @DataBoundSetter
  public void setShortTextDefaultBorderColor(String shortTextDefaultBorderColor) {
    if (isNullOrEmpty(shortTextDefaultBorderColor)) {
      this.shortTextDefaultBorderColor = SHORT_TEXT_DEFAULT_BORDER_COLOR;
    } else {
      this.shortTextDefaultBorderColor = shortTextDefaultBorderColor;
    }
    save();
  }


  public void doRemoveBadges(StaplerRequest req, StaplerResponse rsp) throws IOException {
    removeActions(BadgeAction.class, req, rsp);
  }

  public void doRemoveSummaries(StaplerRequest req, StaplerResponse rsp) throws IOException {
    removeActions(BadgeSummaryAction.class, req, rsp);
  }

  @SuppressWarnings("unchecked")
  private void removeActions(Class type, StaplerRequest req, StaplerResponse rsp) throws IOException {
    req.findAncestorObject(Job.class).checkPermission(Run.UPDATE);
    Run run = req.findAncestorObject(Run.class);
    if (run != null) {
      List<? extends Action> actions = run.getAllActions();
      List<Action> groovyActions = run.getActions(type);
      for (Action action : groovyActions) {
        actions.remove(action);
      }
      run.save();
      rsp.sendRedirect(req.getRequestURI().substring(0, req.getRequestURI().indexOf("parent/parent")));
    }
  }

  public void doRemoveBadge(StaplerRequest req, StaplerResponse rsp) throws IOException {
    removeAction(BadgeAction.class, req, rsp);
  }

  public void doRemoveSummary(StaplerRequest req, StaplerResponse rsp) throws IOException {
    removeAction(BadgeSummaryAction.class, req, rsp);
  }

  @SuppressWarnings("unchecked")
  private void removeAction(Class type, StaplerRequest req, StaplerResponse rsp) throws IOException {
    String index = req.getParameter("index");
    if (index == null) {
      throw new IOException("Missing parameter 'index'.");
    }
    int idx;
    try {
      idx = Integer.parseInt(index);
    } catch (NumberFormatException e) {
      throw new IOException("Invalid index: " + index);
    }
    req.findAncestorObject(Job.class).checkPermission(Run.UPDATE);
    Run run = req.findAncestorObject(Run.class);
    if (run != null) {
      List<? extends Action> actions = run.getAllActions();
      List<? extends Action> groovyActions = run.getActions(type);
      if (idx < 0 || idx >= groovyActions.size()) {
        throw new IOException("Index out of range: " + idx);
      }
      actions.remove(groovyActions.get(idx));
      run.save();
      rsp.sendRedirect(req.getRequestURI().substring(0, req.getRequestURI().indexOf("parent/parent")));
    }
  }
}
