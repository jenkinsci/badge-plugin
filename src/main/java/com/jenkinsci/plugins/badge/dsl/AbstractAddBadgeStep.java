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

import com.jenkinsci.plugins.badge.action.AbstractBadgeAction;
import hudson.model.Run;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;

/**
 * Abstract class to add badges.
 */
public abstract class AbstractAddBadgeStep extends Step {

    private final String id;
    private String icon;
    private String text;
    private String cssClass;
    private String style;
    private String link;

    protected AbstractAddBadgeStep(String id, String icon, String text, String cssClass, String style, String link) {
        this.id = id;
        this.icon = icon;
        this.text = text;
        this.cssClass = cssClass;
        this.style = style;
        this.link = link;
    }

    protected String getId() {
        return id;
    }

    protected String getIcon() {
        return icon;
    }

    protected void setIcon(String icon) {
        this.icon = icon;
    }

    protected String getText() {
        return text;
    }

    protected void setText(String text) {
        this.text = text;
    }

    protected String getCssClass() {
        return cssClass;
    }

    protected void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    protected String getStyle() {
        return style;
    }

    protected void setStyle(String style) {
        this.style = style;
    }

    protected String getLink() {
        return link;
    }

    protected void setLink(String link) {
        this.link = link;
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

        return getDescriptor().getFunctionName() + "(" + StringUtils.join(fields, ", ") + ")";
    }

    abstract static class Execution extends SynchronousStepExecution<AbstractBadgeAction> {

        private static final long serialVersionUID = 1L;

        private final String id;
        private final String icon;
        private final String text;
        private final String cssClass;
        private final String style;
        private final String link;

        Execution(
                String id, String icon, String text, String cssClass, String style, String link, StepContext context) {
            super(context);
            this.id = id;
            this.icon = icon;
            this.text = text;
            this.cssClass = cssClass;
            this.style = style;
            this.link = link;
        }

        @Override
        protected AbstractBadgeAction run() throws Exception {
            AbstractBadgeAction action = newAction(id, icon, text, cssClass, style, link);
            getContext().get(Run.class).addAction(action);
            return action;
        }

        protected abstract AbstractBadgeAction newAction(
                String id, String icon, String text, String cssClass, String style, String link);
    }
}
