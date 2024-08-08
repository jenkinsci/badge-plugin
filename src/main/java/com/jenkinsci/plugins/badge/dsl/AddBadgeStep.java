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
package com.jenkinsci.plugins.badge.dsl;

import com.jenkinsci.plugins.badge.action.BadgeAction;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Add a badge.
 */
public class AddBadgeStep extends AbstractAddBadgeStep {

    @DataBoundConstructor
    public AddBadgeStep(String id, String icon, String text, String cssClass, String style, String link) {
        super(id, icon, text, cssClass, style, link);
    }

    /**
     * @deprecated replaced by {@link #setStyle(String)}.
     */
    @DataBoundSetter
    @Deprecated(since = "2.0", forRemoval = true)
    public void setColor(String color) {
        // translate old color to new field
        if (color != null) {
            String newStyle = "";
            if (color.startsWith("jenkins-!-color")) {
                newStyle += "color: var(--" + color.replaceFirst("jenkins-!-color-", "") + ");";
            } else if (color.startsWith("jenkins-!-")) {
                newStyle += "color: var(--" + color.replaceFirst("jenkins-!-", "") + ");";
            } else {
                newStyle += "color: " + color + ";";
            }
            setStyle(newStyle + StringUtils.defaultString(getStyle()));
        }
    }

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(getId(), getIcon(), getText(), getCssClass(), getStyle(), getLink(), context) {

            @Override
            protected BadgeAction newAction(
                    String id, String icon, String text, String cssClass, String style, String link) {
                return new BadgeAction(id, icon, text, cssClass, style, link);
            }
        };
    }

    @Extension
    public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

        @Override
        public String getFunctionName() {
            return "addBadge";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Add Badge";
        }
    }
}
