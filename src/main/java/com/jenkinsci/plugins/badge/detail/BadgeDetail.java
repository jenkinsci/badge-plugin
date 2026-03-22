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
package com.jenkinsci.plugins.badge.detail;

import com.jenkinsci.plugins.badge.action.BadgeAction;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Run;
import java.util.List;
import jenkins.model.details.Detail;
import jenkins.model.details.DetailFactory;
import jenkins.model.details.DetailGroup;

public class BadgeDetail extends Detail {

    private final List<BadgeAction> actions;

    public BadgeDetail(Run<?, ?> run) {
        super(run);
        actions = run.getActions(BadgeAction.class);
    }

    @NonNull
    public List<BadgeAction> getActions() {
        return actions;
    }

    @Override
    public DetailGroup getGroup() {
        return BadgeDetailGroup.get();
    }

    @Extension(ordinal = -1)
    public static class BadgeDetailGroup extends DetailGroup {

        public static BadgeDetailGroup get() {
            return ExtensionList.lookupSingleton(BadgeDetailGroup.class);
        }
    }

    @Extension
    public static class BadgeDetailFactory extends DetailFactory<Run> {

        @Override
        public Class<Run> type() {
            return Run.class;
        }

        @NonNull
        @Override
        public List<? extends Detail> createFor(@NonNull Run target) {
            return List.of(new BadgeDetail(target));
        }
    }
}
