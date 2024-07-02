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

import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.PrintStream;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Create a summary text.
 *
 * @deprecated replaced by {@link AddBadgeStep}.
 */
@Deprecated(since = "2.0", forRemoval = true)
public class CreateSummaryStep extends Step {

    private String id;
    private final String icon;
    private String text;

    @DataBoundConstructor
    public CreateSummaryStep(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    @DataBoundSetter
    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    @DataBoundSetter
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(icon, text, getId(), context);
    }

    @Extension
    @Deprecated(since = "2.0", forRemoval = true)
    public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

        @Override
        public String getFunctionName() {
            return "createSummary";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Create Summary";
        }
    }

    @Deprecated(since = "2.0", forRemoval = true)
    public static class Execution extends SynchronousStepExecution<BadgeSummaryAction> {

        private static final long serialVersionUID = 1L;

        private final String id;
        private final String icon;
        private final String text;

        Execution(String icon, String text, String id, StepContext context) {
            super(context);
            this.icon = icon;
            this.text = text;
            this.id = id;
        }

        @Override
        protected BadgeSummaryAction run() throws Exception {
            BadgeSummaryAction action = new BadgeSummaryAction(id, icon, null, null, null, null);
            if (StringUtils.isNotBlank(text)) {
                action.appendText(text);
            }
            getContext().get(Run.class).addAction(action);

            TaskListener listener = getContext().get(TaskListener.class);
            PrintStream logger = listener.getLogger();
            logger.println("Step 'createSummary' is deprecated - please consider using 'addSummary' instead.");

            return action;
        }
    }
}
