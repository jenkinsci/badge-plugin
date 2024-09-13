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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import java.util.List;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

class AddSummaryStepTest extends AddBadgeStepTest {

    @Override
    protected void assertFields(AbstractAddBadgeStep step, WorkflowRun run) {
        List<BadgeSummaryAction> summaryActions = run.getActions(BadgeSummaryAction.class);
        assertEquals(1, summaryActions.size());

        BadgeSummaryAction action = summaryActions.get(0);
        assertEquals(step.getId(), action.getId());
        assertEquals(step.getIcon(), action.getIcon());
        assertEquals(step.getText(), action.getText());
        assertEquals(step.getCssClass(), action.getCssClass());
        assertEquals(step.getStyle(), action.getStyle());
        assertEquals(step.getLink(), action.getLink());
    }

    @Override
    protected AbstractAddBadgeStep createStep(
            String id, String icon, String text, String cssClass, String style, String link) {
        return new AddSummaryStep(id, icon, text, cssClass, style, link);
    }
}
