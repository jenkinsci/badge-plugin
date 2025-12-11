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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StepClassHierarchyTest {

    @Nested
    class Badge {

        @Test
        void abstractAddBadgeStep() {
            assertThat(Step.class.isAssignableFrom(AbstractAddBadgeStep.class), is(true));
        }

        @Test
        void abstractRemoveBadgesStep() {
            assertThat(Step.class.isAssignableFrom(AbstractRemoveBadgesStep.class), is(true));
        }

        @Test
        void addBadgeStep() {
            assertThat(Step.class.isAssignableFrom(AddBadgeStep.class), is(true));
            assertThat(AbstractAddBadgeStep.class.isAssignableFrom(AddBadgeStep.class), is(true));
        }

        @Test
        void addErrorBadgeStep() {
            assertThat(Step.class.isAssignableFrom(AddErrorBadgeStep.class), is(true));
            assertThat(AbstractAddBadgeStep.class.isAssignableFrom(AddErrorBadgeStep.class), is(true));
            assertThat(AddBadgeStep.class.isAssignableFrom(AddErrorBadgeStep.class), is(true));
        }

        @Test
        void addInfoBadgeStep() {
            assertThat(Step.class.isAssignableFrom(AddInfoBadgeStep.class), is(true));
            assertThat(AbstractAddBadgeStep.class.isAssignableFrom(AddInfoBadgeStep.class), is(true));
            assertThat(AddBadgeStep.class.isAssignableFrom(AddInfoBadgeStep.class), is(true));
        }

        @Test
        void addWarningBadgeStep() {
            assertThat(Step.class.isAssignableFrom(AddWarningBadgeStep.class), is(true));
            assertThat(AbstractAddBadgeStep.class.isAssignableFrom(AddWarningBadgeStep.class), is(true));
            assertThat(AddBadgeStep.class.isAssignableFrom(AddWarningBadgeStep.class), is(true));
        }

        @Test
        void removeBadgesStep() {
            assertThat(Step.class.isAssignableFrom(RemoveBadgesStep.class), is(true));
            assertThat(AbstractRemoveBadgesStep.class.isAssignableFrom(RemoveBadgesStep.class), is(true));
        }
    }

    @Nested
    class Summary {

        @Test
        void addSummaryStep() {
            assertThat(Step.class.isAssignableFrom(AddSummaryStep.class), is(true));
            assertThat(AbstractAddBadgeStep.class.isAssignableFrom(AddSummaryStep.class), is(true));
            assertThat(AddBadgeStep.class.isAssignableFrom(AddSummaryStep.class), is(true));
        }

        @Test
        void removeSummariesStep() {
            assertThat(Step.class.isAssignableFrom(RemoveSummariesStep.class), is(true));
            assertThat(AbstractRemoveBadgesStep.class.isAssignableFrom(RemoveSummariesStep.class), is(true));
        }
    }
}
