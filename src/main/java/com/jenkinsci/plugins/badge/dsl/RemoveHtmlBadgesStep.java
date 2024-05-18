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

import com.jenkinsci.plugins.badge.action.AbstractAction;
import com.jenkinsci.plugins.badge.action.HtmlBadgeAction;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Removes all html badges or the badges with a given id.
 *
 * @deprecated replaced by {@link RemoveBadgesStep}.
 */
@Deprecated(since = "2.0", forRemoval = true)
public class RemoveHtmlBadgesStep extends Step {

    private String id;

    @DataBoundConstructor
    public RemoveHtmlBadgesStep() {}

    /**
     * @param id Badge identifier. Selectively delete badges by id.
     */
    @DataBoundSetter
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    protected Class<HtmlBadgeAction> getActionClass() {
        return HtmlBadgeAction.class;
    }

    @Extension
    public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

        @Override
        public String getFunctionName() {
            return "removeHtmlBadges";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Remove HTML badges";
        }
    }

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(context, getActionClass(), getId());
    }

    public static class Execution extends SynchronousStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        private final Class<HtmlBadgeAction> actionClass;
        private final String id;

        Execution(StepContext context, Class<HtmlBadgeAction> actionClass, String id) {
            super(context);
            this.actionClass = actionClass;
            this.id = id;
        }

        @Override
        protected Void run() throws IOException, InterruptedException {
            Run<?, ?> run = getContext().get(Run.class);
            if (run != null) {
                run.getAllActions().stream().filter(this::matches).forEach(run::removeAction);
            }

            TaskListener listener = getContext().get(TaskListener.class);
            PrintStream logger = listener.getLogger();
            logger.println("Step 'removeHtmlBadges' is deprecated - please consider using 'removeBadges' instead.");

            return null;
        }

        private boolean matches(Action a) {
            return actionClass.isAssignableFrom(a.getClass())
                    && (id == null || id.equals(((HtmlBadgeAction) a).getId()));
        }
    }
}
