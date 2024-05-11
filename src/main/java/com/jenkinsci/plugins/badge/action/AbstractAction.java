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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.model.Action;
import java.io.Serializable;
import org.kohsuke.stapler.export.Exported;

/**
 * An abstract action providing a badge id
 */
public abstract class AbstractAction implements Action, Serializable {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Exported
    public String getId() {
        return id;
    }

    /**
     * Is the icon reference a Jenkins symbol name.
     * @param icon icon reference
     * @return {@code true} if the icon reference is a Jenkins symbol name
     */
    static boolean isJenkinsSymbolRef(@Nullable String icon) {
        return icon != null && icon.startsWith("symbol-");
    }

    /**
     * Get the Jenkins color class for the given color reference. Returns {@code null} if the color is not a
     * known Jenkins palette color or semantic color.
     * @param color color reference
     * @return jenkins color class name or {@code null}
     */
    @CheckForNull
    static String getJenkinsColorClass(@Nullable String color) {
        if (color == null) {
            return null;
        }

        String primary = color;
        if (color.startsWith("light-") && color.length() > 6) {
            primary = color.substring(6);
        } else if (color.startsWith("dark-") && color.length() > 5) {
            primary = color.substring(5);
        }

        // https://github.com/jenkinsci/jenkins/blob/master/war/src/main/scss/abstracts/_theme.scss
        switch (primary) {
                // palette
            case "blue":
            case "brown":
            case "cyan":
            case "green":
            case "indigo":
            case "orange":
            case "pink":
            case "purple":
            case "red":
            case "yellow":
            case "white":
            case "black":
                return "jenkins-!-color-" + color;
                // semantics
            case "accent":
            case "text":
            case "error":
            case "warning":
            case "destructive":
            case "build":
            case "success":
            case "danger":
            case "info":
                return "jenkins-!-" + color + "-color";
            default:
                return null;
        }
    }
}
