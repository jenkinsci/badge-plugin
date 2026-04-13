/*
 * The MIT License
 *
 * Copyright (c) 2026, Badge Plugin Authors
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
package com.jenkinsci.plugins.badge.tab;

import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import java.util.Collection;
import java.util.List;
import jenkins.model.Tab;
import jenkins.model.TransientActionFactory;

public class SummaryTab extends Tab {

    private final List<BadgeSummaryAction> actions;

    public SummaryTab(Run<?, ?> run) {
        super(run);
        actions = run.getActions().stream()
                .filter(BadgeSummaryAction.class::isInstance)
                .map(BadgeSummaryAction.class::cast)
                .toList();
    }

    @NonNull
    public List<BadgeSummaryAction> getActions() {
        return actions;
    }

    @Override
    public String getIconFileName() {
        return "symbol-list";
    }

    @Override
    public String getDisplayName() {
        return "Summary";
    }

    @Override
    public String getUrlName() {
        return "summary";
    }

    @Extension
    @SuppressWarnings("unused")
    public static class SummaryTabFactory extends TransientActionFactory<Run> {

        @Override
        public Class<Run> type() {
            return Run.class;
        }

        @NonNull
        @Override
        public Collection<SummaryTab> createFor(@NonNull Run target) {
            SummaryTab tab = new SummaryTab(target);
            if (tab.getActions().isEmpty()) {
                return List.of();
            } else {
                return List.of(tab);
            }
        }
    }
}
