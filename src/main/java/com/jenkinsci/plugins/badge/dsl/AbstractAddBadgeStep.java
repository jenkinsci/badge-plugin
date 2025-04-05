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
package com.jenkinsci.plugins.badge.dsl;

import com.jenkinsci.plugins.badge.action.AbstractBadgeAction;
import hudson.model.Run;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Abstract class to add badges.
 */
public abstract class AbstractAddBadgeStep extends Step {

    private String id;
    private String icon;
    private String text;
    private String cssClass;
    private String style;
    private String link;
    private String target;

    protected AbstractAddBadgeStep(
            String id, String icon, String text, String cssClass, String style, String link, String target) {
        this.id = id;
        this.icon = icon;
        this.text = text;
        this.cssClass = cssClass;
        this.style = style;
        this.link = link;
        this.target = target;
    }

    @DataBoundSetter
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    @DataBoundSetter
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    @DataBoundSetter
    public void setText(String text) {
        this.text = text;
    }

    public String getCssClass() {
        return cssClass;
    }

    @DataBoundSetter
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getStyle() {
        return style;
    }

    @DataBoundSetter
    public void setStyle(String style) {
        this.style = style;
    }

    public String getLink() {
        return link;
    }

    @DataBoundSetter
    public void setLink(String link) {
        this.link = link;
    }

    public String getTarget() {
        return target;
    }

    @DataBoundSetter
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        List<String> fields = new ArrayList<>();

        if (getId() != null) {
            fields.add("id: '" + getId() + "'");
        }
        if (getIcon() != null) {
            fields.add("icon: '" + getIcon() + "'");
        }
        if (getText() != null) {
            fields.add("text: '" + getText() + "'");
        }
        if (getCssClass() != null) {
            fields.add("cssClass: '" + getCssClass() + "'");
        }
        if (getStyle() != null) {
            fields.add("style: '" + getStyle() + "'");
        }
        if (getLink() != null) {
            fields.add("link: '" + getLink() + "'");
        }
        if (getTarget() != null) {
            fields.add("target: '" + getTarget() + "'");
        }
        return getDescriptor().getFunctionName() + "(" + StringUtils.join(fields, ", ") + ")";
    }

    abstract static class Execution extends SynchronousStepExecution<AbstractBadgeAction> {

        @Serial
        private static final long serialVersionUID = 1L;

        private final String id;
        private final String icon;
        private final String text;
        private final String cssClass;
        private final String style;
        private final String link;
        private final String target;

        Execution(
                String id,
                String icon,
                String text,
                String cssClass,
                String style,
                String link,
                String target,
                StepContext context) {
            super(context);
            this.id = id;
            this.icon = icon;
            this.text = text;
            this.cssClass = cssClass;
            this.style = style;
            this.link = link;
            this.target = target;
        }

        @Override
        protected AbstractBadgeAction run() throws Exception {
            AbstractBadgeAction action = newAction(id, icon, text, cssClass, style, link, target);
            getContext().get(Run.class).addAction(action);
            return action;
        }

        protected abstract AbstractBadgeAction newAction(
                String id, String icon, String text, String cssClass, String style, String link, String target);
    }
}
