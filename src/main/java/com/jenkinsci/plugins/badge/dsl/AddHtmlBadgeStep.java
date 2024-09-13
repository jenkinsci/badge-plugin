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

import com.jenkinsci.plugins.badge.action.HtmlBadgeAction;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.PrintStream;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Create a html badge.
 *
 * @deprecated replaced by {@link AddBadgeStep}.
 */
@Deprecated(since = "2.0", forRemoval = true)
public class AddHtmlBadgeStep extends Step {

    private String id;
    private final String html;

    @DataBoundConstructor
    public AddHtmlBadgeStep(String html) {
        this.html = html;
    }

    @DataBoundSetter
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getHtml() {
        return html;
    }

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(html, getId(), context);
    }

    @Extension
    @Deprecated(since = "2.0", forRemoval = true)
    public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

        @Override
        public String getFunctionName() {
            return "addHtmlBadge";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Add a HTML Badge";
        }
    }

    @Deprecated(since = "2.0", forRemoval = true)
    public static class Execution extends SynchronousStepExecution<Void> {

        private static final long serialVersionUID = 1L;

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

            TaskListener listener = getContext().get(TaskListener.class);
            PrintStream logger = listener.getLogger();
            logger.println("Step 'addHtmlBadge' is deprecated - please consider using 'addBadge' instead.");

            return null;
        }
    }
}
