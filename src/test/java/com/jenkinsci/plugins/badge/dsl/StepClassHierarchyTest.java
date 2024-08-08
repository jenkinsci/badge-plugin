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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.junit.jupiter.api.Test;

class StepClassHierarchyTest {

    @Test
    void abstractAddBadgeStep() {
        assertTrue(Step.class.isAssignableFrom(AbstractAddBadgeStep.class));
    }

    @Test
    void abstractRemoveBadgesStep() {
        assertTrue(Step.class.isAssignableFrom(AbstractRemoveBadgesStep.class));
    }

    @Test
    void addBadgeStep() {
        assertTrue(Step.class.isAssignableFrom(AddBadgeStep.class));
        assertTrue(AbstractAddBadgeStep.class.isAssignableFrom(AddBadgeStep.class));
    }

    @Test
    void addErrorBadgeStep() {
        assertTrue(Step.class.isAssignableFrom(AddErrorBadgeStep.class));
        assertTrue(AbstractAddBadgeStep.class.isAssignableFrom(AddErrorBadgeStep.class));
        assertTrue(AddBadgeStep.class.isAssignableFrom(AddErrorBadgeStep.class));
    }

    @Test
    void addInfoBadgeStep() {
        assertTrue(Step.class.isAssignableFrom(AddInfoBadgeStep.class));
        assertTrue(AbstractAddBadgeStep.class.isAssignableFrom(AddInfoBadgeStep.class));
        assertTrue(AddBadgeStep.class.isAssignableFrom(AddInfoBadgeStep.class));
    }

    @Test
    void addSummaryStep() {
        assertTrue(Step.class.isAssignableFrom(AddSummaryStep.class));
        assertTrue(AbstractAddBadgeStep.class.isAssignableFrom(AddSummaryStep.class));
        assertTrue(AddBadgeStep.class.isAssignableFrom(AddSummaryStep.class));
    }

    @Test
    void addWarningBadgeStep() {
        assertTrue(Step.class.isAssignableFrom(AddWarningBadgeStep.class));
        assertTrue(AbstractAddBadgeStep.class.isAssignableFrom(AddWarningBadgeStep.class));
        assertTrue(AddBadgeStep.class.isAssignableFrom(AddWarningBadgeStep.class));
    }

    @Test
    void removeBadgesStep() {
        assertTrue(Step.class.isAssignableFrom(RemoveBadgesStep.class));
        assertTrue(AbstractRemoveBadgesStep.class.isAssignableFrom(RemoveBadgesStep.class));
    }

    @Test
    void removeSummariesStep() {
        assertTrue(Step.class.isAssignableFrom(RemoveSummariesStep.class));
        assertTrue(AbstractRemoveBadgesStep.class.isAssignableFrom(RemoveSummariesStep.class));
    }
}
