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
import hudson.model.Action;
import hudson.model.Run;
import java.io.IOException;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;

/**
 * Abstract class to remove badges.
 */
abstract class AbstractRemoveBadgesStep extends Step {

    private final String id;

    protected AbstractRemoveBadgesStep(String id) {
        this.id = id;
    }

    protected String getId() {
        return id;
    }

    protected abstract Class<? extends AbstractBadgeAction> getActionClass();

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(getId(), getActionClass(), context);
    }

    public static class Execution extends SynchronousStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        private final String id;
        private final Class<? extends AbstractBadgeAction> actionClass;

        Execution(String id, Class<? extends AbstractBadgeAction> actionClass, StepContext context) {
            super(context);
            this.id = id;
            this.actionClass = actionClass;
        }

        @Override
        protected Void run() throws IOException, InterruptedException {
            Run<?, ?> run = getContext().get(Run.class);
            if (run != null) {
                run.getAllActions().stream().filter(this::matches).forEach(run::removeAction);
            }
            return null;
        }

        private boolean matches(Action a) {
            return actionClass.isAssignableFrom(a.getClass())
                    && (id == null || id.equals(((AbstractBadgeAction) a).getId()));
        }
    }
}
