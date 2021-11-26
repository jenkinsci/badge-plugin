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
package com.jenkinsci.plugins.badge.action;

import hudson.PluginWrapper;
import hudson.model.Hudson;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ExportedBean(defaultVisibility = 2)
public class BadgeAction extends AbstractBadgeAction {
  private static final Logger LOGGER = Logger.getLogger(BadgeSummaryAction.class.getName());
  private static final long serialVersionUID = 1L;
  private final String iconPath;
  private final String text;
  private String color = "#000000";
  private String background = "#FFFF00";
  private String border = "1px";
  private String borderColor = "#C0C000";
  private String link;

  private BadgeAction(String iconPath, String text) {
    this.iconPath = iconPath;
    this.text = text;
  }

  public static BadgeAction createBadge(String icon, String text) {
    return new BadgeAction(getIconPath(icon), text);
  }

  public static BadgeAction createBadge(String icon, String text, String link) throws IllegalArgumentException {
    BadgeAction action = new BadgeAction(getIconPath(icon), text);
    action.link = link;
    action.validate();
    return action;
  }

  public static BadgeAction createShortText(String text) {
    return new BadgeAction(null, text);
  }


  public static BadgeAction createShortText(String text, String color, String background, String border, String borderColor) {
    return createShortText(text, color, background, border, borderColor, null);
  }

  public static BadgeAction createShortText(String text, String color, String background, String border, String borderColor, String link) {
    BadgeAction action = new BadgeAction(null, text);
    action.color = color;
    action.background = background;
    action.border = border;
    action.borderColor = borderColor;
    action.link = link;
    return action;
  }

  public static BadgeAction createInfoBadge(String text) throws IllegalArgumentException {
    return createInfoBadge(text, null);
  }

  public static BadgeAction createInfoBadge(String text, String link) throws IllegalArgumentException {
    return createBadge("info.gif", text, link);
  }

  public static BadgeAction createWarningBadge(String text) throws IllegalArgumentException {
    return createWarningBadge(text, null);
  }

  public static BadgeAction createWarningBadge(String text, String link) throws IllegalArgumentException {
    return createBadge("warning.gif", text, link);
  }

  public static BadgeAction createErrorBadge(String text) throws IllegalArgumentException {
    return createErrorBadge(text, null);
  }

  public static BadgeAction createErrorBadge(String text, String link) throws IllegalArgumentException {
    return createBadge("error.gif", text, link);
  }

  protected void validate() throws IllegalArgumentException {
    if (BadgePlugin.get().isDisableFormatHTML()) {
      return;
    }

    if (link != null && !link.startsWith("/") && !link.matches("^https?:.*") && !link.matches("^mailto:.*")) {
      throw new IllegalArgumentException("Invalid link '" + link + "'for badge action with text '" + text + "'");
    }
  }

  /* Action methods */
  public String getUrlName() {
    return "";
  }

  public String getDisplayName() {
    return "";
  }

  public String getIconFileName() {
    return null;
  }

  @Exported
  public boolean isTextOnly() {
    return (iconPath == null);
  }

  @Exported
  public String getIconPath() {
    // add the context path to the path variable if the image starts with /
    if (iconPath != null && iconPath.startsWith("/")) {
      StaplerRequest currentRequest = Stapler.getCurrentRequest();
      if (currentRequest != null && !iconPath.startsWith(currentRequest.getContextPath())) {
        return currentRequest.getContextPath() + iconPath;
      }
    }
    return iconPath;
  }

  @Exported
  public String getText() {
    if (BadgePlugin.get().isDisableFormatHTML()) {
      return text;
    }
    try {
      return BadgePlugin.get().translate(text);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error preparing badge text for ui", e);
      return "<b><font color=\"red\">ERROR</font></b>";
    }
  }

  @Exported
  public String getColor() {
    return color;
  }

  @Exported
  public String getBackground() {
    return background;
  }

  @Exported
  public String getBorder() {
    return border;
  }

  @Exported
  public String getBorderColor() {
    return borderColor;
  }

  @Exported
  public String getLink() {
    if (link == null || BadgePlugin.get().isDisableFormatHTML()) {
      return link;
    }

    if (link.startsWith("/") || link.matches("^https?:.*") || link.matches("^mailto:.*")) {
      return link;
    }
    LOGGER.log(Level.WARNING, "Error invalid link value: '" + link + "'");

    return null;
  }

  public static String getIconPath(String icon) {
    if (icon == null) {
      return null;
    }

    if (icon.startsWith("/") || icon.matches("^https?:.*")) {
      return icon;
    }

    Jenkins jenkins = Jenkins.getInstanceOrNull();

    // Try plugin images dir, fallback to Hudson images dir
    PluginWrapper wrapper = jenkins != null ? jenkins.getPluginManager().getPlugin("badge") : null;
    boolean pluginIconExists = (wrapper != null) && new File(wrapper.baseResourceURL.getPath() + "/images/" + icon).exists();
    return pluginIconExists ? "/plugin/" + wrapper.getShortName() + "/images/" + icon : Hudson.RESOURCE_PATH + "/images/16x16/" + icon;
  }
}
