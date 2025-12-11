/*
 * The MIT License
 *
 * Copyright (c) 2025, Badge Plugin Authors
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
import hudson.model.Action;
import io.jenkins.plugins.emoji.symbols.Emojis;
import io.jenkins.plugins.ionicons.Ionicons;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * An abstract action providing an id amongst other fields to build a badge.
 * Most of the implementation resides in this class in order to be shared with badges and summaries.
 */
@ExportedBean(defaultVisibility = 2)
public abstract class AbstractBadgeAction implements Action, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(AbstractBadgeAction.class.getName());

    private final String id;
    private String icon;
    private String text;
    private String cssClass;
    private String style;
    private String link;
    private String target;

    /**
     * Ctor.
     * @param id the id for a badge. if null, a random uuid will be generated.
     * @param icon the icon for a badge.
     * @param text the text for a badge.
     * @param cssClass the css class for a badge.
     * @param style the css style for a badge.
     * @param link the link for a badge.
     *
     * @deprecated Use {@link AbstractBadgeAction#AbstractBadgeAction(String, String, String, String, String, String, String)} instead.
     */
    @Deprecated(since = "2.8")
    protected AbstractBadgeAction(String id, String icon, String text, String cssClass, String style, String link) {
        this(id, icon, text, cssClass, style, link, null);
    }

    /**
     * Ctor.
     * @param id the id for a badge. if null, a random uuid will be generated.
     * @param icon the icon for a badge.
     * @param text the text for a badge.
     * @param cssClass the css class for a badge.
     * @param style the css style for a badge.
     * @param link the link for a badge.
     * @param target the link target for a badge.
     */
    protected AbstractBadgeAction(
            String id, String icon, String text, String cssClass, String style, String link, String target) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.icon = icon;
        this.text = text;
        this.cssClass = cssClass;
        this.style = style;
        this.link = link;
        this.target = target;
    }

    @Exported
    @Whitelisted
    public @NonNull String getId() {
        return id;
    }

    @Whitelisted
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Exported
    @Whitelisted
    public String getIcon() {
        if (icon == null
                || icon.isBlank()
                || icon.startsWith("/")
                || icon.startsWith("symbol-")
                || icon.startsWith("icon-")
                || icon.matches("^https?://.*")) {
            return icon;
        }

        // backwards compatible replacement for old GIFs
        return switch (icon) {
            case "completed.gif" -> "symbol-status-blue";
            case "db_in.gif" -> Ionicons.getIconClassName("cloud-upload-outline");
            case "db_out.gif" -> Ionicons.getIconClassName("cloud-download-outline");
            case "delete.gif" -> "symbol-trash";
            case "error.gif" -> "symbol-status-red";
            case "folder.gif" -> "symbol-folder";
            case "green.gif" -> Emojis.getIconClassName("green_square");
            case "info.gif" -> "symbol-information-circle";
            case "red.gif" -> Emojis.getIconClassName("red_square");
            case "save.gif" -> Ionicons.getIconClassName("save-outline");
            case "success.gif" -> "symbol-status-blue";
            case "text.gif" -> "symbol-document-text";
            case "warning.gif" -> "symbol-status-yellow";
            case "yellow.gif" -> Emojis.getIconClassName("yellow_square");
            default -> {
                if (isJenkinsResource(Jenkins.RESOURCE_PATH + "/images/16x16/" + icon)) {
                    yield Jenkins.RESOURCE_PATH + "/images/16x16/" + icon;
                } else if (isJenkinsResource(Jenkins.RESOURCE_PATH + "/images/svgs/" + icon)) {
                    yield Jenkins.RESOURCE_PATH + "/images/svgs/" + icon;
                } else {
                    LOGGER.log(Level.WARNING, () -> "Icon '" + icon + "' not found as Jenkins resource");
                    yield icon;
                }
            }
        };
    }

    private static boolean isJenkinsResource(String iconPath) {
        try {
            String url = Jenkins.get().getRootUrl() + iconPath;
            HttpURLConnection conn = (HttpURLConnection) new URI(url).toURL().openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            return conn.getResponseCode() == 200;
        } catch (IOException | URISyntaxException ex) {
            LOGGER.log(Level.WARNING, ex, () -> "Unable to validate Jenkins resource '" + iconPath + "'.");
            return false;
        }
    }

    @Whitelisted
    public void setText(String text) {
        this.text = text;
    }

    @Exported
    @Whitelisted
    public String getText() {
        if (text == null || text.isBlank()) {
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

    @Exported
    @Whitelisted
    public String getCssClass() {
        return cssClass;
    }

    @Whitelisted
    public void setStyle(String style) {
        this.style = style;
    }

    @Exported
    @Whitelisted
    public String getStyle() {
        return style;
    }

    @Whitelisted
    public void setLink(String link) {
        this.link = link;
    }

    @Exported
    @Whitelisted
    public String getLink() {
        if (link == null
                || link.isBlank()
                || link.startsWith("/")
                || link.matches("^https?://.*")
                || link.matches("^mailto:.*")) {
            return link;
        }

        LOGGER.log(Level.WARNING, () -> "Invalid link value: '" + link + "' - ignoring it");
        return null;
    }

    @Whitelisted
    public void setTarget(String target) {
        this.target = target;
    }

    @Exported
    @Whitelisted
    public String getTarget() {
        return target;
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
    @Serial
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
                style += "color: " + getJenkinsColorStyle(color) + ";";
            }
        }
        if (!style.isEmpty()) {
            setStyle(style);
        }

        return this;
    }

    /**
     * Get the Jenkins color style for the given color reference. Returns {@code color} if the color is not a
     * known Jenkins palette color or semantic color.
     * @param color color reference
     * @return jenkins color style variable
     */
    @NonNull
    @Restricted(NoExternalUse.class)
    public static String getJenkinsColorStyle(@NonNull String color) {
        String primary = color;
        if (color.startsWith("light-") && color.length() > 6) {
            primary = color.substring(6);
        } else if (color.startsWith("dark-") && color.length() > 5) {
            primary = color.substring(5);
        }
        // spotless:off
        // https://github.com/jenkinsci/jenkins/blob/master/src/main/scss/abstracts/_theme.scss
        return switch (primary) {
            case "blue",
                 "brown",
                 "cyan",
                 "green",
                 "indigo",
                 "orange",
                 "pink",
                 "purple",
                 "red",
                 "yellow",
                 "white",
                 "black" -> "var(--" + color + ")"; // palette
            case "accent",
                 "text",
                 "error",
                 "warning",
                 "success",
                 "destructive",
                 "build",
                 "danger",
                 "info" -> "var(--" + color + "-color)"; // semantics
            default -> color;
        };
        // spotless:on
    }
}
