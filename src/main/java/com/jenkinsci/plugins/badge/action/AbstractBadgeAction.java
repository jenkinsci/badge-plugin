/*
 * The MIT License
 *
 * Copyright (c) 2024, Badge Plugin Authors
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

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.PluginWrapper;
import hudson.model.Action;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;

/**
 * An abstract action providing an id amongst other fields to build a badge.
 * Most of the implementation resides in this class in order to be shared with badges and summaries.
 */
public abstract class AbstractBadgeAction implements Action, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(AbstractBadgeAction.class.getName());

    private final String id;
    private String icon;
    private String text;
    private String cssClass;
    private String style;
    private String link;

    /**
     * Ctor.
     * @param id the id for a badge. if null, a random uuid will be generated.
     * @param icon the icon for a badge.
     * @param text the text for a badge.
     * @param cssClass the css class for a badge.
     * @param style the css style for a badge.
     * @param link the link for a badge.
     */
    protected AbstractBadgeAction(String id, String icon, String text, String cssClass, String style, String link) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.icon = icon;
        this.text = text;
        this.cssClass = cssClass;
        this.style = style;
        this.link = link;
    }

    @Whitelisted
    public @NonNull String getId() {
        return id;
    }

    @Whitelisted
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Whitelisted
    public String getIcon() {
        if (StringUtils.isEmpty(icon)
                || icon.startsWith("/")
                || icon.startsWith("symbol-")
                || icon.startsWith("icon-")
                || icon.matches("^https?://.*")) {
            return icon;
        }

        // try plugin images dir, fallback to Jenkins images dir
        PluginWrapper wrapper = Jenkins.get().getPluginManager().getPlugin("badge");
        boolean pluginIconExists =
                (wrapper != null) && new File(wrapper.baseResourceURL.getPath() + "/images/" + icon).exists();
        return pluginIconExists
                ? "/plugin/" + wrapper.getShortName() + "/images/" + icon
                : Jenkins.RESOURCE_PATH + "/images/16x16/" + icon;
    }

    @Whitelisted
    public void setText(String text) {
        this.text = text;
    }

    @Whitelisted
    public String getText() {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        try {
            return Jenkins.get().getMarkupFormatter().translate(text);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error preparing badge text for UI", ex);
            return "<b><font color=\"var(--error-color)\">Error preparing badge text for UI</font></b>";
        }
    }

    @Whitelisted
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @Whitelisted
    public String getCssClass() {
        return cssClass;
    }

    @Whitelisted
    public void setStyle(String style) {
        this.style = style;
    }

    @Whitelisted
    public String getStyle() {
        return style;
    }

    @Whitelisted
    public void setLink(String link) {
        this.link = link;
    }

    @Whitelisted
    public String getLink() {
        if (StringUtils.isEmpty(link)
                || link.startsWith("/")
                || link.matches("^https?://.*")
                || link.matches("^mailto:.*")) {
            return link;
        }

        LOGGER.log(Level.WARNING, "Invalid link value: '{}' - ignoring it", link);
        return null;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return "";
    }

    // LEGACY CODE
    @Deprecated(since = "2.0", forRemoval = true)
    private transient String iconPath;

    @Deprecated(since = "2.0", forRemoval = true)
    private transient String color;

    @Deprecated(since = "2.0", forRemoval = true)
    private transient String background;

    @Deprecated(since = "2.0", forRemoval = true)
    private transient String border;

    @Deprecated(since = "2.0", forRemoval = true)
    private transient String borderColor;

    /**
     * @deprecated kept for backwards compatibility.
     * Translates pre 2.0 build.xml to latest format for backwards compatibility.
     * @return this instance
     */
    @Deprecated(since = "2.0", forRemoval = true)
    protected Object readResolve() {
        // field renamed - see AbstractBadgeAction
        if (iconPath != null) {
            setIcon(iconPath);
        }

        // field reworked - see AddShortTextStep
        String style = "";
        if (border != null) {
            style += "border: " + border + " solid " + (borderColor != null ? borderColor : "") + ";";
        }
        if (background != null) {
            style += "background: " + background + ";";
        }
        if (color != null) {
            if (color.startsWith("jenkins-!-color")) {
                style += "color: var(--" + color.replaceFirst("jenkins-!-color-", "") + ");";
            } else if (color.startsWith("jenkins-!-")) {
                style += "color: var(--" + color.replaceFirst("jenkins-!-", "") + ");";
            } else {
                style += "color: " + color + ";";
            }
        }
        if (!style.isEmpty()) {
            setStyle(style);
        }

        return this;
    }
}
