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

import com.jenkinsci.plugins.badge.action.HtmlBadgeAction;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Create a short text.
 */
public class AddHtmlBadgeStep extends AbstractStep {

    private final String html;

    /**
     *
     * @param html The html content to be used for this badge
     */
    @DataBoundConstructor
    public AddHtmlBadgeStep(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(html, getId(), context);
    }

    @Extension
    public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

        @Override
        public String getFunctionName() {
            return "addHtmlBadge";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Add a html badge Text";
        }
    }

    public static class Execution extends SynchronousStepExecution<Void> {

        @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
        private final transient String html;

        @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
        private final transient String id;

        Execution(String html, String id, StepContext context) {
            super(context);
            this.html = html;
            this.id = id;
        }

        @Override
        protected Void run() throws Exception {
            HtmlBadgeAction htmlBadge = HtmlBadgeAction.createHtmlBadge(html);
            htmlBadge.setId(id);
            getContext().get(Run.class).addAction(htmlBadge);
            return null;
        }

        private static final long serialVersionUID = 1L;
    }
}
